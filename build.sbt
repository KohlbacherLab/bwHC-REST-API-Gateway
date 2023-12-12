import sbt.Keys._


name := "bwhc-rest-api-gateway"
ThisBuild / organization := "de.bwhc"
ThisBuild / scalaVersion := "2.13.8"
ThisBuild / version := "1.1"


scalacOptions ++= Seq(
  "-encoding", "utf8",
  "-unchecked",
  "-language:postfixOps",
  "-Xfatal-warnings",
  "-feature",
  "-deprecation"
)


libraryDependencies ++= Seq(
  guice,
  "org.scalatestplus.play" %% "scalatestplus-play"              % "5.0.0" % "test",

  "de.bwhc"                %% "authentication-api"              % "1.0",
  "de.bwhc"                %% "session-manager-impl"            % "1.0",

   // Fake Data dependencies
  "de.bwhc"                %% "mtb-dto-generators"              % "1.0",
  "de.bwhc"                %% "fhir-mappings"                   % "1.0",

  // User Service dependencies
  "de.bwhc"                %% "user-service-api"                % "1.1",
  "de.bwhc"                %% "user-service-impl"               % "1.1",
  "de.bwhc"                %% "user-service-fs-repos"           % "1.1",

  // Data Entry/Validation Service dependencies
  "de.bwhc"                %% "data-entry-service-api"          % "1.1",
  "de.bwhc"                %% "data-entry-service-impl"         % "1.1",
  "de.bwhc"                %% "data-entry-service-dependencies" % "1.1",

  // Query Service dependencies
  "de.bwhc"                %% "query-service-api"               % "1.1",
  "de.bwhc"                %% "query-service-impl"              % "1.1",
//  "de.bwhc"                %% "bwhc-broker-connector"           % "1.1",
  "de.bwhc"                %% "bwhc-connector"                  % "1.1",
  "de.bwhc"                %% "fs-mtbfile-db"                   % "1.1",

  // Catalog dependencies
  "de.bwhc"                %% "hgnc-impl"                       % "1.0",
  "de.bwhc"                %% "icd-catalogs-impl"               % "1.1",
  "de.bwhc"                %% "medication-catalog-impl"         % "1.1",
)

dependencyOverrides ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % "2.0.0",
  "org.scala-lang.modules" %% "scala-java8-compat" % "1.0.2",
)

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
  )


