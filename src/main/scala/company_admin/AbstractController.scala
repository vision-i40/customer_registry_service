package company_admin

import authentication.AuthenticatedUser
import com.twitter.finatra.http.Controller
import com.twitter.finatra.http.response.ResponseBuilder
import com.twitter.inject.Logging
import company_admin.requests.SingleResourceRequest
import domain.models.{Company, CompanyResource}
import domain.repositories.CompanyResourceRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.reflect.ClassTag

trait AbstractController[R <: CompanyResource] extends Controller with Logging {
  protected val authenticatedUser: AuthenticatedUser
  protected val repository: CompanyResourceRepository[R]

  protected val API_VERSION = "v1"
  protected val COMPANY_SLUG = "company_slug"
  protected val BASE_RESOURCE: String = "/" + API_VERSION + "/:" + COMPANY_SLUG
  protected val COMPANY_RESOURCE_KEY: String
  protected val RESOURCE_PLURAL: String
  protected val RESOURCE_SINGULAR: String

  protected lazy val INDEX_ROUTE = s"$BASE_RESOURCE/$RESOURCE_PLURAL"
  protected lazy val SINGLE_ROUTE = s"$BASE_RESOURCE/$RESOURCE_SINGULAR/:id"

  def post(payload: R): Future[ResponseBuilder#EnrichedResponse] = {
    implicit val company: Company = authenticatedUser.getCompany

    repository
      .create(payload)
      .map(response.created.body)
  }

  def put(payload: R): Option[Future[R]] = {
    implicit val company: Company = authenticatedUser.getCompany
    payload.id.map { id =>
      repository
        .update(id, payload)
        .map { _ => payload}
    }
  }

  def delete(request: SingleResourceRequest): Future[ResponseBuilder#EnrichedResponse] = {
    implicit val company: Company = authenticatedUser.getCompany

    repository
      .delete(request.id)
      .map { _ => response.noContent}
  }

  protected def getResourceList[T:ClassTag]: List[T] = {
    val company = authenticatedUser.getCompany

    company
      .getClass
      .getMethod(COMPANY_RESOURCE_KEY)
      .invoke(company)
      .asInstanceOf[List[T]]
  }
}
