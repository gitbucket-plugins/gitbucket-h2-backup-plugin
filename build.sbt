val Organization = "fr.brouillard.gitbucket"
val ProjectName = "gitbucket-h2-backup-plugin"
val ProjectVersion = "1.3.0"

lazy val root = (project in file(".")).enablePlugins(SbtTwirl)

organization := Organization
name := ProjectName
version := ProjectVersion
scalaVersion := "2.11.8"

resolvers ++= Seq(
  Classpaths.typesafeReleases,
  "central" at "http://repo.maven.apache.org/maven2/",
  "amateras" at "http://amateras.sourceforge.jp/mvn/"
)

libraryDependencies ++= Seq(
  "io.github.gitbucket" %% "gitbucket"         % "4.3.0" % "provided",
  "io.github.gitbucket"  % "solidbase"         % "1.0.0" % "provided",
  "com.typesafe.play"   %% "twirl-compiler"    % "1.0.4" % "provided",
  "javax.servlet"        % "javax.servlet-api" % "3.1.0" % "provided"
)

scalacOptions := Seq("-deprecation", "-feature", "-language:postfixOps")
javacOptions in compile ++= Seq("-target", "7", "-source", "7")