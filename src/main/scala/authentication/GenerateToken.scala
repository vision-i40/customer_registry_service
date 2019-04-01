package authentication

import domain.User
import infrastructure.config.EncryptionConfig
import pdi.jwt.{Jwt, JwtAlgorithm}

import scala.concurrent.{ExecutionContext, Future}

object GenerateToken {
  def apply(email: String, password: String)
           (implicit repository: UserRepository,
            encryptionConfig: EncryptionConfig,
            ec: ExecutionContext): Future[String] = {
    repository
      .getByEmailAndPassword(email, password)
      .map {
        case Some(user) => Jwt.encode(generateUserData(user), encryptionConfig.secretKey, JwtAlgorithm.HS256)
        case _ => throw UnauthorizedException()
      }
  }

  private def generateUserData(user: User): String = s"""{"id":"${user.id}"}"""

}
