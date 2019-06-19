package domain.models

import com.twitter.finatra.request.RouteParam
import org.joda.time.DateTime

case class ProductionLine(
  name: String,
  oeeGoal: Double,
  resetProduction: Boolean,
  discountRework: Boolean,
  discountWaste: Boolean,
  createdAt: Option[DateTime] = None,
  updatedAt: Option[DateTime] = None,
  @RouteParam id: Option[String] = None
) extends CompanyResource
