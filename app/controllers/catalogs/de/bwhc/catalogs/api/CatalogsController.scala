package de.bwhc.catalogs.api


import java.time.Year
import scala.util.Try
import scala.concurrent.{
  Future,
  ExecutionContext
}
import javax.inject.Inject
import play.api.mvc.{
  Action,
  AnyContent,
  BaseController,
  ControllerComponents,
  Request,
}
import play.api.libs.json.{
  Json,
  JsValue,
  JsObject,
  Format
}
import cats.Functor
import de.bwhc.catalogs.icd.{
  ICD10GM,
  ICD10GMCatalogs,
  ICDO3Catalogs
}
import de.bwhc.catalogs.hgnc.HGNCCatalog
import de.bwhc.catalogs.med.MedicationCatalog
import de.bwhc.user.api.Role
import de.bwhc.mtb.dtos._
import de.bwhc.mtb.query.api.{
  Query,
  VitalStatus
}
import de.bwhc.rest.util.SearchSet
import shapeless.{
  Poly1,
  Generic,
  ::
}
import shapeless.syntax._


object Catalogs
{

  val icd10gm     = ICD10GMCatalogs.getInstance.get
                  
  val icdO3       = ICDO3Catalogs.getInstance.get
                  
  val hgnc        = HGNCCatalog.getInstance.get

  val medications = MedicationCatalog.getInstance.get


  object ToJson extends Poly1
  {

    implicit def anyCase[T](
      implicit js: Format[ValueSet[T]]
    ): Case.Aux[ValueSet[T],JsValue] = 
      at(Json.toJson(_))

  }

  implicit class ProductOps[T <: Product](val t: T) extends AnyVal
  {
    def toHList[L](
      implicit gen: Generic.Aux[T,L]
    ): L = gen.to(t)
  }


  
  implicit val userRolesValueSet =
    ValueSet[Role.Value](
      "Nutzer-Rollen",
      Role.Admin                -> "Admin",
      Role.Documentarist        -> "Dokumentar:in",
      Role.LocalZPMCoordinator  -> "ZPM-Koordinator:in (Lokal)",
      Role.GlobalZPMCoordinator -> "ZPM-Koordinator:in (Global)",
      Role.MTBCoordinator       -> "MTB-Koordinator:in",
      Role.MTBMember            -> "MTB-Mitarbeiter:in",
      Role.Researcher           -> "Forscher:in"
    )


  lazy val jsonValueSets =
    (
     ValueSet[Role.Value] ::
     ValueSet[VitalStatus.Value] ::
     ValueSet[Query.Mode.Value] ::
     ValueSet[Query.DrugUsage.Value] ::
     ValueSets.allValueSets.toHList
    )
    .map(ToJson)
    .toList
    .map(js => (js \ "name").as[String].toLowerCase -> js)
    .toMap

}



class CatalogsController @Inject()(
  val controllerComponents: ControllerComponents
)(
  implicit ec: ExecutionContext
)
extends BaseController
{

  import Catalogs._ 


  def coding(
    system: String,
    pattern: Option[String],
    version: Option[String]
  ): Action[AnyContent] = {

    Action {
      
      val result = 
        system.toLowerCase match {

          case "icd-10-gm" => {
            (
              version match {

                case Some(v) => Try { pattern.fold(icd10gm.codings())(icd10gm.matches(_,v)) }

                case None    => Try { pattern.fold(icd10gm.codings())(icd10gm.matches(_)) }

              }
            )
            .map(SearchSet(_))
            .map(Json.toJson(_))
          }


          case "icd-o-3-t" =>
            Try {
              pattern.fold(icdO3.topographyCodings())(icdO3.topographyMatches(_))
            }
            .map(SearchSet(_))
            .map(Json.toJson(_))

          case "icd-o-3-m" =>
            Try {
              pattern.fold(icdO3.morphologyCodings())(icdO3.morphologyMatches(_))
            }
            .map(SearchSet(_))
            .map(Json.toJson(_))

          case "hgnc" =>
            Try {
              pattern.fold(hgnc.genes)(hgnc.genesMatchingName(_))
            }
            .map(SearchSet(_))
            .map(Json.toJson(_))

          case "atc" =>
            Try {
              version match {
                case Some(v) =>
                  pattern
                    .map(medications.findMatching(_,version))
                    .getOrElse(medications.entries(v))

                // If no version is specified, concatenate all available ATC versions,
                // so that users can selected medications or med. classes whose coding
                // has changed accross ATC versions 
                case None =>
                  medications
                    .availableVersions
                    .sorted(Ordering.by(Year.parse).reverse)
                    .flatMap(
                      v =>
                        pattern
                          .map(medications.findMatching(_,Some(v)))
                          .getOrElse(medications.entries(v))
                    )
                    .distinctBy(_.name)
//                    .distinctBy(_.code)
              }
            }
            .map(SearchSet(_))
            .map(Json.toJson(_))


          case _           => Try { throw new IllegalArgumentException(s"Unknown Coding $system") }

        }

      result.fold(
        t => NotFound(s"Unknown Coding System '$system' or wrong/unavailable version '$version'"),
        Ok(_)
      )

    }

  }



  def valueSets: Action[AnyContent] =
    Action {
      Ok(Json.toJson(SearchSet(jsonValueSets.values)))
    }



  def valueSet(
    name: String
  ): Action[AnyContent] = 
    Action {

     jsonValueSets.get(name.toLowerCase)
       .map(Ok(_))
       .getOrElse(NotFound(s"Unknown ValueSet $name"))

   }


}
