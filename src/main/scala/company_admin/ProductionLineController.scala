package company_admin

import authentication.AuthenticatedUser
import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Request
import company_admin.requests.SingleResourceRequest
import domain.models.ProductionLine
import domain.repositories.{CompanyResourceRepository, ProductionLineRepository}

@Singleton
class ProductionLineController @Inject()(productionLineRepository: ProductionLineRepository,
                                         user: AuthenticatedUser) extends AbstractController[ProductionLine] {
  override protected val authenticatedUser: AuthenticatedUser = user
  override protected val repository: CompanyResourceRepository[ProductionLine] = productionLineRepository
  override protected val companyResourceKey: String = "productionLines"
  override protected val resourcePlural: String = "production_lines"
  override protected val resourceSingular: String = "production_line"

  type PayloadType = ProductionLine

  get(indexRoute) { _: Request =>
    getResourceList[PayloadType]
      .sortBy(_.createdAt.get.getMillis)
      .reverse
  }

  post(indexRoute) { payload: PayloadType => post(payload) }

  put(singleRoute) { payload: PayloadType => put(payload) }

  get(singleRoute) { request: SingleResourceRequest =>
    getResourceList[PayloadType].find(_.id.exists(_.equals(request.id)))
  }

  delete(singleRoute) { request: SingleResourceRequest => delete(request) }
}
