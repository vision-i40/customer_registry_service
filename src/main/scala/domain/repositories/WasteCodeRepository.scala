package domain.repositories

import com.google.inject.{Inject, Singleton}
import domain.models.WasteCode

@Singleton
class WasteCodeRepository @Inject()(collection: CompanyCollection)
  extends CompanyResourceRepository[WasteCode] {
  override protected val resourceName: String = "wasteCodes"
  override protected val companyCollection: CompanyCollection = collection
  override protected val parentResource: Option[String] = Some("wasteGroups")
}
