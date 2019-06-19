package domain.repositories

import com.google.inject.{Inject, Singleton}
import domain.models.ProductionLine

@Singleton
class ProductionLineRepository @Inject()(collection: CompanyCollection)
  extends CompanyResourceRepository[ProductionLine] {
  override protected val resourceName: String = "productionLines"
  override protected val companyCollection: CompanyCollection = collection
}
