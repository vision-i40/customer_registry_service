package company_admin

import authentication.AuthenticatedUser
import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Request
import company_admin.requests.SingleResourceRequest
import domain.models.Equipment
import domain.repositories.{CompanyResourceRepository, EquipmentRepository}

@Singleton
class EquipmentControler @Inject()(equipmentRepository: EquipmentRepository,
                                   user: AuthenticatedUser) extends AbstractController[Equipment] {
  override protected val authenticatedUser: AuthenticatedUser = user
  override protected val repository: CompanyResourceRepository[Equipment] = equipmentRepository
  override protected val companyResourceKey: String = "equipments"
  override protected val resourcePlural: String = "equipments"
  override protected val resourceSingular: String = "equipment"

  type PayloadType = Equipment

  get(indexRoute) { _: Request =>
    getResourceList[PayloadType]
      .sortBy(_.createdAt.get.getMillis)
      .reverse
  }

  post(indexRoute) { payload: PayloadType => post(payload) }

  put(indexRoute) { payload: PayloadType => put(payload) }

  get(singleRoute) { request: SingleResourceRequest =>
    getResourceList[PayloadType].find(_.id.exists(_.equals(request.id)))
  }

  delete(singleRoute) { request: SingleResourceRequest => delete(request)}
}
