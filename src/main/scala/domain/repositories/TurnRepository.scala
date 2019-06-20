package domain.repositories

import com.google.inject.{Inject, Singleton}
import domain.models.Turn

@Singleton
class TurnRepository @Inject()(collection: CompanyCollection)
  extends CompanyResourceRepository[Turn] {
  override protected val resourceName: String = "turns"
  override protected val companyCollection: CompanyCollection = collection
}
