package company_admin

import authentication.AuthenticatedUser
import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Request
import company_admin.requests.SingleResourceRequest
import domain.models.UnitOfMeasurement
import domain.repositories.{CompanyResourceRepository, UnitOfMeasurementRepository}

@Singleton
class UnitOfMeasurementController @Inject()(unitOfMeasurementRepository: UnitOfMeasurementRepository,
                                            user: AuthenticatedUser) extends AbstractController[UnitOfMeasurement]  {
  override protected val authenticatedUser: AuthenticatedUser = user
  override protected val repository: CompanyResourceRepository[UnitOfMeasurement] = unitOfMeasurementRepository
  override protected val companyResourceKey: String = "unitsOfMeasurement"
  override protected val resourcePlural: String = "units_of_measurement"
  override protected val resourceSingular: String = "unit_of_measurement"

  type PayloadType = UnitOfMeasurement

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
