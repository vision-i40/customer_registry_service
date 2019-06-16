package company_admin.requests

import com.twitter.finatra.request.RouteParam

case class UnitOfMeasurementPayload(
  @RouteParam id: Option[String] = None,
  name: String,
  conversion_factor: Double,
  description: Option[String]
)
