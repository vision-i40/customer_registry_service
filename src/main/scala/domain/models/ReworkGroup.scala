package domain.models

import org.joda.time.DateTime

case class ReworkGroup(
  name: String,
  description: Option[String] = None,
  updatedAt: Option[DateTime] = None,
  createdAt: Option[DateTime] = None,
  id: Option[String] = None
) extends CompanyResource
