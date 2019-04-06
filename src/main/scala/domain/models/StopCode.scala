package domain.models

import org.joda.time.DateTime

case class StopCode(
  id: String,
  code: String,
  isManual: Boolean,
  isPlanned: Boolean,
  allow_change_in_pending_stops: Boolean,
  createdAt: DateTime,
  updatedAt: DateTime
)
