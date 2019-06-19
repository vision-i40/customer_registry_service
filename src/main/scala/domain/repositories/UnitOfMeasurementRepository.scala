package domain.repositories

import com.google.inject.{Inject, Singleton}
import domain.models.UnitOfMeasurement

@Singleton
class UnitOfMeasurementRepository @Inject()(collection: CompanyCollection)
  extends CompanyResourceRepository[UnitOfMeasurement] {
  override protected val resourceName: String = "unitsOfMeasurement"
  override protected val companyCollection: CompanyCollection = collection
}
