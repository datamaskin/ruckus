import play.Play.autoImport._
import PlayKeys._

//offline := true

organization  := "RuckusGaming"

name := "ruckus"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.1"

lazy val root = (project in file(".")).enablePlugins(PlayJava, SbtWeb)

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

// Allows for debugging of tests in IntelliJ
Keys.fork in (Test) := false

resolvers ++= Seq(
  //DefaultMavenRepository,
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
  //MavenRepository("Nexus Repo", "http://nexus.ruckusgaming.com/nexus/content/repositories/snapshots/"),
)

libraryDependencies ++= Seq(
  javaCore,
  javaJdbc,
  javaEbean,
  "net.sourceforge.nekohtml" % "nekohtml" % "1.9.10",
  "mysql" % "mysql-connector-java" % "5.1.18",
  "org.atmosphere" % "atmosphere-runtime-native" % "2.2.0",
  "org.atmosphere" % "atmosphere-play" % "2.0.0",
  "org.apache.commons" % "commons-pool2" % "2.2",
  "org.seleniumhq.selenium" % "selenium-java" % "2.41.0",
  "org.apache.httpcomponents" % "httpcore" % "4.3.2",
  "com.hazelcast" % "hazelcast-all" % "3.2.5",
  "ws.securesocial" %% "securesocial" % "3.0-M1",
  "org.springframework" % "spring-context" % "4.0.5.RELEASE",
  "org.springframework" % "spring-context-support" % "4.0.5.RELEASE",
  "com.amazonaws" % "aws-java-sdk" % "1.8.7",
  "org.springframework" % "spring-test" % "4.0.5.RELEASE" % "test",
  "org.easymock" % "easymock" % "3.2" % "test",
  "com.novocode" % "junit-interface" % "0.10" % "test",
  "org.webjars" % "dustjs-linkedin" % "2.4.0-1"
)

mappings in Universal ++=
  (baseDirectory.value / ".ebextensions" * "*" get) map
    (x => x -> (".ebextensions/" + x.getName))

//HACK: to prevent modifying static assets from reloading play
//watchSources := (watchSources.value
//  --- baseDirectory.value / "app/assets" ** "*"
//  --- baseDirectory.value / "public"     ** "*").get


publishTo := {
  val nexus = "http://nexus.ruckusgaming.com/"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "nexus/content/repositories/snapshots")
  else
    Some("releases"  at nexus + "nexus/content/repositories/releases")
}
