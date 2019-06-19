package domain.models

import org.joda.time.DateTime

trait CompanyResource {
  val id: Option[String]
  val createdAt: Option[DateTime]
  val updatedAt: Option[DateTime]
}
