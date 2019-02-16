resolvers += "Typesafe repository".at("http://repo.typesafe.com/typesafe/releases/")

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.7.0")

/**
  * Scala Static Analysis Tools
  */
// http://www.scalastyle.org/
addSbtPlugin("com.sksamuel.scapegoat" %% "sbt-scapegoat" % "1.0.7")

addSbtPlugin("org.wartremover" % "sbt-wartremover" % "2.3.7")

/**
  * Code formatter for scala
  */
// http://scalameta.org/scalafmt/#sbt
addSbtPlugin("com.geirsson" % "sbt-scalafmt" % "1.5.1")

/**
  * Scala Code Coverage
  */
// https://github.com/scoverage/sbt-scoverage
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")
