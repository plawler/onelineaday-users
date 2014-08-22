name := """onelineaday-users"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

scalacOptions += "-feature"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "com.google.inject" % "guice" % "3.0",
  "javax.inject" % "javax.inject" % "1",
  "com.stormpath.sdk" % "stormpath-sdk-api" % "1.0.RC2",
  "com.stormpath.sdk" % "stormpath-sdk-httpclient" % "1.0.RC2",
  "com.stormpath.sdk" % "stormpath-sdk-oauth" % "1.0.RC2",
  "org.mockito" % "mockito-all" % "1.9.5"
)
