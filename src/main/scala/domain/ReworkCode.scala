package domain

import org.joda.time.DateTime

case class ReworkCode(
  id: String,
  name: String,
  reasonClazz: String,
  createAt: DateTime,
  updateAt: DateTime,
  description: Option[String] = None
)
