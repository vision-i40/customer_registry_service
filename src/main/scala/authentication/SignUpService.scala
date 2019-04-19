package authentication

import authentication.dtos.{AuthenticationResponse, SignupRequest}
import authentication.exceptions.BadRequestException
import com.google.inject.{Inject, Singleton}
import domain.models.{Company, User}
import domain.repositories.{CompanyRepository, UserRepository}
import infrastructure.config.EncryptionConfig

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class SignUpService @Inject()(tokenBuilder: TokenBuilder,
                               companyRepository: CompanyRepository,
                              userRepository: UserRepository,
                              encryptionConfig: EncryptionConfig) {
  private val BAD_REQUEST_MESSAGE = "There was an error while processing company/user information"

  def setupCompany(request: SignupRequest): Future[AuthenticationResponse] = {
    val userFuture = for {
      company <- companyRepository.create(request.companyName, sanitizeSlug(request))
      user <- userRepository.create(request.userEmail, request.userName, request.userPassword)(company)
    } yield (user, company)

    userFuture.map{
      case (user: User, company: Company) => AuthenticationResponse(tokenBuilder.build(user), user, company)
      case _ => throw BadRequestException(BAD_REQUEST_MESSAGE)
    }
  }

  private def sanitizeSlug(request: SignupRequest): String = {
    request.companySlug.replaceAll("""\s""", "-")
  }
}
