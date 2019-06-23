package unit.infrastructure.config

import infrastructure.config.PubSubConfig
import support.VisionSpec

class PubSubConfigTest extends VisionSpec {
  private val config: PubSubConfig = new PubSubConfig()

  behavior of "pub/sub config"
  it should "properly provide the is local flag" in {
    config.isLocal shouldEqual true
  }

  it should "properly provide the host" in {
    config.hostPort shouldEqual "localhost:8085"
  }

  it should "properly provide the project id" in {
    config.projectId shouldEqual "vision-project-id"
  }

  it should "properly provide the user events topic name" in {
    config.userEventsTopic shouldEqual "user_events_local"
  }

  it should "properly provide the credentials file" in {
    config.credentialsFile shouldEqual None
  }
}
