package domain.repositories

import com.google.inject.{Inject, Singleton}
import domain.models.Collector

@Singleton
class CollectorRepository @Inject()(collection: CompanyCollection)
  extends CompanyResourceRepository[Collector] {
  override protected val resourceName: String = "collectors"
  override protected val companyCollection: CompanyCollection = collection
}
