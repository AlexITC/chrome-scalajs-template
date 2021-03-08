import com.alexitc.ChromeSbtPlugin

lazy val appName = "chrome-scalajs-template" // TODO: REPLACE ME
lazy val isProductionBuild = sys.env.getOrElse("PROD", "false") == "true"

val circe = "0.13.0"

lazy val baseSettings: Project => Project = {
  _.enablePlugins(ScalaJSPlugin)
    .settings(
      name := appName,
      version := "1.0.0",
      scalaVersion := "2.13.3",
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
      requireJsDomEnv in Test := true
    )
}

lazy val bundlerSettings: Project => Project = {
  _.enablePlugins(ScalaJSBundlerPlugin)
    .settings(
      // NOTE: source maps are disabled to avoid a file not found error which occurs when using the current
      // webpack settings.
      scalaJSLinkerConfig := scalaJSLinkerConfig.value.withSourceMap(false),
      version in webpack := "4.8.1",
      // running `sbt test` fails if the webpack config is specified, it seems to happen because
      // the default webpack config from scalajs-bundler isn't written, making `sbt test` depend on
      // the chromeUnpackedFast task ensures that such config is generated, there might be a better
      // solution but this works for now.
      Test / test := (Test / test).dependsOn(chromeUnpackedFast).value,
      webpackConfigFile in Test := Some(baseDirectory.value / "test.webpack.config.js"),
      webpackConfigFile := {
        val file = if (isProductionBuild) "production.webpack.config.js" else "dev.webpack.config.js"
        Some(baseDirectory.value / file)
      },
      // scala-js-chrome
      scalaJSLinkerConfig := scalaJSLinkerConfig.value.withRelativizeSourceMapBase(
        Some((Compile / fastOptJS / artifactPath).value.toURI)
      ),
      skip in packageJSDependencies := false,
      webpackBundlingMode := BundlingMode.Application,
      fastOptJsLib := (webpack in (Compile, fastOptJS)).value.head,
      fullOptJsLib := (webpack in (Compile, fullOptJS)).value.head,
      webpackBundlingMode := BundlingMode.LibraryAndApplication(),
      // you can customize and have a static output name for lib and dependencies
      // instead of having the default files names like extension-fastopt.js, ...
      artifactPath in (Compile, fastOptJS) := {
        (crossTarget in (Compile, fastOptJS)).value / "main.js"
      },
      artifactPath in (Compile, fullOptJS) := {
        (crossTarget in (Compile, fullOptJS)).value / "main.js"
      }
    )
}

lazy val buildInfoSettings: Project => Project = {
  _.enablePlugins(BuildInfoPlugin)
    .settings(
      buildInfoPackage := "com.alexitc",
      buildInfoKeys := Seq[BuildInfoKey](name),
      buildInfoKeys ++= Seq[BuildInfoKey](
        "production" -> isProductionBuild
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
      "org.scala-js" %%% "scalajs-dom" % "1.1.0",
      "com.alexitc" %%% "scala-js-chrome" % "0.7.0",
      "io.circe" %%% "circe-core" % circe,
      "io.circe" %%% "circe-generic" % circe,
      "io.circe" %%% "circe-parser" % circe,
      "org.scalatest" %%% "scalatest" % "3.2.6" % "test"
    )
  )
