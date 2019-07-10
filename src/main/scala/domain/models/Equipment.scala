package domain.models

import com.twitter.finatra.request.RouteParam
import org.joda.time.DateTime

case class Equipment(
  name: String,
  createdAt: Option[DateTime] = None,
  updatedAt: Option[DateTime] = None,
  description: Option[String] = None,
  @RouteParam id:Option[String] = None
) extends CompanyResource
