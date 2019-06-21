package company_admin

import authentication.AuthenticatedUser
import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Request
import company_admin.requests.{NestedResourceRequest, SingleResourceRequest}
import domain.models.ReworkCode
import domain.repositories.{CompanyResourceRepository, ReworkCodeRepository}

@Singleton
class ReworkCodeController @Inject()(reworkCodeRepository: ReworkCodeRepository,
                                     user: AuthenticatedUser) extends AbstractController[ReworkCode] {
  override protected val authenticatedUser: AuthenticatedUser = user
  override protected val repository: CompanyResourceRepository[ReworkCode] = reworkCodeRepository
  override protected val companyResourceKey: String = "reworkCodes"
  override protected val resourcePlural: String = "rework_codes"
  override protected val resourceSingular: String = "rework_code"
  override protected val parentResource: Option[String] = Some("rework_group")

  type PayloadType = ReworkCode

  get(indexRoute) { _: Request =>
    getResourceList[PayloadType]
      .sortBy(_.createdAt.get.getMillis)
      .reverse
  }

  post(indexRoute) { payload: PayloadType =>
    post(payload)
  }

  put(singleRoute) { payload: PayloadType =>
    put(payload)
  }

  delete(singleRoute) { request: NestedResourceRequest =>
    delete(request)
  }

  get(singleRoute) { request: NestedResourceRequest =>
    getResourceList[PayloadType]
      .find(_.id.exists(_.equals(request.id)))
  }
}
