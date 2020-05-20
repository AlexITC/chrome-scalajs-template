resolvers += Resolver.bintrayRepo("oyvindberg", "converter")
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

addSbtPlugin("org.scalablytyped.converter" % "sbt-converter" % "1.0.0-beta12")
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.0.1")
addSbtPlugin("com.alexitc" % "sbt-chrome-plugin" % "0.7.0")
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.9.0")
addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.18.0")
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.0.0")
