package authentication

import domain.User
import domain.repositories.{CompanyRepository, UserRepository}
import infrastructure.config.EncryptionConfig
import scala.concurrent.{ExecutionContext, Future}

object SetupCompany {
  def apply(request: SignupRequest)
           (implicit companyRepository: CompanyRepository,
            userRepository: UserRepository,
            ec: ExecutionContext,
            encryptionConfig: EncryptionConfig): Future[AuthenticationToken] = {
    val userFuture: Future[User] = for {
      company <- companyRepository.create(request.companyName)
      user <- userRepository.create(request.userEmail, request.userName, request.userPassword)(company)
    } yield user

    userFuture.map(BuildToken(_))
  }

}
