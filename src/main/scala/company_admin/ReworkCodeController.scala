package company_admin

import authentication.AuthenticatedUser
import com.google.inject.{Inject, Singleton}
import company_admin.requests.NestedResourceRequest
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

  post(indexRoute) { payload: PayloadType =>
    payload
      .parentId
      .map { parentId =>
        post(parentId, payload)
      }
  }

  put(singleRoute) { payload: ReworkCode =>
    payload
      .parentId
      .map { parentId =>
        put(parentId, payload)
      }
  }

  delete(singleRoute) { request: NestedResourceRequest =>
    delete(request)
  }
}
