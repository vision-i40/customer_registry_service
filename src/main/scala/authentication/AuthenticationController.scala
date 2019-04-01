package authentication

import com.twitter.finatra.http.Controller
import infrastructure.config.EncryptionConfig
import infrastructure.mongodb.MongoDB
import scala.concurrent.ExecutionContext

class AuthenticationController(implicit repository: UserRepository,
                               encryptionConfig: EncryptionConfig,
                               ec: ExecutionContext) extends Controller {

  post("/authenticate") { request: AuthenticationRequest =>
    GenerateToken(request.email, request.password)
      .map { token => response.ok().body(AuthenticationToken(token)) }
      .recover {
        case _ => response.unauthorized().json("""{"message": "Unauthorized"}""")
      }
  }
}

object AuthenticationController {
  def apply()(implicit db: MongoDB, ec: ExecutionContext): AuthenticationController ={
    implicit val encryptionConfig: EncryptionConfig = EncryptionConfig
    implicit val userRepository: UserRepository = UserRepository()

    new AuthenticationController()
  }
}
