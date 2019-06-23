package unit.infrastructure.config

import infrastructure.config.AuthConfig
import support.VisionSpec

class AuthConfigTest extends VisionSpec {
  private val authenticationConfig = new AuthConfig()

  behavior of "AuthenticationConfig"
  it should "return the verification expiration in minutes value set correctly " in {
    authenticationConfig.verificationExpirationInMinutes shouldEqual 1440
  }

  it should "return the salt value set correctly " in {
    authenticationConfig.verificationSalt shouldEqual "KaNCiSkYLIfoLEb"
  }

}
