import sbt._
import Keys._
import play.twirl.sbt.SbtTwirl
import play.twirl.sbt.Import.TwirlKeys._

object MyBuild extends Build {

  val Organization = "fr.brouillard.gitbucket"
  val Name = "gitbucket-h2-backup-plugin"
  val Version = "1.0"
  val ScalaVersion = "2.11.6"

  lazy val project = Project (
    "gitbucket-h2-backup-plugin",
    file(".")
  )
  .settings(
    sourcesInBase := false,
    organization := Organization,
    name := Name,
      version := Version,
    scalaVersion := ScalaVersion,
    scalacOptions := Seq("-deprecation", "-language:postfixOps"),
    resolvers ++= Seq(
      "amateras-repo" at "http://amateras.sourceforge.jp/mvn/"
    ),
    libraryDependencies ++= Seq(
      "gitbucket"          % "gitbucket-assembly" % "3.5.0" % "provided",
      "com.typesafe.play" %% "twirl-compiler"     % "1.0.4" % "provided",
      "javax.servlet"      % "javax.servlet-api"  % "3.1.0" % "provided"
    ),
    javacOptions in compile ++= Seq("-target", "7", "-source", "7")
  ).enablePlugins(SbtTwirl)
}
