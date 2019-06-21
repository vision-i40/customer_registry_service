package company_admin

import authentication.AuthenticatedUser
import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Request
import company_admin.requests.SingleResourceRequest
import domain.models.WasteCode
import domain.repositories.{CompanyResourceRepository, WasteCodeRepository}

@Singleton
class WasteCodeController @Inject()(wasteCodeRepository: WasteCodeRepository,
                                    user: AuthenticatedUser) extends AbstractController[WasteCode] {
  override protected val authenticatedUser: AuthenticatedUser = user
  override protected val repository: CompanyResourceRepository[WasteCode] = wasteCodeRepository
  override protected val companyResourceKey: String = "wasteCodes"
  override protected val resourcePlural: String = "waste_codes"
  override protected val resourceSingular: String = "waste_code"

  type PayloadType = WasteCode

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
