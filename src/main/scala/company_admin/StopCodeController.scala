package company_admin

import authentication.AuthenticatedUser
import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Request
import company_admin.requests.SingleResourceRequest
import domain.models.StopCode
import domain.repositories.{CompanyResourceRepository, StopCodeRepository}

@Singleton
class StopCodeController @Inject()(stopCodeRepository: StopCodeRepository,
                                   user: AuthenticatedUser) extends AbstractController[StopCode] {
  override protected val authenticatedUser: AuthenticatedUser = user
  override protected val repository: CompanyResourceRepository[StopCode] = stopCodeRepository
  override protected val companyResourceKey: String = "stopCodes"
  override protected val resourcePlural: String = "stop_codes"
  override protected val resourceSingular: String = "stop_code"

  type PayloadType = StopCode

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

  delete(singleRoute) { request: SingleResourceRequest =>
    delete(request)
  }

  get(singleRoute) { request: SingleResourceRequest =>
    getResourceList[PayloadType]
      .find(_.id.exists(_.equals(request.id)))
  }
}
