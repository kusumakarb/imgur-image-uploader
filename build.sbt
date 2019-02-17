import sbt.Keys.libraryDependencies
import sbt._

name := """imgur-image-uploader"""
organization := "example.com"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.8"

// sbt-scapegoat
scapegoatVersion.in(ThisBuild) := "1.3.8"
scapegoatIgnoredFiles := Seq(".*/routes")

// sbt-scoverage
coverageHighlighting := true
coverageEnabled := true

coverageEnabled.in(Test, test) := true

resolvers +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies ++= Seq(
  guice,
  ehcache,
  ws,
  Library.Enumeratum,
  Library.EnumeratumCirce,
  Library.Octopus,
  Library.CommonsValidator,
  Library.Ficus,
  Library.PlayCirce,
  Library.ScalaTestPlus,
  Library.PlayWSMock,
  Library.Logging
) ++ Library.Circe

enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)

dockerBaseImage := "openjdk:jre"

// Work around https://stackoverflow.com/a/29244028/3856808
javaOptions in Universal ++= Seq(
  "-Dpidfile.path=/dev/null"
)

dockerExposedPorts ++= Seq(9000, 9443)
