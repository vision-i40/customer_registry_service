package unit.infrastructure.config

import infrastructure.config.MongoDBConfig
import support.VisionSpec

class MongoDBConfigTest extends VisionSpec {
  private val config: MongoDBConfig.type = MongoDBConfig

  behavior of "MongoDBConfig"
  it should "return connectionString" in {
    config.connectionString shouldEqual """mongodb://mongodb:mongodb@localhost:27017/admin"""
  }

  it should "return database" in {
    config.database shouldEqual "company_configs_test"
  }

  it should "return password" in {
    config.password shouldEqual "mongodb"
  }

  it should "return username" in {
    config.username shouldEqual "mongodb"
  }

  it should "return host" in {
    config.host shouldEqual "localhost"
  }

  it should "return port" in {
    config.port shouldEqual 27017
  }
}
