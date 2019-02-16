import sbt._

object Version {
  final val Circe = "0.11.1"
}

object Library {
  // https://github.com/circe/circe
  // Json library for scala
  final val CirceCore    = "io.circe" %% "circe-core"           % Version.Circe
  final val CirceGeneric = "io.circe" %% "circe-generic"        % Version.Circe
  final val CirceParser  = "io.circe" %% "circe-parser"         % Version.Circe
  final val CirceExtras  = "io.circe" %% "circe-generic-extras" % Version.Circe

  final val Circe = Seq(CirceCore, CirceGeneric, CirceParser)

  // https://github.com/lloydmeta/enumeratum
  // Replacement for scala Enum.
  final val Enumeratum = "com.beachape" %% "enumeratum" % "1.5.13"

  // https://github.com/lloydmeta/enumeratum#circe
  // To use Enumeratum with Circe
  final val EnumeratumCirce = "com.beachape" %% "enumeratum-circe" % "1.5.19"

  // https://github.com/iheartradio/ficus
  // Scala-friendly companion to Typesafe config
  final val Ficus = "com.iheart" %% "ficus" % "1.4.4"

  // https://github.com/codingwell/scala-guice
  // Scala extensions for Google Guice
  // Updating this might fail runtime DI. Test after upgrading.
  final val ScalaGuice = "net.codingwell" %% "scala-guice" % "4.2.1"

  /**
    * ------------------------------------------------------------------------------------------------------------------
    * Play Framework Companions.
    * ------------------------------------------------------------------------------------------------------------------
    */
  // https://github.com/jilen/play-circe
  // Circe for Play
  final val PlayCirce = "com.dripower" %% "play-circe" % "2711.0"

  /**
    * ------------------------------------------------------------------------------------------------------------------
    * Logging
    * ------------------------------------------------------------------------------------------------------------------
    */
  // https://github.com/lightbend/scala-logging
  // Convenient and performant logging in Scala
  final val Logging = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0"

  /**
    * ------------------------------------------------------------------------------------------------------------------
    * Testing
    * ------------------------------------------------------------------------------------------------------------------
    */
  // https://github.com/scalatest/scalatest
  // A testing tool for Scala and Java developers
//  final val ScalaTest = "org.scalatest" %% "scalatest" % "3.0.5" % Test

  final val ScalaTestPlus = "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.1" % Test

  final val Sttp        = "com.softwaremill.sttp" %% "core"              % "1.5.9"
  final val AkkaHTTP    = "com.softwaremill.sttp" %% "akka-http-backend" % "1.5.9"
  final val AkkaStreams = "com.typesafe.akka"     %% "akka-stream"       % "2.5.11"

//  final val PlayWS = "com.typesafe.play" %% "play-ahc-ws-standalone" % "2.0.1"

  // https://github.com/krzemin/octopus
  // Scala library for boilerplate-free validation
  final val Octopus = "com.github.krzemin" %% "octopus" % "0.3.3"

  final val CommonsValidator = "commons-validator" % "commons-validator" % "1.6"
}
