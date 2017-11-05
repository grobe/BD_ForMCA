name := """BD.grobe.fr"""

version := "1.4.3"

lazy val root = (project in file(".")).enablePlugins(BuildInfoPlugin, PlayJava, PlayEbean,DebianPlugin, WindowsPlugin).settings(
  buildInfoKeys := Seq[BuildInfoKey](name, version),
  buildInfoPackage := "info"
)

scalaVersion := "2.11.8"

libraryDependencies += javaJpa
libraryDependencies += "org.mockito" % "mockito-core" % "2.1.0"
libraryDependencies += javaWs % "test"
libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.36"
libraryDependencies += "org.webjars" % "bootstrap" % "3.3.7"


// https://mvnrepository.com/artifact/org.jsoup/jsoup
libraryDependencies += "org.jsoup" % "jsoup" % "1.10.2"

// https://mvnrepository.com/artifact/com.google.zxing/core
libraryDependencies += "com.google.zxing" % "core" % "3.3.0"
// https://mvnrepository.com/artifact/com.google.zxing/javase
libraryDependencies += "com.google.zxing" % "javase" % "3.3.0"

// https://mvnrepository.com/artifact/commons-io/commons-io
libraryDependencies += "commons-io" % "commons-io" % "2.5"



libraryDependencies ++= Seq(
  javaWs
)

// Compile the project before generating Eclipse files, so that generated .scala or .class files for views and routes are present
EclipseKeys.preTasks := Seq(compile in Compile)


//general info for linux package
maintainer in Linux := "Grobe <admin@grobe.fr>"
packageSummary in Linux := "bd.grobe.Fr"
packageDescription := "My longer package description"



// general package information (can be scoped to Windows)
maintainer := "grobe>"
packageSummary := "bd.grobe.fr"
packageDescription := """Manage my own collection"""

// wix build information
//to obtain a new wixProductId -->https://www.guidgen.com/
wixProductId := "0e8a4c89-1e96-4992-9956-a8b2b1110381"
wixProductUpgradeId := "4552fb0e-e257-4dbd-9ecb-dba9dbacf424"

