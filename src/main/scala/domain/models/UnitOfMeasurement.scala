package domain.models

import com.twitter.finatra.request.RouteParam
import org.joda.time.DateTime

case class UnitOfMeasurement(
  name: String,
  conversionFactor: Double,
  description: Option[String] = None,
  updatedAt: Option[DateTime] = None,
  createdAt: Option[DateTime] = None,
  @RouteParam id: Option[String] = None
) extends CompanyResource
