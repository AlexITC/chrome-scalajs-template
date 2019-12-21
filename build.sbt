import chrome._
import chrome.permissions.Permission
import chrome.permissions.Permission.API
import net.lullabyte.{Chrome, ChromeSbtPlugin}

resolvers += Resolver.sonatypeRepo("releases")

name := "chrome-scalajs-template"
version := "1.0.0"
scalaVersion := "2.12.10"
scalacOptions ++= Seq(
  "-language:implicitConversions",
  "-language:existentials",
  "-Xlint",
  "-deprecation",
  //"-Xfatal-warnings",
  "-feature",
  "-P:scalajs:sjsDefinedByDefault"
)

enablePlugins(ChromeSbtPlugin, BuildInfoPlugin)

// build-info
buildInfoPackage := "com.alexitc"
buildInfoKeys := Seq[BuildInfoKey](name)
buildInfoKeys ++= Seq[BuildInfoKey](
  "production" -> (sys.env.getOrElse("PROD", "false") == "true")
)

// scala-js-chrome
scalaJSUseMainModuleInitializer := true
scalaJSUseMainModuleInitializer in Test := false
relativeSourceMaps := true
skip in packageJSDependencies := false

// you can customize and have a static output name for lib and dependencies
// instead of having the default files names like extension-fastopt.js, ...
artifactPath in (Compile, fastOptJS) := {
  (crossTarget in fastOptJS).value / "main.js"
}

artifactPath in (Compile, fullOptJS) := {
  (crossTarget in fullOptJS).value / "main.js"
}

artifactPath in (Compile, packageJSDependencies) := {
  (crossTarget in packageJSDependencies).value / "dependencies.js"
}

artifactPath in (Compile, packageMinifiedJSDependencies) := {
  (crossTarget in packageMinifiedJSDependencies).value / "dependencies.js"
}

chromeManifest := new ExtensionManifest {
  override val name = "__MSG_extensionName__" // NOTE: i18n on the manifest is not supported on firefox
  override val version = Keys.version.value
  override val description = Some(
    "TO BE UPDATED"
  )
  override val icons = Chrome.icons("icons", "app.png", Set(48, 96, 128))

  override val permissions =
    Set[Permission](
      API.Storage,
      API.Notifications,
      API.Alarms
    )

  override val defaultLocale: Option[String] = Some("en")

  override val browserAction: Option[BrowserAction] =
    Some(BrowserAction(icons, Some("TO BE DEFINED - POPUP TITLE"), Some("popup.html")))

  // scripts used on all modules
  val commonScripts = List("scripts/common.js", "dependencies.js", "main.js")

  override val background =
    Background(
      scripts = commonScripts ::: List("scripts/background-script.js")
    )

  override val contentScripts: List[ContentScript] =
    List(
      ContentScript(
        matches = List(
          "https://github.com/*"
        ),
        css = List("css/active-tab.css"),
        js = commonScripts ::: List("scripts/active-tab-script.js")
      )
    )

  override val webAccessibleResources = List("icons/*")
}

val circe = "0.11.1"
val sttp = "1.7.0"

libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.7"
libraryDependencies += "net.lullabyte" %%% "scala-js-chrome" % "1b6d0d9cbeb95a23d5ecd4ba0defd6f1373fae1b"

libraryDependencies += "io.circe" %%% "circe-core" % circe
libraryDependencies += "io.circe" %%% "circe-generic" % circe
libraryDependencies += "io.circe" %%% "circe-parser" % circe

libraryDependencies += "com.softwaremill.sttp" %%% "core" % sttp

addCompilerPlugin(
  "org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full
)
