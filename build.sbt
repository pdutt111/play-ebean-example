name := "play-ebean-example"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.8"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)
  
libraryDependencies += jdbc
libraryDependencies += "com.adrianhurt" %% "play-bootstrap" % "1.0-P25-B3"
libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.21"
libraryDependencies += "org.mindrot" % "jbcrypt" % "0.3m"

