package support.builders

import java.util.UUID

import domain.models.StopCode
import org.joda.time.DateTime

case class StopCodeBuilder(
  code: String = UUID.randomUUID().toString,
  isManual: Boolean = true,
  isPlanned: Boolean = true,
  allowChangeInPendingStops: Boolean = true,
  createdAt: Option[DateTime] = Some(DateTime.now),
  updatedAt: Option[DateTime] = Some(DateTime.now),
  id: Option[String] = Some(UUID.randomUUID().toString)
) {
  def build: StopCode = StopCode(
    code = code,
    isManual = isManual,
    isPlanned = isPlanned,
    allowChangeInPendingStops = allowChangeInPendingStops,
    createdAt = createdAt,
    updatedAt = updatedAt,
    id = id
  )
}
