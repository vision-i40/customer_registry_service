package domain.models

import org.joda.time.DateTime

case class ProductionLine(
  id: String,
  name: String,
  oeeGoal: Double,
  resetProduction: Boolean,
  discountRework: Boolean,
  discountWaste: Boolean,
  createdAt: DateTime,
  updatedAt: DateTime
)
