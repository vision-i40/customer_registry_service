package authentication

import authentication.dtos.{SigninRequest, SignupRequest}
import com.google.inject.{Inject, Singleton}
import com.twitter.finatra.http.Controller
import com.twitter.finatra.http.response.ResponseBuilder
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class AuthenticationController @Inject()(signInService: SignInService,
                                         signUpService: SignUpService) extends Controller {

  post("/auth/sign_in") { request: SigninRequest =>
    signInService
      .generateToken(request.email, request.password)
      .recover {
        case _ => handleUnauthorized
      }
  }

  post("/auth/sign_up") { request: SignupRequest =>
    signUpService
      .setupCompany(request)
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
