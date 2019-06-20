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
  override protected val COMPANY_RESOURCE_KEY: String = "unitsOfMeasurement"
  override protected val RESOURCE_PLURAL: String = "units_of_measurement"
  override protected val RESOURCE_SINGULAR: String = "unit_of_measurement"

  type PayloadType = UnitOfMeasurement

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
