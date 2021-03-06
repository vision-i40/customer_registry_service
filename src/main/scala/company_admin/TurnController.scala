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
  override protected val companyResourceKey: String = "turns"
  override protected val resourcePlural: String = "turns"
  override protected val resourceSingular: String = "turn"

  type PayloadType = Turn

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
