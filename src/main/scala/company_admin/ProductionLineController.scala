package company_admin

import authentication.models.AuthenticatedUser
import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import company_admin.requests.{ProductionLineRequest, ShowProductionLineRequest}

@Singleton
class ProductionLineController @Inject()(authenticatedUser: AuthenticatedUser) extends Controller {
  get("/v1/:company_slug/production_lines") { _: Request =>
    authenticatedUser
      .getCompany
      .productionLines
  }

  get("/v1/:company_slug/production_line/:id") { request: ShowProductionLineRequest =>
    authenticatedUser
      .getCompany
      .productionLines
      .find(_.id.equals(request.id))
  }
}
