package de.bwhc.catalogs.api



import javax.inject.Inject

import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._
import play.api.mvc.Results.Ok

import play.api.libs.json.Json.toJson
import de.bwhc.rest.util.sapphyre.playjson._


class Router @Inject()(
  catalogs: CatalogsController,
)
extends SimpleRouter
{

  override def routes: Routes = {

    //-------------------------------------------------------------------------
    // Catalogs / ValueSets
    //-------------------------------------------------------------------------
    
    case GET(p"/") =>
      catalogs.Action { Ok(toJson(CatalogHypermedia.ApiResource)) }
  

    case GET(p"/Coding/$system"?q_o"pattern=$pattern"?q_o"version=$v") =>
      catalogs.coding(system,pattern,v)


    case GET(p"/Coding"?q"system=$system"?q_o"pattern=$pattern"?q_o"version=$v") =>
      catalogs.coding(system,pattern,v)


    case GET(p"/ValueSet/$name")           => catalogs.valueSet(name)
    case GET(p"/ValueSet"?q"name=$name")   => catalogs.valueSet(name)
    case GET(p"/ValueSet")                 => catalogs.valueSets

  }

}
