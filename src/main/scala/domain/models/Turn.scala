package domain.models

import org.joda.time.{DateTime, LocalTime}

case class Turn(
  name: String,
  startTime: LocalTime,
  endTime: LocalTime,
  description: Option[String] = None,
  id: Option[String] = None,
  createdAt: Option[DateTime] = None,
  updatedAt: Option[DateTime] = None
) extends CompanyResource
