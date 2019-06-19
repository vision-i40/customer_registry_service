package domain.repositories

import com.google.inject.{Inject, Singleton}
import domain.models.ReworkCode

@Singleton
class ReworkCodeRepository @Inject()(collection: CompanyCollection)
  extends CompanyResourceRepository[ReworkCode] {
  override protected val resourceName: String = "reworkCodes"
  override protected val companyCollection: CompanyCollection = collection
}
