package unit.authentication

import authentication.{GenerateToken, UserRepository}
import domain.User
import infrastructure.config.EncryptionConfig
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import pdi.jwt.{Jwt, JwtAlgorithm}
import support.VisionAsyncSpec
import support.build.UserBuilder

import scala.concurrent.Future

class GenerateTokenTest extends VisionAsyncSpec with MockitoSugar {
  implicit private val config: EncryptionConfig = mock[EncryptionConfig]
  implicit private val repository: UserRepository = mock[UserRepository]

  behavior of "generate token"
  it should "return " in {
    val email = "email@@email.com"
    val password = "password"
    val user: User = UserBuilder().build
    when(config.secretKey).thenReturn("a-secret-key")
    when(repository.getByEmailAndPassword(email, password)).thenReturn(Future.successful(Some(user)))

    val tokenFuture = GenerateToken(email, password)

    tokenFuture.map { token =>
      val decodedToken = Jwt.decode(token, config.secretKey, Seq(JwtAlgorithm.HS256))
      decodedToken.isSuccess shouldEqual true
      decodedToken.get shouldEqual s"""{"id":"${user.id}"}"""
    }
  }
}
