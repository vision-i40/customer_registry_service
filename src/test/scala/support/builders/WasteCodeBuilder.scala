package support.builders

import java.util.UUID

import domain.models.WasteCode
import io.alphash.faker.{Lorem, Person}
import org.joda.time.DateTime

case class WasteCodeBuilder(
  name: String = Person().firstNameFemale,
  reasonClass: String = "a-reason-class",
  description: Option[String] = Some(Lorem.wordList.mkString),
  createdAt: Option[DateTime] = Some(DateTime.now),
  updatedAt: Option[DateTime] = Some(DateTime.now),
  id: Option[String] = Some(UUID.randomUUID().toString)
) {
  def build: WasteCode = WasteCode(
    name = name,
    reasonClass = reasonClass,
    description = description,
    createdAt = createdAt,
    updatedAt = updatedAt,
    id = id
  )
}
