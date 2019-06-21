package domain.models

import com.twitter.finatra.request.RouteParam
import org.joda.time.DateTime

case class StopCode(
  code: String,
  isManual: Boolean,
  isPlanned: Boolean,
  allowChangeInPendingStops: Boolean,
  createdAt: Option[DateTime] = None,
  updatedAt: Option[DateTime] = None,
  @RouteParam id: Option[String] = None
) extends CompanyResource
