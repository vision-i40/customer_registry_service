name := "customer_registry_service"
version := sys.env.getOrElse("GIT_COMMIT", "local")

scalaVersion := "2.11.11"

lazy val UnitTestConf = config("unit").extend(Test)
lazy val IntegrationTestConf = config("integration").extend(Test)

lazy val versions = new {
  val finatra = "19.5.1"
  val mongoDriver = "2.4.2"
  val logback = "1.2.3"
}

parallelExecution := false

libraryDependencies ++= Seq(
  "com.twitter" %% "finatra-http" % versions.finatra,
  "com.twitter.inject" %% "inject-request-scope" % "2.1.6",
  "ch.qos.logback" % "logback-classic" % versions.logback % Runtime,
  "org.apache.logging.log4j" % "log4j-core" % "2.7",
  "org.mongodb.scala" %% "mongo-scala-driver" % versions.mongoDriver,
  "com.typesafe" % "config" % "1.3.2",
  "com.pauldijou" %% "jwt-core" % "2.1.0",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.0.2",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "com.github.stevenchen3" %% "scala-faker" % "0.1.1" % Test,
  "org.scalatest" %% "scalatest" % "3.0.5" % Test,
  "org.mockito" % "mockito-all" % "1.9.5" % Test
)

lazy val root = (project in file("."))
  .configs(IntegrationTestConf, UnitTestConf)
  .settings(inConfig(UnitTestConf)(Defaults.testTasks): _*)
  .settings(inConfig(IntegrationTestConf)(Defaults.testTasks): _*)
  .settings(
    mainClass in Compile := Some("Main")
  )

dockerExposedPorts ++= Seq(8888, 9990)
enablePlugins(JavaServerAppPackaging)

lazy val unit = TaskKey[Unit]("unit", "Runs all Unit Tests.")
lazy val integration = TaskKey[Unit]("integration", "Runs all Integration Tests.")

unit := (test in UnitTestConf).value
integration := (test in IntegrationTestConf).value

testOptions in UnitTestConf := Seq(Tests.Filter(testPackageName => testPackageName.startsWith("unit")))

testOptions in IntegrationTestConf := Seq(Tests.Filter(testPackageName => testPackageName.startsWith("integration")))
