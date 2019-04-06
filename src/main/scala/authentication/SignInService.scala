package authentication

import authentication.exceptions.UnauthorizedException
import authentication.models.AuthenticationToken
import com.google.inject.{Inject, Singleton}
import domain.repositories.UserRepository
import infrastructure.config.EncryptionConfig
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class SignInService @Inject()(repository: UserRepository,
                              tokenBuilder: TokenBuilder,
                              encryptionConfig: EncryptionConfig) {
  private val UNAUTHORIZED_MESSAGE = "Email or password is invalid."

  def generateToken(email: String, password: String): Future[AuthenticationToken] = {
    repository
      .getByEmailAndPassword(email, password)
      .map {
        case Some(user) if user.isActive => tokenBuilder.build(user)
        case _ =>
          throw UnauthorizedException(UNAUTHORIZED_MESSAGE)
      }
  }
}
