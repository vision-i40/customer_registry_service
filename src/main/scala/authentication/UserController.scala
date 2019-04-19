package authentication

import authentication.dtos.UserInformation
import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import domain.repositories.CompanyRepository

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class UserController @Inject()(authenticatedUser: AuthenticatedUser,
                               companyRepository: CompanyRepository) extends Controller {
  get("/v1/user") { _: Request =>
    val currentUser = authenticatedUser.getUser

    companyRepository
      .findById(currentUser.defaultCompanyId)
      .map(UserInformation.apply(currentUser, _))
  }
}
