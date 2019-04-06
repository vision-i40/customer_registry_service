package support.builders

import java.util.UUID.randomUUID

import domain.models.ProductionLine
import io.alphash.faker.Person
import org.joda.time.DateTime

case class ProductionLineBuilder(
  id: String = randomUUID().toString,
  name: String = Person().firstNameFemale,
  oeeGoal: Double = 0.8,
  resetProduction: Boolean = false,
  discountRework: Boolean = false,
  discountWaste: Boolean = false,
  createdAt: DateTime = DateTime.now,
  updatedAt: DateTime = DateTime.now
) {
  def build: ProductionLine = ProductionLine(
    id = id,
    name = name,
    oeeGoal = oeeGoal,
    resetProduction = resetProduction,
    discountRework = discountRework,
    discountWaste = discountWaste,
    createdAt = createdAt,
    updatedAt = updatedAt
  )
}
