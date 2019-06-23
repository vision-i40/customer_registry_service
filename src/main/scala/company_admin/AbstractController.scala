package company_admin

import authentication.AuthenticatedUser
import com.twitter.finatra.http.Controller
import com.twitter.finatra.http.response.ResponseBuilder
import com.twitter.inject.Logging
import company_admin.requests.{NestedResourceRequest, SingleResourceRequest}
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
  protected val baseResource: String = "/" + API_VERSION + "/:" + COMPANY_SLUG
  protected val companyResourceKey: String
  protected val resourcePlural: String
  protected val resourceSingular: String
  protected val parentResource: Option[String] = None

  protected def indexRoute: String = {
    parentResource
      .map(p => s"$baseResource/$p/:parent_id/$resourcePlural")
      .getOrElse(s"$baseResource/$resourcePlural")
  }
  protected def singleRoute: String = {
    parentResource
      .map(p => s"$baseResource/$p/:parent_id/$resourcePlural/:id")
      .getOrElse(s"$baseResource/$resourceSingular/:id")
  }

  def post(payload: R): Future[ResponseBuilder#EnrichedResponse] = {
    implicit val company: Company = authenticatedUser.getCompany

    repository
      .create(payload)
      .map(response.created.body)
  }

  def post(parentId: String, payload: R): Future[ResponseBuilder#EnrichedResponse] = {
    implicit val company: Company = authenticatedUser.getCompany

    repository
      .create(parentId, payload)
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

  def put(parentId: String, payload: R): Option[Future[R]] = {
    implicit val company: Company = authenticatedUser.getCompany
    payload.id.map { id =>
      repository
        .update(parentId, id, payload)
        .map { _ => payload}
    }
  }

  def delete(request: SingleResourceRequest): Future[ResponseBuilder#EnrichedResponse] = {
    implicit val company: Company = authenticatedUser.getCompany

    repository
      .delete(request.id)
      .map { _ => response.noContent}
  }

  def delete(request: NestedResourceRequest): Future[ResponseBuilder#EnrichedResponse] = {
    implicit val company: Company = authenticatedUser.getCompany

    repository
      .delete(request.parent_id, request.id)
      .map { _ => response.noContent}
  }

  protected def getResourceList[T:ClassTag]: List[T] = {
    val company = authenticatedUser.getCompany

    company
      .getClass
      .getMethod(companyResourceKey)
      .invoke(company)
      .asInstanceOf[List[T]]
  }
}
