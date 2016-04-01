name := "cma-es-scala"

organization := "com.sungevity"

version := "1.0.1"

crossScalaVersions := Seq("2.10.6", "2.11.7")

scalacOptions += "-target:jvm-1.7"

javacOptions ++= Seq("-source", "1.7", "-target", "1.7")

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.2.1",
  "joda-time" % "joda-time" % "2.8.1",
  "org.scalanlp" %% "breeze" % "0.12",
  "org.scalanlp" %% "breeze-natives" % "0.12",
  "org.scalanlp" %% "breeze-viz" % "0.12",
  "org.apache.commons" % "commons-lang3" % "3.4",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)