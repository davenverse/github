ThisBuild / tlBaseVersion := "0.4"

ThisBuild / organization := "io.chrisdavenport"
ThisBuild / organizationName := "Christopher Davenport"
ThisBuild / licenses := Seq(License.MIT)
ThisBuild / developers := List(
  // your GitHub handle and name
  tlGitHubDev("christopherdavenport", "Christopher Davenport")
)

ThisBuild / tlCiReleaseBranches := Seq("main")

ThisBuild / scalaVersion := "2.13.8"
ThisBuild / versionScheme := Some("early-semver")
ThisBuild / crossScalaVersions := Seq("2.12.15", "2.13.8", "3.2.1")

val catsV = "2.8.0"
val catsEffectV = "3.4.2"
val fs2V = "3.8.0"
val http4sV = "0.23.16"
val circeV = "0.14.3"
val catsEffectTestingV = "1.5.0"
val log4catsV = "2.5.0"
val logbackClassicV = "1.2.11"

lazy val `github` = tlCrossRootProject
  .aggregate(core, example)

lazy val core = project.in(file("core"))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    name := "github",
    scalacOptions -= "-Xfatal-warnings",
    buildInfoKeys := Seq[BuildInfoKey](version),
    buildInfoPackage := "io.chrisdavenport.github",
    libraryDependencies ++= Seq(
      "org.typelevel"               %% "cats-core"                  % catsV,
      "org.typelevel"               %% "cats-effect"                % catsEffectV,
      "co.fs2"                      %% "fs2-core"                   % fs2V,
      "co.fs2"                      %% "fs2-io"                     % fs2V,

      "org.http4s"                  %% "http4s-client"              % http4sV,
      "org.http4s"                  %% "http4s-circe"               % http4sV,
      "org.http4s"                  %% "http4s-dsl"                 % http4sV     % Test,

      "io.circe"                    %% "circe-core"                 % circeV,
      // "io.circe"                    %% "circe-literal"              % circeV      % Test,
      "io.circe"                    %% "circe-parser"               % circeV      % Test,

      "org.typelevel"              %% "cats-effect-testing-specs2" % catsEffectTestingV  % Test
    )
  )

lazy val example = project.in(file("example"))
  .disablePlugins(MimaPlugin)
  .enablePlugins(NoPublishPlugin)
  .dependsOn(core)
  .settings(
    libraryDependencies ++= Seq(
      // For Testing As I go
    "org.http4s"                  %% "http4s-ember-client"        % http4sV,
    "org.typelevel"           %% "log4cats-slf4j"             % log4catsV,
    "ch.qos.logback"              % "logback-classic"               % logbackClassicV,
    )
  )

lazy val site = project.in(file("site"))
  .enablePlugins(TypelevelSitePlugin)
  .dependsOn(core)