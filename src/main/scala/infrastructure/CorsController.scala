package infrastructure

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

class CorsController extends Controller {
  options("/*") {
    _: Request => response.ok
  }
}
