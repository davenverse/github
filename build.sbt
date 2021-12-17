import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

ThisBuild / crossScalaVersions := Seq("2.12.10", "2.13.6", "3.0.2")

val catsV = "2.6.1"
val catsEffectV = "3.2.9"
val fs2V = "3.1.6"
val http4sV = "0.23.0"
val circeV = "0.14.1"
val catsEffectTestingV = "1.3.0"
val log4catsV = "2.1.1"
val logbackClassicV = "1.2.9"

val specs2V = "4.12.3"


lazy val `github` = project.in(file("."))
  .disablePlugins(MimaPlugin)
  .enablePlugins(NoPublishPlugin)
  .aggregate(core, example)

lazy val core = project.in(file("core"))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    name := "github",
    scalacOptions -= "-Xfatal-warnings",
    scalacOptions ++= {
      if (isDotty.value) Seq("-language:postfixOps")
      else Seq()
    },
    mimaVersionCheckExcludedVersions := {
      if (isDotty.value) Set("0.3.0")
      else Set()
    },
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
      
      ("org.specs2"                  %% "specs2-core"                % specs2V       % Test).cross(CrossVersion.for3Use2_13),
      ("org.specs2"                  %% "specs2-scalacheck"          % specs2V       % Test).cross(CrossVersion.for3Use2_13),
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
    "org.http4s"                  %% "http4s-blaze-client"        % http4sV,
    "org.typelevel"           %% "log4cats-slf4j"             % log4catsV,
    "ch.qos.logback"              % "logback-classic"               % logbackClassicV,
    )
  )

lazy val site = project.in(file("site"))
  .disablePlugins(MimaPlugin)
  .enablePlugins(NoPublishPlugin)
  .enablePlugins(DavenverseMicrositePlugin)
  .settings(
    micrositeDescription := "Github Integration for Scala",
  )