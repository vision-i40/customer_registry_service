package domain.models

import com.twitter.finatra.request.RouteParam
import org.joda.time.DateTime

case class ReworkCode(
  name: String,
  reasonClass: String,
  description: Option[String] = None,
  createdAt: Option[DateTime] = None,
  updatedAt: Option[DateTime] = None,
  @RouteParam id: Option[String] = None
) extends CompanyResource
