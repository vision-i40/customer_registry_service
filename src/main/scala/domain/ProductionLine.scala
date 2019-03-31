package domain

case class ProductionLine(
  id: String,
  name: String,
  oee_goal: Double,
  reset_production: Boolean,
  discount_rework: Boolean,
  discount_waste: Boolean,
)
