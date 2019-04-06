package authentication

import authentication.models.{AuthenticationToken, SignupRequest}
import com.google.inject.{Inject, Singleton}
import domain.models.User
import domain.repositories.{CompanyRepository, UserRepository}
import infrastructure.config.EncryptionConfig

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class SignUpService @Inject()(tokenBuilder: TokenBuilder,
                               companyRepository: CompanyRepository,
                              userRepository: UserRepository,
                              encryptionConfig: EncryptionConfig) {
  def setupCompany(request: SignupRequest): Future[AuthenticationToken] = {
    val userFuture: Future[User] = for {
      company <- companyRepository.create(request.companyName, sanitizeSlug(request))
      user <- userRepository.create(request.userEmail, request.userName, request.userPassword)(company)
    } yield user

    userFuture.map(tokenBuilder.build)
  }

  private def sanitizeSlug(request: SignupRequest): String = {
    request.companySlug.replaceAll("""\s""", "-")
  }
}
