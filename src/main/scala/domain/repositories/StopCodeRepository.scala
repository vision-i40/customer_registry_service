package domain.repositories

import com.google.inject.{Inject, Singleton}
import domain.models.StopCode

@Singleton
class StopCodeRepository @Inject()(collection: CompanyCollection)
  extends CompanyResourceRepository[StopCode] {
  override protected val resourceName: String = "stopCodes"
  override protected val companyCollection: CompanyCollection = collection
  override protected val parentResource: Option[String] = Some("stopGroups")
}
