package unit.authentication

import authentication.models.AuthenticationToken
import authentication.{SignInService, TokenBuilder}
import domain.User
import domain.repositories.UserRepository
import infrastructure.config.EncryptionConfig
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import pdi.jwt.{Jwt, JwtAlgorithm}
import sun.text.normalizer.ICUBinary.Authenticate
import support.VisionAsyncSpec
import support.builders.UserBuilder

import scala.concurrent.Future

class SignInServiceTest extends VisionAsyncSpec with MockitoSugar {
  private val config: EncryptionConfig = mock[EncryptionConfig]
  private val repository: UserRepository = mock[UserRepository]
  private val tokenBuilder: TokenBuilder = mock[TokenBuilder]
  private val service = new SignInService(repository, tokenBuilder,config)

  behavior of "generate token"
  it should "return " in {
    val email = "email@@email.com"
    val password = "password"
    val user: User = UserBuilder().build
    val expectedJWTPayload = s"""{"id":"${user.id}"}"""
    when(config.secretKey).thenReturn("a-secret-key")
    when(tokenBuilder.build(user)).thenReturn(AuthenticationToken(Jwt.encode(expectedJWTPayload)))
    when(repository.getByEmailAndPassword(email, password)).thenReturn(Future.successful(Some(user)))

    val tokenFuture = service.generateToken(email, password)

    tokenFuture.map { authorizationToken =>
      val decodedToken = Jwt.decode(authorizationToken.token)
      decodedToken.isSuccess shouldEqual true
      decodedToken.get shouldEqual expectedJWTPayload
    }
  }
}
