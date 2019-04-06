package unit.infrastructure.config

import infrastructure.config.EncryptionConfig
import support.VisionSpec

class EncryptionConfigTest extends VisionSpec {
  private val config = new EncryptionConfig()

  behavior of "EncryptionConfig"
  it should "return salt value" in {
    config.salt shouldEqual "$2a$10$vI8aWBnW3fID.ZQ4/zo1G.q1lRps.9cGLcZEiGDMVr5yUP1KUOYTa"
  }

  it should "return secret key value" in {
    config.secretKey shouldEqual "asdf12345fdsa0987as7d7991823791d1asd"
  }

}
