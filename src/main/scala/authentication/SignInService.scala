package authentication

import authentication.dtos.AuthenticationResponse
import authentication.exceptions.UnauthorizedException
import com.google.inject.{Inject, Singleton}
import domain.models.{Company, User}
import domain.repositories.{CompanyRepository, UserRepository}
import infrastructure.config.EncryptionConfig

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class SignInService @Inject()(userRepository: UserRepository,
                              companyRepository: CompanyRepository,
                              tokenBuilder: TokenBuilder,
                              encryptionConfig: EncryptionConfig) {
  private val UNAUTHORIZED_MESSAGE = "Email or password are invalid."

  def generateToken(email: String, password: String): Future[AuthenticationResponse] = {

    val futureUserAndCompany = for {
      Some(user) <- userRepository.getByEmailAndPassword(email, password)
      Some(company) <- companyRepository.findById(user.defaultCompanyId)
    } yield (user, company)

    futureUserAndCompany
      .map {
        case (user: User, company: Company) => AuthenticationResponse(tokenBuilder.build(user), user, company)
        case _ => throw UnauthorizedException(UNAUTHORIZED_MESSAGE)
      }
  }
}
