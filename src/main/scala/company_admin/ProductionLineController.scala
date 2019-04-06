package company_admin

import authentication.models.AuthenticatedUser
import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import company_admin.requests.{ProductionLineRequest, ShowProductionLineRequest}
import domain.models.Company
import domain.repositories.ProductionLineRepository
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class ProductionLineController @Inject()(authenticatedUser: AuthenticatedUser,
                                         repository: ProductionLineRepository) extends Controller {
  get("/v1/:company_slug/production_lines") { _: Request =>
    authenticatedUser
      .getCompany
      .productionLines
  }

  post("/v1/:company_slug/production_lines") { request: ProductionLineRequest =>
    implicit val company: Company = authenticatedUser.getCompany

    repository
      .addProductionLine(
        name = request.name,
        oeeGoal = request.oeeGoal,
        resetProduction = request.resetProduction,
        discountRework = request.discountRework,
        discountWaste = request.discountWaste
      ).map(productionLine => response.created.body(productionLine))
  }

  get("/v1/:company_slug/production_line/:id") { request: ShowProductionLineRequest =>
    authenticatedUser
      .getCompany
      .productionLines
      .find(_.id.equals(request.id))
  }
}
