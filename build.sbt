val ScalatraVersion = "2.7.1"

organization := "fr.brouillard.gitbucket"
name := "gitbucket-h2-backup-plugin"
version := "1.9.0"
scalaVersion := "2.13.5"
gitbucketVersion := "4.35.3"
scalacOptions += "-deprecation"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest-funspec"  % "3.2.3" % "test",
  "org.scalatest" %% "scalatest-funsuite" % "3.2.3" % "test",
  "org.scalatra"  %% "scalatra-scalatest" % ScalatraVersion % "test",
)
