package unit.authentication

import java.security.MessageDigest
import java.util.UUID

import authentication.{SignUpService, UserVerificationRepository}
import domain.repositories.UserRepository
import infrastructure.config.{AuthConfig, PubSubConfig}
import infrastructure.pubsub.PubSubPublisher
import org.joda.time.DateTime
import org.mindrot.jbcrypt.BCrypt
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import support.VisionAsyncSpec
import support.builders.{SignupRequestBuilder, UserBuilder, UserVerificationBuilder}

import scala.concurrent.Future

class SignUpServiceTest extends VisionAsyncSpec with MockitoSugar with BeforeAndAfterEach {
  implicit private val pubSubConfig: PubSubConfig = mock[PubSubConfig]
  private val authConfig: AuthConfig = mock[AuthConfig]
  private val userRepository: UserRepository = mock[UserRepository]
  private val userVerificationRepository: UserVerificationRepository = mock[UserVerificationRepository]
  private val service = new SignUpService(userRepository, userVerificationRepository,
    authConfig)

  private val verificationTokenSaltConfig = "a-salt"
  private val encryptionSaltConfig = "$2a$10$vI8aWBnW3fID.ZQ4/zo1G.q1lRps.9cGLcZEiGDMVr5yUP1KUOYTa"
  private val expirationConfig = 1440

  override def beforeEach(): Unit = {
    super.beforeEach()
    when(authConfig.verificationExpirationInMinutes).thenReturn(expirationConfig)
    when(authConfig.verificationSalt).thenReturn(verificationTokenSaltConfig)
    when(authConfig.bcryptSalt).thenReturn(encryptionSaltConfig)
  }

  behavior of "adding a new user"
  it should "create a disabled user and with a verifying token set" in {
    val newUser = SignupRequestBuilder().build
    val expectedId = UUID.randomUUID().toString
    val expectedDateTime = DateTime.now
    val expectedPassword = BCrypt.hashpw(newUser.password, encryptionSaltConfig)

    val expectedUser = UserBuilder(
      id = expectedId,
      defaultCompanyId = None,
      email = newUser.email,
      username = newUser.username,
      name = newUser.name,
      isActive = false,
      createdAt = expectedDateTime,
      updatedAt = expectedDateTime,
      password = expectedPassword
    ).build

    val expectedToken = buildToken(expectedId)
    val expectedExpiration = expectedDateTime.plusMinutes(expirationConfig)
    val expectedUserVerification = UserVerificationBuilder(
      token = expectedToken,
      createdAt = expectedDateTime,
      expiresAt = expectedExpiration
    ).build


    when(userRepository.create(expectedUser)).thenReturn(Future.successful(expectedUser))
    when(userVerificationRepository.create(expectedUserVerification))
      .thenReturn(Future.successful(expectedUserVerification))

    service
      .signUp(newUser, expectedId, expectedDateTime)
      .map { authenticationResponse =>

        verify(userRepository, times(1))
          .create(expectedUser)
        verify(userVerificationRepository, times(1))
          .create(expectedUserVerification)


        authenticationResponse.email shouldEqual newUser.email
        authenticationResponse.name shouldEqual newUser.name
        authenticationResponse.verificationExpireAt shouldEqual expectedExpiration
      }
  }

  private def buildToken(id: String): String = {
    val hash = s"${authConfig.verificationSalt}-$id"
    val algorithm = "SHA-256"
    val charset = "UTF-8"

    MessageDigest.getInstance(algorithm)
      .digest(hash.getBytes(charset))
      .map("%02x".format(_)).mkString
  }
}
