package company_admin

import authentication.AuthenticatedUser
import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Request
import company_admin.requests.SingleResourceRequest
import domain.models.ReworkGroup
import domain.repositories.{CompanyResourceRepository, ReworkGroupRepository}

@Singleton
class ReworkGroupController @Inject()(reworkGroupRepository: ReworkGroupRepository,
                                      user: AuthenticatedUser) extends AbstractController[ReworkGroup] {
  override protected val authenticatedUser: AuthenticatedUser = user
  override protected val repository: CompanyResourceRepository[ReworkGroup] = reworkGroupRepository
  override protected val companyResourceKey: String = "reworkGroups"
  override protected val resourcePlural: String = "rework_groups"
  override protected val resourceSingular: String = "rework_group"

  type PayloadType = ReworkGroup

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
