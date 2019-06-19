package support.builders

import java.util.UUID.randomUUID

import domain.models.ReworkCode
import io.alphash.faker.{Lorem, Person}
import org.joda.time.DateTime

case class ReworkCodeBuilder(
  name: String = Person().firstNameFemale,
  reasonClass: String = Person().firstNameFemale,
  description: Option[String] = Some(Lorem.wordList.mkString),
  createdAt: Option[DateTime] = Some(DateTime.now),
  updatedAt: Option[DateTime] = Some(DateTime.now),
  id: Option[String] = Some(randomUUID().toString)
) {
  def build: ReworkCode = {
    ReworkCode(
      name = name,
      reasonClass = reasonClass,
      description = description,
      createdAt = createdAt,
      updatedAt = updatedAt,
      id = id
    )
  }
}
