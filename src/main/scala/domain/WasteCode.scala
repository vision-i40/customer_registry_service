package domain

import org.joda.time.DateTime

case class WasteCode(
  id: String,
  name: String,
  reasonClass: String,
  createdAt: DateTime,
  updatedAt: DateTime,
  description: Option[String] = None
)
