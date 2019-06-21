package domain.repositories

import com.google.inject.{Inject, Singleton}
import domain.models.StopGroup

@Singleton
class StopGroupRepository @Inject()(collection: CompanyCollection)
  extends CompanyResourceRepository[StopGroup] {
  override protected val resourceName: String = "stopGroups"
  override protected val companyCollection: CompanyCollection = collection
}
