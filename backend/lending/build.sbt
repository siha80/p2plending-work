import _root_.sbtprotoc.ProtocPlugin.autoImport.PB
import sbt.Keys._

//sbt clean package assemblyPackageDependency

organization := "com.skplanet"
version := "0.1.0"
name := "lending"

scalaVersion := "2.11.8"
scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers += Resolver.sonatypeRepo("public")
resolvers += "SKP Repository" at "http://mvn.skplanet.com/content/groups/public"

val akkaV = "2.4.11"
val slickV = "3.1.1"

PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
)

// If you need scalapb/scalapb.proto or anything from
// google/protobuf/*.proto
//libraryDependencies += "com.trueaccord.scalapb" %% "scalapb-runtime" % com.trueaccord.scalapb.compiler.Version.scalapbVersion % "protobuf"

libraryDependencies ++= Seq(
    "ch.qos.logback" % "logback-classic" % "1.1.3",
    "commons-codec" % "commons-codec" % "1.10",
    "mysql" % "mysql-connector-java" % "5.1.40",
    "org.scala-lang" % "scala-reflect" % scalaVersion.value,
    "org.json4s" % "json4s-jackson_2.11" % "3.4.2",
    "org.apache.commons" % "commons-dbcp2" % "2.0.1"
) ++ akka(akkaV) ++ slick(slickV) ++ protobuf ++ skplanet

def akka(v: String) = Seq(
  "com.typesafe.akka" %% "akka-actor" % v,
  "com.typesafe.akka" %% "akka-stream" % v,
  "com.typesafe.akka" %% "akka-testkit" % v,
  "com.typesafe.akka" %% "akka-http-spray-json-experimental" % v,
  "com.typesafe.akka" %% "akka-http-experimental" % v,
  "com.typesafe.akka" %% "akka-http-core" % v,
  "com.typesafe.akka" %% "akka-slf4j" % v,
  "com.typesafe.akka" %% "akka-stream-kafka" % "0.13"
)

def slick(v: String) = Seq(
  "com.typesafe.slick" %% "slick" % v,
  "com.typesafe.slick" %% "slick-hikaricp" % v
)

def skplanet = Seq(
  "com.skplanet.syruppay" % "jose_jdk1.5" % "1.3.13"
  //"com.skplanet.querycache" % "querycache-jdbc" % "0.26.6.1-SNAPSHOT"
)

def protobuf = Seq(
  "io.grpc" % "grpc-netty" % "1.0.1",
  "com.trueaccord.scalapb" %% "scalapb-runtime-grpc" % com.trueaccord.scalapb.compiler.Version.scalapbVersion
)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

includeFilter in (Compile, unmanagedResources) := {
  val confFile = sys.props.get("akka.profile") match {
    case Some(profile) => s"application.${profile}.conf"
    case _ => "application.conf"
  }
  confFile || "logback.xml"
}

test in assembly := {}
assemblyJarName in assembly := "p2plending-lending.jar"
mainClass in assembly := Some("com.skp.payment.p2plending.lending.launcher")
