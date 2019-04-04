package infrastructure

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

class CorsController extends Controller {
  options("/auth/sign_in") {
    _: Request => response.ok
  }
}
