name := "customer_registry_service"
version := "0.1"
scalaVersion := "2.12.8"

lazy val UnitTestConf = config("unit").extend(Test)
lazy val IntegrationTestConf = config("integration").extend(Test)

lazy val versions = new {
  val finatra = "19.3.0"
  val finaglePostgres = "0.10.0"
  val logback = "1.2.3"
}

libraryDependencies ++= Seq(
  "com.twitter" %% "finatra-http" % versions.finatra,
  "ch.qos.logback" % "logback-classic" % versions.logback,
  "io.github.finagle" %% "finagle-postgres" % versions.finaglePostgres,
  "com.typesafe" % "config" % "1.3.2",
  "com.pauldijou" %% "jwt-core" % "2.1.0"
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
