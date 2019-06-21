package support.builders

import java.util.UUID

import domain.models.{StopCode, StopGroup}
import io.alphash.faker.{Lorem, Person}
import org.joda.time.DateTime

case class StopGroupBuilder(
  name: String = Person().firstNameFemale,
  stopCodes: List[StopCode] = List(StopCodeBuilder().build),
  description: Option[String] = Some(Lorem.wordList.mkString),
  createdAt: Option[DateTime] = Some(DateTime.now),
  updatedAt: Option[DateTime] = Some(DateTime.now),
  id: Option[String] = Some(UUID.randomUUID().toString)
) {
  def build: StopGroup = {
    StopGroup(
      name = name,
      stopCodes = stopCodes,
      description = description,
      createdAt = createdAt,
      updatedAt = updatedAt,
      id = id
    )
  }
}
