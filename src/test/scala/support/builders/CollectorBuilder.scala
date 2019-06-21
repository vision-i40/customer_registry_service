package support.builders

import java.util.UUID.randomUUID

import domain.models.{Collector, Devices}
import io.alphash.faker.Person
import org.joda.time.DateTime

case class CollectorBuilder(
  name: String = Person().firstNameFemale,
  uid: String = randomUUID().toString,
  deviceType: Devices.Value = Devices.WISE,
  authToken: String = randomUUID().toString,
  createdAt: Option[DateTime] = Some(DateTime.now),
  updatedAt: Option[DateTime] = Some(DateTime.now),
  id: Option[String] = Some(randomUUID().toString)
) {
  def build: Collector = Collector(
    uid = uid,
    deviceType = deviceType,
    authToken = authToken,
    createdAt = createdAt,
    updatedAt = updatedAt,
    id = id
  )
}
