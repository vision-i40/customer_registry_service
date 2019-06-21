package company_admin

import authentication.AuthenticatedUser
import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Request
import company_admin.requests.SingleResourceRequest
import domain.models.StopGroup
import domain.repositories.{CompanyResourceRepository, StopGroupRepository}

@Singleton
class StopGroupController @Inject()(stopGroupRepository: StopGroupRepository,
                                    user: AuthenticatedUser) extends AbstractController[StopGroup] {
  override protected val authenticatedUser: AuthenticatedUser = user
  override protected val repository: CompanyResourceRepository[StopGroup] = stopGroupRepository
  override protected val companyResourceKey: String = "stopGroups"
  override protected val resourcePlural: String = "stop_groups"
  override protected val resourceSingular: String = "stop_group"

  type PayloadType = StopGroup

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
