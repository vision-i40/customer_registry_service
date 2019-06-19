package support.builders

import java.util.UUID.randomUUID

import domain.models.UnitOfMeasurement
import io.alphash.faker.Person
import io.alphash.faker.Lorem
import org.joda.time.DateTime

case class UnitOfMeasurementBuilder(
  id: Option[String] = Some(randomUUID().toString),
  name: String = Person().firstNameFemale,
  conversionFactor: Double = 0.8,
  description: Option[String] = Some(Lorem.wordList.mkString),
  createdAt: Option[DateTime] = Some(DateTime.now),
  updatedAt: Option[DateTime] = Some(DateTime.now)
) {
  def build: UnitOfMeasurement = UnitOfMeasurement(
    id = id,
    name = name,
    conversionFactor = conversionFactor,
    description = description,
    createdAt = createdAt,
    updatedAt = updatedAt
  )
}
