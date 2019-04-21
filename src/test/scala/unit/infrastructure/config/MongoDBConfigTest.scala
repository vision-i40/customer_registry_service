package unit.infrastructure.config

import infrastructure.config.MongoDBConfig
import support.VisionSpec

class MongoDBConfigTest extends VisionSpec {
  private val config = new MongoDBConfig()
  val databaseName = "company_configs_test"

  behavior of "MongoDBConfig"
  it should "return connectionString" in {
    config.connectionString shouldEqual s"""mongodb://localhost/$databaseName"""
  }

  it should "return database" in {
    config.database shouldEqual databaseName
  }
}
