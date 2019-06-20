package company_admin

import authentication.AuthenticatedUser
import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.inject.Logging
import company_admin.requests.SingleResourceRequest
import domain.models.{Company, ProductionLine, ReworkCode}
import domain.repositories.{CompanyResourceRepository, ProductionLineRepository}

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class ProductionLineController @Inject()(productionLineRepository: ProductionLineRepository,
                                         user: AuthenticatedUser) extends AbstractController[ProductionLine] {
  override protected val authenticatedUser: AuthenticatedUser = user
  override protected val repository: CompanyResourceRepository[ProductionLine] = productionLineRepository
  override protected val COMPANY_RESOURCE_KEY: String = "productionLines"
  override protected val RESOURCE_PLURAL: String = "production_lines"
  override protected val RESOURCE_SINGULAR: String = "production_line"

  type PayloadType = ProductionLine

  get(INDEX_ROUTE) { _: Request =>
    getResourceList[PayloadType]
      .sortBy(_.createdAt.get.getMillis)
      .reverse
  }

  post(INDEX_ROUTE) { payload: PayloadType =>
    post(payload)
  }

  put(SINGLE_ROUTE) { payload: PayloadType =>
    put(payload)
  }

  get(SINGLE_ROUTE) { request: SingleResourceRequest =>
    getResourceList[PayloadType].find(_.id.exists(_.equals(request.id)))
  }

  delete(SINGLE_ROUTE) { request: SingleResourceRequest =>
    delete(request)
  }
}
