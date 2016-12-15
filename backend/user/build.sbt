import _root_.sbtassembly.AssemblyPlugin.autoImport._
import sbt.Keys._

name := "p2plending-backend-user"
scalaVersion := "2.11.8"
scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers += Resolver.sonatypeRepo("public")
resolvers += "SKP Repository" at "http://mvn.skplanet.com/content/groups/public"

libraryDependencies ++= {
  val akkaV = "2.4.11"

  Seq(
    "mysql" % "mysql-connector-java" % "5.1.40",

    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-testkit" % akkaV,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaV,
    "com.typesafe.akka" %% "akka-http-experimental" % akkaV,
    "com.typesafe.akka" %% "akka-http-core" % akkaV,
    "com.typesafe.akka" %% "akka-slf4j" % akkaV,

    "com.typesafe.slick" %% "slick" % "3.1.1",
    "com.typesafe.slick" %% "slick-hikaricp" % "3.1.1",
    "ch.qos.logback" % "logback-classic" % "1.1.3"
  )
}

libraryDependencies += "org.json4s" % "json4s-jackson_2.11" % "3.4.2"
libraryDependencies += "commons-codec" % "commons-codec" % "1.10"
libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value
libraryDependencies += "com.skplanet.syruppay" % "jose_jdk1.5" % "1.3.6"
libraryDependencies += "com.wix" %% "accord-core" % "0.6"
libraryDependencies += "com.typesafe.akka" %% "akka-stream-kafka" % "0.13"
libraryDependencies += "com.skplanet.querycache" % "querycache-jdbc" % "0.26.6.1-SNAPSHOT"
libraryDependencies += "org.apache.commons" % "commons-dbcp2" % "2.0.1"

includeFilter in (Compile, unmanagedResources) := {
  val confFile = sys.props.get("akka.profile") match {
    case Some(profile) => s"application.${profile}.conf"
    case _ => "application.conf"
  }
  confFile || "logback.xml"
}

test in assembly := {}
assemblyJarName in assembly := "p2plending-user.jar"
mainClass in assembly := Some("com.skp.payment.p2plending.user.launcher")
