name := """BD_ForMCA"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.8"

libraryDependencies += javaJpa
libraryDependencies += "org.mockito" % "mockito-core" % "2.1.0"
libraryDependencies += javaWs % "test"
libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.36"
libraryDependencies ++= Seq(
  javaWs
)

// Compile the project before generating Eclipse files, so that generated .scala or .class files for views and routes are present
EclipseKeys.preTasks := Seq(compile in Compile)
