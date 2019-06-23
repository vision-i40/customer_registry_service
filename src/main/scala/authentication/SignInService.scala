package authentication

import authentication.dtos.AuthenticationResponse
import authentication.exceptions.UnauthorizedException
import com.google.inject.{Inject, Singleton}
import domain.repositories.UserRepository
import org.joda.time.DateTime

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class SignInService @Inject()(userRepository: UserRepository,
                              tokenBuilder: TokenBuilder) {
  private val UNAUTHORIZED_MESSAGE = "Email or password are invalid."

  def generateToken(email: String, password: String): Future[AuthenticationResponse] = {
    userRepository
      .getByEmailAndPassword(email, password)
      .map {
        case Some(user) => AuthenticationResponse(tokenBuilder.build(user), DateTime.now)
        case _ => throw UnauthorizedException(UNAUTHORIZED_MESSAGE)
      }
  }
}
