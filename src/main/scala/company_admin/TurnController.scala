package company_admin

import authentication.AuthenticatedUser
import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Request
import company_admin.requests.SingleResourceRequest
import domain.models.{Company, Turn}
import domain.repositories.{CompanyResourceRepository, TurnRepository}

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class TurnController @Inject()(turnRepository: TurnRepository,
                               user: AuthenticatedUser) extends AbstractController[Turn]  {
  override protected val authenticatedUser: AuthenticatedUser = user
  override protected val repository: CompanyResourceRepository[Turn] = turnRepository
  override protected val COMPANY_RESOURCE_KEY: String = "turns"
  override protected val RESOURCE_PLURAL: String = "turns"
  override protected val RESOURCE_SINGULAR: String = "turn"

  type PayloadType = Turn

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
