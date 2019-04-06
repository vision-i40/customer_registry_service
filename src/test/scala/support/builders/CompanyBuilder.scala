package support.builders

import java.util.UUID

import io.alphash.faker._
import domain._
import domain.models._
import org.joda.time.DateTime

case class CompanyBuilder(
  id: String = UUID.randomUUID().toString,
  name: String = Person().lastName,
  slug: String = Person().lastName.toLowerCase.replaceAll(" ", "-"),
  turns: List[Turn] = List(),
  productionLines: List[ProductionLine] = List(),
  collectors: List[Collector] = List(),
  stopGroups: List[StopGroup] = List(),
  reworkGroups: List[ReworkGroup] = List(),
  wasteGroups: List[WasteGroup] = List(),
  unitOfMeasurements: List[UnitOfMeasurement] = List(),
  createdAt: DateTime = DateTime.now,
  updatedAt: DateTime = DateTime.now
) {
  def build:Company = Company(
    id = id,
    name = name,
    slug = slug,
    turns = turns,
    productionLines = productionLines,
    collectors = collectors,
    stopGroups = stopGroups,
    reworkGroups = reworkGroups,
    wasteGroups = wasteGroups,
    unitOfMeasurements = unitOfMeasurements,
    createdAt = createdAt,
    updatedAt = updatedAt
  )
}
