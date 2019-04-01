package authentication

import com.twitter.finatra.http.Controller
import com.twitter.finatra.http.response.ResponseBuilder
import infrastructure.config.EncryptionConfig
import infrastructure.mongodb.MongoDB
import scala.concurrent.ExecutionContext

class AuthenticationController(implicit repository: UserRepository,
                               encryptionConfig: EncryptionConfig,
                               ec: ExecutionContext) extends Controller {

  post("/authenticate") { request: AuthenticationRequest =>
    GenerateToken(request.email, request.password)
      .recover {
        case _ => handleUnauthorized
      }
  }

  private def handleUnauthorized: ResponseBuilder#EnrichedResponse = {
    response.unauthorized().json("""{"message": "Unauthorized. Please, double check your credentials."}""")
  }
}

object AuthenticationController {
  def apply()(implicit db: MongoDB, ec: ExecutionContext): AuthenticationController ={
    implicit val encryptionConfig: EncryptionConfig = EncryptionConfig
    implicit val userRepository: UserRepository = UserRepository()

    new AuthenticationController()
  }
}
