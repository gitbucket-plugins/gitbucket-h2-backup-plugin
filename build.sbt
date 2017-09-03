val Organization = "fr.brouillard.gitbucket"
val ProjectName = "gitbucket-h2-backup-plugin"
val ProjectVersion = "1.5.1"

lazy val h2_backup_plugin = (project in file(".")).enablePlugins(SbtTwirl)

organization := Organization
name := ProjectName
version := ProjectVersion
scalaVersion := "2.12.3"


libraryDependencies ++= Seq(
  "io.github.gitbucket" %% "gitbucket"         % "4.16.0" % "provided",
  "io.github.gitbucket"  % "solidbase"         % "1.0.2" % "provided",
  "com.typesafe.play"   %% "twirl-compiler"    % "1.3.0" % "provided",
  "javax.servlet"        % "javax.servlet-api" % "3.1.0" % "provided"
)

scalacOptions := Seq("-deprecation", "-feature", "-language:postfixOps")
javacOptions in compile ++= Seq("-target", "8", "-source", "8")

useJCenter := true