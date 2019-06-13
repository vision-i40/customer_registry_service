package company_admin.requests

case class UnitOfMeasurementPayload(
  name: String,
  conversion_factor: Double,
  description: Option[String]
)
