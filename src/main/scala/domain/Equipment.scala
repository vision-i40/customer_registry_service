package domain

import org.joda.time.DateTime

case class Equipment(
  name: String,
  createdAt: DateTime,
  updatedAt: DateTime,
  description: Option[String] = None
)
