package domain.models

import org.joda.time.DateTime

case class Turn(
  name: String,
  startTime: DateTime,
  endTime: DateTime,
  description: Option[String] = None,
  id: Option[String] = None,
  createdAt: Option[DateTime] = None,
  updatedAt: Option[DateTime] = None
) extends CompanyResource
