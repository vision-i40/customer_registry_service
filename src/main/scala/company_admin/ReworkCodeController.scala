package company_admin

import authentication.AuthenticatedUser
import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Request
import company_admin.requests.SingleResourceRequest
import domain.models.ReworkCode
import domain.repositories.{CompanyResourceRepository, ReworkCodeRepository}

@Singleton
class ReworkCodeController @Inject()(reworkCodeRepository: ReworkCodeRepository,
                                     user: AuthenticatedUser) extends AbstractController[ReworkCode] {
  override protected val authenticatedUser: AuthenticatedUser = user
  override protected val repository: CompanyResourceRepository[ReworkCode] = reworkCodeRepository
  override protected val COMPANY_RESOURCE_KEY: String = "reworkCodes"
  override protected val RESOURCE_PLURAL: String = "rework_codes"
  override protected val RESOURCE_SINGULAR: String = "rework_code"

  type PayloadType = ReworkCode

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

  delete(SINGLE_ROUTE) { request: SingleResourceRequest =>
    delete(request)
  }

  get(SINGLE_ROUTE) { request: SingleResourceRequest =>
    getResourceList[PayloadType]
      .find(_.id.exists(_.equals(request.id)))
  }
}
