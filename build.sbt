name := "trains_reborning"

version := "0.1"

scalaVersion := "2.13.8"

val scalaTestVer = "3.2.12"
val fs2Ver = "2.5.3"
val circeVer = "0.14.1"
val catsVer = "2.5.3"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-effect" % catsVer,
  "org.scalactic" %% "scalactic" % scalaTestVer,
  "org.scalatest" %% "scalatest" % scalaTestVer % "test"
) ++ Seq(
  "co.fs2" %% "fs2-core",
  "co.fs2" %% "fs2-io"
).map(_ % fs2Ver) ++ Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVer)