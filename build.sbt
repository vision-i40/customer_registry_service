name := "customer_registry_service"
version := "0.1"
scalaVersion := "2.12.8"

lazy val UnitTestConf = config("unit").extend(Test)
lazy val IntegrationTestConf = config("integration").extend(Test)

lazy val versions = new {
  val finatra = "19.3.0"
  val mongoDriver = "2.4.2"
  val logback = "1.2.3"
}

libraryDependencies ++= Seq(
  "com.twitter" %% "finatra-http" % versions.finatra,
  "ch.qos.logback" % "logback-classic" % versions.logback,
  "org.mongodb.scala" %% "mongo-scala-driver" % versions.mongoDriver,
  "com.typesafe" % "config" % "1.3.2",
  "com.pauldijou" %% "jwt-core" % "2.1.0",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "com.github.stevenchen3" %% "scala-faker" % "0.1.1" % Test,
  "org.scalatest" %% "scalatest" % "3.0.5" % Test,
  "org.mockito" % "mockito-all" % "1.9.5" % Test,

)

lazy val root = (project in file("."))
  .configs(IntegrationTestConf, UnitTestConf)
  .settings(inConfig(UnitTestConf)(Defaults.testTasks): _*)
  .settings(inConfig(IntegrationTestConf)(Defaults.testTasks): _*)
  .settings(
    mainClass in Compile := Some("Main")
  )

lazy val unit = TaskKey[Unit]("unit", "Runs all Unit Tests.")
lazy val integration = TaskKey[Unit]("integration", "Runs all Integration Tests.")

unit := (test in UnitTestConf).value
integration := (test in IntegrationTestConf).value

testOptions in UnitTestConf := Seq(Tests.Filter(testPackageName => testPackageName.startsWith("unit")))

testOptions in IntegrationTestConf := Seq(Tests.Filter(testPackageName => testPackageName.startsWith("integration")))
