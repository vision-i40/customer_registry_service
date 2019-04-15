package user

import authentication.models.AuthenticatedUser
import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

@Singleton
class UserController @Inject()(authenticatedUser: AuthenticatedUser) extends Controller {
  get("/v1/user") { _: Request =>
    authenticatedUser.getUser
  }
}
