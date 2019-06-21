package domain.models

import com.twitter.finatra.request.RouteParam
import org.joda.time.DateTime

case class Collector(
  uid: String,
  deviceType: Devices.Value,
  authToken: String,
  createdAt: Option[DateTime] = None,
  updatedAt: Option[DateTime] = None,
  @RouteParam id: Option[String] = None
) extends CompanyResource
