package support.builders

import java.util.UUID

import domain.models.{WasteCode, WasteGroup}
import io.alphash.faker.{Lorem, Person}
import org.joda.time.DateTime

case class WasteGroupBuilder(
  name: String = Person().firstNameFemale,
  wasteCodes: List[WasteCode] = List(WasteCodeBuilder().build),
  description: Option[String] = Some(Lorem.wordList.mkString),
  createdAt: Option[DateTime] = Some(DateTime.now),
  updatedAt: Option[DateTime] = Some(DateTime.now),
  id: Option[String] = Some(UUID.randomUUID().toString)
) {
  def build: WasteGroup = {
    WasteGroup(
      name = name,
      wasteCodes = wasteCodes,
      description = description,
      createdAt = createdAt,
      updatedAt = updatedAt,
      id = id
    )
  }
}
