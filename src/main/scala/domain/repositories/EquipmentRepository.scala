package domain.repositories

import com.google.inject.{Inject, Singleton}
import domain.models.Equipment

@Singleton
class EquipmentRepository @Inject()(collection: CompanyCollection)
  extends CompanyResourceRepository[Equipment] {
  override protected val resourceName: String = "equipments"
  override protected val companyCollection: CompanyCollection = collection
}
