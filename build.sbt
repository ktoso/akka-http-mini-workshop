scalaVersion := "2.11.7"

scalacOptions ++= List(
  "-unchecked",
  "-deprecation",
  "-language:_",
  "-target:jvm-1.6",
  "-encoding", "UTF-8"
)

lazy val commonSettings = Seq(
  organization := "com.typesafe.akka",
  name := "samples.scalaexchange",
  version := "1.0",
  scalaVersion := "2.11.7"
)

commonSettings

libraryDependencies ++= Dependencies.all


fork in run := true