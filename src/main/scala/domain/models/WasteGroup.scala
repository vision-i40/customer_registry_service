package domain.models

import org.joda.time.DateTime

case class WasteGroup (
  id: String,
  name: String,
  wasteCodes: List[WasteCode],
  createdAt: DateTime,
  updatedAt: DateTime,
  description: Option[String] = None
)
