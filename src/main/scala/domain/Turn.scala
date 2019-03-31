package domain

import org.joda.time.DateTime

case class Turn(
  id: String,
  name: String,
  startTime: DateTime,
  endTime: DateTime,
  createdAt: DateTime,
  updatedAt: DateTime,
  description: Option[String] = None
)
