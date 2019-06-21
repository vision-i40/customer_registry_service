package domain.repositories

import com.google.inject.{Inject, Singleton}
import domain.models.WasteGroup

@Singleton
class WasteGroupRepository @Inject()(collection: CompanyCollection)
  extends CompanyResourceRepository[WasteGroup] {
  override protected val resourceName: String = "wasteGroups"
  override protected val companyCollection: CompanyCollection = collection
}
