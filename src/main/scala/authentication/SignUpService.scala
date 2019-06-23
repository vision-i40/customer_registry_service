package authentication

import java.security.MessageDigest
import java.util.UUID.randomUUID

import authentication.dtos.{SignUpResponse, SignUpRequest}
import com.google.inject.{Inject, Singleton}
import domain.models.User
import domain.repositories.UserRepository
import infrastructure.config.AuthConfig
import org.joda.time.DateTime
import org.mindrot.jbcrypt.BCrypt

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class SignUpService @Inject()(userRepository: UserRepository,
                              userVerificationRepository: UserVerificationRepository,
                              authenticationConfig: AuthConfig) {
  def signUp(request: SignUpRequest,
             id: String = randomUUID().toString,
             signUpDateTime: DateTime = DateTime.now): Future[SignUpResponse] = {

    val newUser = buildUser(request, id, signUpDateTime)
    val userVerification = buildUserVerification(id, signUpDateTime)

    for {
      user <- userRepository.create(newUser)
      userVerification <- userVerificationRepository.create(userVerification)
    } yield SignUpResponse(user.email, user.name, userVerification.expiresAt)
  }

  private def buildUser(request: SignUpRequest, id: String, signUpDateTime: DateTime) = {
    User(
      id = id,
      name = request.name,
      email = request.email,
      username = request.username,
      password = BCrypt.hashpw(request.password, authenticationConfig.bcryptSalt),
      updatedAt = signUpDateTime,
      createdAt = signUpDateTime
    )
  }

  private def buildUserVerification(id: String, signUpDateTime: DateTime): UserVerification = {
    UserVerification(
      token = buildToken(id),
      expiresAt = signUpDateTime.plusMinutes(authenticationConfig.verificationExpirationInMinutes),
      createdAt = signUpDateTime
    )
  }

  private def buildToken(id: String): String = {
    val hash = s"${authenticationConfig.verificationSalt}-$id"
    val algorithm = "SHA-256"
    val charset = "UTF-8"

    MessageDigest.getInstance(algorithm)
      .digest(hash.getBytes(charset))
      .map("%02x".format(_)).mkString
  }
}
