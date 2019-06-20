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
  override protected val COMPANY_RESOURCE_KEY: String = "reworkGroups"
  override protected val RESOURCE_PLURAL: String = "rework_groups"
  override protected val RESOURCE_SINGULAR: String = "rework_group"

  type PayloadType = ReworkGroup

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
