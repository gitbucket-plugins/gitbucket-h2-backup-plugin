organization := "fr.brouillard.gitbucket"
name := "gitbucket-h2-backup-plugin"
version := "1.6.0"
scalaVersion := "2.12.4"
gitbucketVersion := "4.21.0"

resolvers ++= Seq(
  Resolver.jcenterRepo,
  Resolver.mavenLocal
)

libraryDependencies ++= Seq(
  "io.github.gitbucket" %% "gitbucket"         % "4.21.0" % "provided",
  "io.github.gitbucket"  % "solidbase"         % "1.0.2"  % "provided",
  "com.typesafe.play"   %% "twirl-compiler"    % "1.3.13" % "provided",
  "javax.servlet"        % "javax.servlet-api" % "3.1.0"  % "provided"
)

scalacOptions := Seq("-deprecation", "-feature", "-language:postfixOps")
javacOptions in compile ++= Seq("-target", "8", "-source", "8")