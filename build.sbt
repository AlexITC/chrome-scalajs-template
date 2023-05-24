import com.alexitc.ChromeSbtPlugin

lazy val appName = "chrome-scalajs-template" // TODO: REPLACE ME
lazy val isProductionBuild = sys.env.getOrElse("PROD", "false") == "true"

Global / onChangedBuildSource := ReloadOnSourceChanges

val circe = "0.14.1"

lazy val baseSettings: Project => Project = {
  _.enablePlugins(ScalaJSPlugin)
    .settings(
      name := appName,
      version := "1.0.0",
      scalaVersion := "2.13.8",
      scalacOptions ++= Seq(
        "-language:implicitConversions",
        "-language:existentials",
        "-Xlint",
        "-deprecation", // Emit warning and location for usages of deprecated APIs.
        "-encoding",
        "utf-8", // Specify character encoding used by source files.
        "-explaintypes", // Explain type errors in more detail.
        "-feature", // Emit warning and location for usages of features that should be imported explicitly.
        "-unchecked" // Enable additional warnings where generated code depends on assumptions.
      ),
      scalacOptions += "-Ymacro-annotations",
      Test / requireJsDomEnv := true
    )
}

lazy val bundlerSettings: Project => Project = {
  _.enablePlugins(ScalaJSBundlerPlugin)
    .settings(
      useYarn := true,
      // NOTE: source maps are disabled to avoid a file not found error which occurs when using the current
      // webpack settings.
      scalaJSLinkerConfig := scalaJSLinkerConfig.value.withSourceMap(false),
      webpack / version := "4.8.1",
      // running `sbt test` fails if the webpack config is specified, it seems to happen because
      // the default webpack config from scalajs-bundler isn't written, making `sbt test` depend on
      // the chromeUnpackedFast task ensures that such config is generated, there might be a better
      // solution but this works for now.
      Test / test := (Test / test).dependsOn(chromeUnpackedFast).value,
      Test / webpackConfigFile := Some(baseDirectory.value / "test.webpack.config.js"),
      webpackConfigFile := {
        val file = if (isProductionBuild) "production.webpack.config.js" else "dev.webpack.config.js"
        Some(baseDirectory.value / file)
      },
      // scala-js-chrome
      scalaJSLinkerConfig := scalaJSLinkerConfig.value.withRelativizeSourceMapBase(
        Some((Compile / fastOptJS / artifactPath).value.toURI)
      ),
      packageJSDependencies / skip := false,
      webpackBundlingMode := BundlingMode.Application,
      fastOptJsLib := (Compile / fastOptJS / webpack).value.head,
      fullOptJsLib := (Compile / fullOptJS / webpack).value.head,
      webpackBundlingMode := BundlingMode.LibraryAndApplication(),
      // you can customize and have a static output name for lib and dependencies
      // instead of having the default files names like extension-fastopt.js, ...
      Compile / fastOptJS / artifactPath := {
        (Compile / fastOptJS / crossTarget).value / "main.js"
      },
      Compile / fullOptJS / artifactPath := {
        (Compile / fullOptJS / crossTarget).value / "main.js"
      }
    )
}

lazy val buildInfoSettings: Project => Project = {
  _.enablePlugins(BuildInfoPlugin)
    .settings(
      buildInfoPackage := "com.alexitc",
      buildInfoKeys := Seq[BuildInfoKey](name),
      buildInfoKeys ++= Seq[BuildInfoKey](
        "production" -> isProductionBuild,

        // it's simpler to propagate the required js scripts from this file to avoid hardcoding
        // them on the code that actually injects them.
        "activeTabWebsiteScripts" -> AppManifest.manifestActiveTabWebsiteScripts
      ),
      buildInfoUsePackageAsPath := true
    )
}

lazy val root = (project in file("."))
  .enablePlugins(ChromeSbtPlugin, ScalablyTypedConverterPlugin)
  .configure(baseSettings, bundlerSettings, buildInfoSettings)
  .settings(
    chromeManifest := AppManifest.generate(appName, Keys.version.value),
    // js dependencies, adding typescript type definitions gets them a Scala facade
    Compile / npmDependencies ++= Seq(
      "sweetalert" -> "2.1.2"
    ),
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "2.1.0",
      "com.alexitc" %%% "scala-js-chrome" % "0.8.1",
      "org.scala-js" %%% "scala-js-macrotask-executor" % "1.0.0",
      "org.scala-js" %%% "scalajs-java-securerandom" % "1.0.0",
      "io.circe" %%% "circe-core" % circe,
      "io.circe" %%% "circe-generic" % circe,
      "io.circe" %%% "circe-parser" % circe,
      "org.scalatest" %%% "scalatest" % "3.2.16" % "test"
    )
  )
