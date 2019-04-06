package company_admin

import authentication.models.AuthenticatedUser
import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import company_admin.requests.{ProductionLinePayload, ShowProductionLineRequest}
import domain.models.Company
import domain.repositories.ProductionLineRepository
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class ProductionLineController @Inject()(authenticatedUser: AuthenticatedUser,
                                         repository: ProductionLineRepository) extends Controller {
  private val API_VERSION = "v1"
  private val COMPANY_SLUG = "company_slug"
  private val BASE_RESOURCE: String = "/" + API_VERSION + "/:" + COMPANY_SLUG

  get(BASE_RESOURCE + "/production_lines") { _: Request =>
    authenticatedUser
      .getCompany
      .productionLines
  }

  post(BASE_RESOURCE + "/production_lines") { request: ProductionLinePayload =>
    implicit val company: Company = authenticatedUser.getCompany

    repository
      .create(
        name = request.name,
        oeeGoal = request.oeeGoal,
        resetProduction = request.resetProduction,
        discountRework = request.discountRework,
        discountWaste = request.discountWaste)
      .map(productionLine => response.created.body(productionLine))
  }

  put(BASE_RESOURCE + "/production_line/:id") { request: ProductionLinePayload =>
    implicit val company: Company = authenticatedUser.getCompany

    repository
      .update(request.id.get, request)
      .map { _ => request}
  }

  get(BASE_RESOURCE + "/production_line/:id") { request: ShowProductionLineRequest =>
    authenticatedUser
      .getCompany
      .productionLines
      .find(_.id.equals(request.id))
  }
}
