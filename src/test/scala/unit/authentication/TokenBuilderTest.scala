package unit.authentication

import java.time.Instant

import authentication.TokenBuilder
import authentication.dtos.JwtPayload
import infrastructure.config.AuthConfig
import org.joda.time.DateTime
import org.mockito.Mockito._
import org.mockito.Mockito
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import pdi.jwt.{Jwt, JwtAlgorithm}
import support.VisionSpec
import support.builders.UserBuilder

class TokenBuilderTest extends VisionSpec with MockitoSugar with BeforeAndAfterEach {
  val authConfig: AuthConfig = mock[AuthConfig]
  val tokenBuilder = new TokenBuilder(authConfig)

  val expectedSecret = "a-secret"

  override def beforeEach(): Unit = {
    super.beforeEach()
    Mockito.reset(authConfig)
    when(authConfig.secretKey).thenReturn(expectedSecret)
  }

  behavior of "generating token"
  it should "generate token valid token" in {
    val expectedExpiration = 10L
    when(authConfig.tokenExpirationInSeconds).thenReturn(expectedExpiration)
    val user = UserBuilder().build
    val now = Instant.now.getEpochSecond

    val actualToken = tokenBuilder.build(user)
    val decodeToken = Jwt.decode(actualToken, expectedSecret, Seq(JwtAlgorithm.HS256))

    decodeToken.isSuccess shouldEqual true
    val jsonPayload = JwtPayload.parse(decodeToken.get)
    println(jsonPayload)
    jsonPayload.id shouldEqual user.id
    jsonPayload.exp should be >= expectedExpiration
    jsonPayload.iat should be >= now
  }

  it should "NOT generate token just expired" in {
    val user = UserBuilder().build

    val actualToken = tokenBuilder.build(user, 0)

    val decodeToken = Jwt.decode(actualToken, expectedSecret, Seq(JwtAlgorithm.HS256))

    decodeToken.isSuccess shouldEqual false
  }

  it should "NOT generate stale token" in {
    val user = UserBuilder().build

    val actualToken = tokenBuilder.build(user, -(60*60*24))

    val decodeToken = Jwt.decode(actualToken, expectedSecret, Seq(JwtAlgorithm.HS256))

    decodeToken.isSuccess shouldEqual false
  }
}
