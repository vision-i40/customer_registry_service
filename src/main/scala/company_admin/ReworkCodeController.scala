package company_admin

import authentication.AuthenticatedUser
import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.inject.Logging
import company_admin.requests.SingleResourceRequest
import domain.models.{Company, ReworkCode}
import domain.repositories.ReworkCodeRepository

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class ReworkCodeController @Inject()(authenticatedUser: AuthenticatedUser,
                                     repository: ReworkCodeRepository) extends Controller with Logging {
  private val API_VERSION = "v1"
  private val COMPANY_SLUG = "company_slug"
  private val BASE_RESOURCE: String = "/" + API_VERSION + "/:" + COMPANY_SLUG

  get(BASE_RESOURCE + "/rework_codes") { _: Request =>
    authenticatedUser
      .getCompany
      .reworkCodes
      .sortBy(_.createdAt.get.getMillis)
      .reverse
  }

  post(BASE_RESOURCE + "/rework_codes") { payload: ReworkCode =>
    implicit val company: Company = authenticatedUser.getCompany
    info(s"Saving company $company")

    repository
      .create(payload)
      .map(reworkCode => response.created.body(reworkCode))
  }

  put(BASE_RESOURCE + "/rework_code/:id") { payload: ReworkCode =>
    implicit val company: Company = authenticatedUser.getCompany
    payload.id.map { reworkCodeId =>
      repository
        .update(reworkCodeId, payload)
      .map { _ => payload}
    }
  }

  get(BASE_RESOURCE + "/rework_code/:id") { request: SingleResourceRequest =>
    authenticatedUser
      .getCompany
      .reworkCodes
      .find(_.id.exists(_.equals(request.id)))
  }

  delete(BASE_RESOURCE + "/rework_code/:id") { request: SingleResourceRequest =>
    implicit val company: Company = authenticatedUser.getCompany

    repository
      .delete(request.id)
      .map { _ => response.noContent}
  }
}
