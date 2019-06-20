package domain.repositories

import com.google.inject.{Inject, Singleton}
import domain.models.ReworkGroup

@Singleton
class ReworkGroupRepository @Inject()(collection: CompanyCollection)
  extends CompanyResourceRepository[ReworkGroup] {
  override protected val resourceName: String = "reworkGroups"
  override protected val companyCollection: CompanyCollection = collection
}
