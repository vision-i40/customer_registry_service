package company_admin

import authentication.AuthenticatedUser
import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Request
import company_admin.requests.SingleResourceRequest
import domain.models.WasteGroup
import domain.repositories.{CompanyResourceRepository, WasteGroupRepository}

@Singleton
class WasteGroupController @Inject()(wasteGroupRepository: WasteGroupRepository,
                                     user: AuthenticatedUser) extends AbstractController[WasteGroup] {
  override protected val authenticatedUser: AuthenticatedUser = user
  override protected val repository: CompanyResourceRepository[WasteGroup] = wasteGroupRepository
  override protected val companyResourceKey: String = "wasteGroups"
  override protected val resourcePlural: String = "waste_groups"
  override protected val resourceSingular: String = "waste_group"

  type PayloadType = WasteGroup

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
