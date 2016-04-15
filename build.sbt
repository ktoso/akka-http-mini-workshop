scalaVersion := "2.11.7"

scalacOptions ++= List(
  "-unchecked",
  "-deprecation",
  "-language:_",
  "-encoding", "UTF-8"
)

lazy val commonSettings = Seq(
  organization := "com.typesafe.akka",
  name := "samples.lodz",
  version := "1.0",
  scalaVersion := "2.11.7"
)

commonSettings

libraryDependencies ++= Dependencies.all


fork in run := true
