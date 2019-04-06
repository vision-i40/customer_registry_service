package company_admin.requests

case class ProductionLineRequest(
  name: String,
  oeeGoal: Double,
  resetProduction: Boolean,
  discountRework: Boolean ,
  discountWaste: Boolean
)
