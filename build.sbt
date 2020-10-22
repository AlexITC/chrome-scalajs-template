import chrome._
import chrome.permissions.Permission
import chrome.permissions.Permission.API
import com.alexitc.{Chrome, ChromeSbtPlugin}

resolvers += Resolver.sonatypeRepo("releases")
resolvers += Resolver.bintrayRepo("oyvindberg", "ScalablyTyped")
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

name := "chrome-scalajs-template" // TODO: REPLACE ME
version := "1.0.0"
scalaVersion := "2.12.10"
scalacOptions ++= Seq(
  "-language:implicitConversions",
  "-language:existentials",
  "-Xlint",
  "-deprecation",
  //"-Xfatal-warnings",
  "-feature"
)

enablePlugins(ChromeSbtPlugin, BuildInfoPlugin, ScalaJSBundlerPlugin, ScalablyTypedConverterPlugin)

lazy val isProductionBuild = sys.env.getOrElse("PROD", "false") == "true"

// build-info
buildInfoPackage := "com.alexitc"
buildInfoKeys := Seq[BuildInfoKey](name)
buildInfoKeys ++= Seq[BuildInfoKey](
  "production" -> (sys.env.getOrElse("PROD", "false") == "true")
)

// NOTE: source maps are disabled to avoid a file not found error which occurs when using the current
// webpack settings.
scalaJSLinkerConfig := scalaJSLinkerConfig.value.withSourceMap(false)
version in webpack := "4.8.1"

webpackConfigFile := {
  val file = if (isProductionBuild) "production.webpack.config.js" else "dev.webpack.config.js"
  Some(baseDirectory.value / file)
}

// scala-js-chrome
scalaJSLinkerConfig := scalaJSLinkerConfig.value.withRelativizeSourceMapBase(
  Some((Compile / fastOptJS / artifactPath).value.toURI)
)
skip in packageJSDependencies := false

webpackBundlingMode := BundlingMode.Application

fastOptJsLib := (webpack in (Compile, fastOptJS)).value.head
fullOptJsLib := (webpack in (Compile, fullOptJS)).value.head

webpackBundlingMode := BundlingMode.LibraryAndApplication()

// you can customize and have a static output name for lib and dependencies
// instead of having the default files names like extension-fastopt.js, ...
artifactPath in (Compile, fastOptJS) := {
  (crossTarget in (Compile, fastOptJS)).value / "main.js"
}

artifactPath in (Compile, fullOptJS) := {
  (crossTarget in (Compile, fullOptJS)).value / "main.js"
}

chromeManifest := new ExtensionManifest {
  override val name = "__MSG_extensionName__" // NOTE: i18n on the manifest is not supported on firefox
  override val version = Keys.version.value

  override val description = Some(
    "TO BE UPDATED" // TODO: REPLACE ME
  )
  override val icons = Chrome.icons("icons", "app.png", Set(48, 96, 128))

  // TODO: REPLACE ME, use only the minimum required permissions
  override val permissions = Set[Permission](
    API.Storage,
    API.Notifications,
    API.Alarms
  )

  override val defaultLocale: Option[String] = Some("en")

  // TODO: REPLACE ME
  override val browserAction: Option[BrowserAction] =
    Some(BrowserAction(icons, Some("TO BE DEFINED - POPUP TITLE"), Some("popup.html")))

  // scripts used on all modules
  val commonScripts = List("scripts/common.js", "main-bundle.js")

  override val background = Background(
    scripts = commonScripts ::: List("scripts/background-script.js")
  )

  override val contentScripts: List[ContentScript] = List(
    ContentScript(
      matches = List(
        "https://github.com/*" // TODO: REPLACE ME
      ),
      css = List("css/active-tab.css"),
      js = commonScripts ::: List("scripts/active-tab-script.js")
    )
  )

  override val webAccessibleResources = List("icons/*")
}

val circe = "0.13.0"

libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "1.1.0"
libraryDependencies += "com.alexitc" %%% "scala-js-chrome" % "0.7.0"

libraryDependencies += "io.circe" %%% "circe-core" % circe
libraryDependencies += "io.circe" %%% "circe-generic" % circe
libraryDependencies += "io.circe" %%% "circe-parser" % circe

// js dependencies, adding typescript type definitions gets them a Scala facade
npmDependencies in Compile ++= Seq(
  "sweetalert" -> "2.1.2"
)
