name := """BD.grobe.fr"""

version := "Release-V0.3"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean,DebianPlugin)

scalaVersion := "2.11.8"

libraryDependencies += javaJpa
libraryDependencies += "org.mockito" % "mockito-core" % "2.1.0"
libraryDependencies += javaWs % "test"
libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.36"

// https://mvnrepository.com/artifact/org.jsoup/jsoup
libraryDependencies += "org.jsoup" % "jsoup" % "1.10.2"

// https://mvnrepository.com/artifact/com.google.zxing/core
libraryDependencies += "com.google.zxing" % "core" % "3.3.0"
// https://mvnrepository.com/artifact/com.google.zxing/javase
libraryDependencies += "com.google.zxing" % "javase" % "3.3.0"


libraryDependencies ++= Seq(
  javaWs
)

// Compile the project before generating Eclipse files, so that generated .scala or .class files for views and routes are present
EclipseKeys.preTasks := Seq(compile in Compile)



maintainer in Linux := "Grobe <admin@grobe.fr>"

packageSummary in Linux := "bd.grobe.Fr"

packageDescription := "My longer package description"