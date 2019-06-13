package company_admin

import authentication.AuthenticatedUser
import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.inject.Logging
import company_admin.requests.UnitOfMeasurementPayload
import domain.models.Company
import domain.repositories.UnitOfMeasurementRepository
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class UnitOfMeasurementController @Inject()(authenticatedUser: AuthenticatedUser,
                                            repository: UnitOfMeasurementRepository) extends Controller with Logging  {
  private val API_VERSION = "v1"
  private val COMPANY_SLUG = "company_slug"
  private val BASE_RESOURCE: String = "/" + API_VERSION + "/:" + COMPANY_SLUG

  get(BASE_RESOURCE + "/units_of_measurement") { _: Request =>
    authenticatedUser
      .getCompany
      .unitsOfMeasurement
      .sortBy(_.createdAt.getMillis)
      .reverse
  }

  post(BASE_RESOURCE + "/units_of_measurement") { request: UnitOfMeasurementPayload =>
    implicit val company: Company = authenticatedUser.getCompany
    info(s"Saving company $company")

    repository
      .create(
        name = request.name,
        conversionFactor = request.conversion_factor,
        description = request.description
      )
      .map(unitOfMeasurement => response.created.body(unitOfMeasurement))
  }
}
