package domain.models

import org.joda.time.DateTime

case class ReworkGroup(
  id: String,
  name: String,
  reworkCodes: List[ReworkCode],
  updateAt: DateTime,
  createAt: DateTime,
  description: Option[String] = None
)
