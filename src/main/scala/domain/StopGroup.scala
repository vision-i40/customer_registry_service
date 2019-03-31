package domain

import org.joda.time.DateTime

case class StopGroup(
  id: String,
  name: String,
  stopCodes: List[StopCode],
  createdAt: DateTime,
  updatedAt: DateTime,
  description: Option[String] = None
)
