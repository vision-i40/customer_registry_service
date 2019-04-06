package company_admin.requests

import com.twitter.finatra.request.RouteParam

case class ProductionLinePayload(
  @RouteParam id: Option[String],
  name: String,
  oeeGoal: Double,
  resetProduction: Boolean,
  discountRework: Boolean ,
  discountWaste: Boolean
)
