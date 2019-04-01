package authentication

import domain.repositories.UserRepository
import infrastructure.config.EncryptionConfig

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
        case Some(user) if user.isActive => BuildToken(user)
        case _ =>
          throw UnauthorizedException(UNAUTHORIZED_MESSAGE)
      }
  }
}
