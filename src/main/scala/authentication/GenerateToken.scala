package authentication

import domain.User
import infrastructure.config.EncryptionConfig
import pdi.jwt.{Jwt, JwtAlgorithm}

import scala.concurrent.{ExecutionContext, Future}

object GenerateToken {
  private val UNAUTHORIZED_MESSAGE = "Email or password is invalid."

  def apply(email: String, password: String)
           (implicit repository: UserRepository,
            encryptionConfig: EncryptionConfig,
            ec: ExecutionContext): Future[AuthenticationToken] = {
    repository
      .getByEmailAndPassword(email, password)
      .map {
        case Some(user) if user.isActive => buildToken(user)
        case _ =>
          throw UnauthorizedException(UNAUTHORIZED_MESSAGE)
      }
  }

  private def buildToken(user: User)(implicit encryptionConfig: EncryptionConfig): AuthenticationToken = {
    AuthenticationToken(Jwt.encode(generateUserData(user), encryptionConfig.secretKey, JwtAlgorithm.HS256))
  }

  private def generateUserData(user: User): String = s"""{"id":"${user.id}"}"""

}
