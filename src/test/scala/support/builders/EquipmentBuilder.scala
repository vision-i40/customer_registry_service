package support.builders

import java.util.UUID.randomUUID

import domain.models.Equipment
import io.alphash.faker.{Lorem, Person}
import org.joda.time.DateTime

case class EquipmentBuilder(
  id: Option[String] = Some(randomUUID().toString),
  name: String = Person().firstNameFemale,
  createdAt: Option[DateTime] = Some(DateTime.now),
  updatedAt: Option[DateTime] = Some(DateTime.now),
  description: Option[String] = Some(Lorem.wordList.mkString)
) {
  def build: Equipment = Equipment(
    id = id,
    name = name,
    createdAt = createdAt,
    updatedAt = updatedAt,
    description = description
  )
}
