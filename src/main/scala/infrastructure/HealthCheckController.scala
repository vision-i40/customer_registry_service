package infrastructure

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

class HealthCheckController extends Controller {
  get("/health") { _: Request =>
    response.ok
  }
}
