package support.build

import java.util.UUID
import io.alphash.faker._
import domain._
import org.joda.time.DateTime

case class CompanyBuilder(
  id: String = UUID.randomUUID().toString,
  name: String = Person().lastName,
  users: List[User] = List(UserBuilder().build),
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
    id = Some(id),
    name = Some(name),
    users = Some(users),
    turns = Some(turns),
    productionLines = Some(productionLines),
    collectors = Some(collectors),
    stopGroups = Some(stopGroups),
    reworkGroups = Some(reworkGroups),
    wasteGroups = Some(wasteGroups),
    unitOfMeasurements = Some(unitOfMeasurements),
    createdAt = Some(createdAt),
    updatedAt = Some(updatedAt)
  )
}
