package authentication

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

class AuthenticationController extends Controller {

  post("/authenticate") { request: Request =>
    "wip"
  }
}

object AuthenticationController {
  def apply(): AuthenticationController = new AuthenticationController()
}
