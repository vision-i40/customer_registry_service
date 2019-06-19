package company_admin

import authentication.AuthenticatedUser
import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.inject.Logging
import company_admin.requests.SingleResourceRequest
import domain.models.{Company, ProductionLine}
import domain.repositories.ProductionLineRepository

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class ProductionLineController @Inject()(authenticatedUser: AuthenticatedUser,
                                         repository: ProductionLineRepository) extends Controller with Logging {
  private val API_VERSION = "v1"
  private val COMPANY_SLUG = "company_slug"
  private val BASE_RESOURCE: String = "/" + API_VERSION + "/:" + COMPANY_SLUG

  get(BASE_RESOURCE + "/production_lines") { _: Request =>
    authenticatedUser
      .getCompany
      .productionLines
      .sortBy(_.createdAt.get.getMillis)
      .reverse
  }

  post(BASE_RESOURCE + "/production_lines") { payload: ProductionLine =>
    implicit val company: Company = authenticatedUser.getCompany
    info(s"Saving company $company")

    repository
      .create(payload)
      .map(productionLine => response.created.body(productionLine))
  }

  put(BASE_RESOURCE + "/production_line/:id") { payload: ProductionLine =>
    implicit val company: Company = authenticatedUser.getCompany
    payload.id.map { productionLineId =>
      repository
        .update(productionLineId, payload)
      .map { _ => payload}
    }
  }

  get(BASE_RESOURCE + "/production_line/:id") { request: SingleResourceRequest =>
    authenticatedUser
      .getCompany
      .productionLines
      .find(_.id.exists(_.equals(request.id)))
  }

  delete(BASE_RESOURCE + "/production_line/:id") { request: SingleResourceRequest =>
    implicit val company: Company = authenticatedUser.getCompany

    repository
      .delete(request.id)
      .map { _ => response.noContent}
  }
}
