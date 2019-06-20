package support.builders

import java.util.UUID.randomUUID

import domain.models.Turn
import io.alphash.faker.{Lorem, Person}
import org.joda.time.{DateTime, LocalTime}

case class TurnBuilder(
  id: Option[String] = Some(randomUUID().toString),
  name: String = Person().firstNameFemale,
  startTime: LocalTime = LocalTime.now(),
  endTime: LocalTime = LocalTime.now().plusHours(2),
  description: Option[String] = Some(Lorem.wordList.mkString),
  createdAt: Option[DateTime] = Some(DateTime.now),
  updatedAt: Option[DateTime] = Some(DateTime.now)
) {
  def build: Turn = Turn(
    id = id,
    name = name,
    startTime = startTime,
    endTime = endTime,
    description = description,
    createdAt = createdAt,
    updatedAt = updatedAt
  )
}
