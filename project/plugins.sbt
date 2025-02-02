resolvers += "Typesafe repository".at("http://repo.typesafe.com/typesafe/releases/")

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.7.0")

/**
  * Code formatter for scala
  */
// http://scalameta.org/scalafmt/#sbt
addSbtPlugin("com.geirsson" % "sbt-scalafmt" % "1.5.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.18")