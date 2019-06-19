package support.builders

import java.util.UUID.randomUUID

import domain.models.ProductionLine
import io.alphash.faker.Person
import org.joda.time.DateTime

case class ProductionLineBuilder(
  id: Option[String] = Some(randomUUID().toString),
  name: String = Person().firstNameFemale,
  oeeGoal: Double = 0.8,
  resetProduction: Boolean = false,
  discountRework: Boolean = false,
  discountWaste: Boolean = false,
  createdAt: Option[DateTime] = Some(DateTime.now),
  updatedAt: Option[DateTime] = Some(DateTime.now)
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
