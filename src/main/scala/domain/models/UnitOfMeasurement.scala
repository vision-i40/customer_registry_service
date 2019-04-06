package domain.models

import org.joda.time.DateTime

case class UnitOfMeasurement(
  id: String,
  name: String,
  conversionFactor: Double,
  createdAt: DateTime,
  updatedAt: DateTime,
  description: Option[String] = None
)
