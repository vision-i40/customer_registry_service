package infrastructure.config

import com.typesafe.config.{Config, ConfigFactory}

trait Configuration {
  protected val config: Config = ConfigFactory.load()
}
