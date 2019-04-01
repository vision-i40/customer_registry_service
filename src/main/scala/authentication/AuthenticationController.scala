package authentication

import com.twitter.finatra.http.Controller
import com.twitter.finatra.http.response.ResponseBuilder
import domain.repositories.{CompanyRepository, UserRepository}
import infrastructure.config.EncryptionConfig
import infrastructure.mongodb.MongoDB

import scala.concurrent.ExecutionContext

class AuthenticationController(implicit repository: UserRepository,
                               companyRepository: CompanyRepository,
                               encryptionConfig: EncryptionConfig,
                               ec: ExecutionContext) extends Controller {

  post("/auth/sign_in") { request: SigninRequest =>
    GenerateToken(request.email, request.password)
      .recover {
        case _ => handleUnauthorized
      }
  }

  post("/auth/sign_up") { request: SignupRequest =>
    SetupCompany(request)
      .recover {
        case e => handleError(e)
      }
  }

  private def handleUnauthorized: ResponseBuilder#EnrichedResponse = {
    response.unauthorized().json("""{"message": "Unauthorized. Please, double check your credentials."}""")
  }

  private def handleError(e: Throwable): ResponseBuilder#EnrichedResponse = {
    response.badRequest.json(s"""{"message": "Something went wrong processing the request. ${e.getMessage}."}""")
  }
}

object AuthenticationController {
  def apply()(implicit db: MongoDB, ec: ExecutionContext): AuthenticationController ={
    implicit val encryptionConfig: EncryptionConfig = EncryptionConfig
    implicit val userRepository: UserRepository = UserRepository()
    implicit val companyRepository: CompanyRepository = CompanyRepository()

    new AuthenticationController()
  }
}
