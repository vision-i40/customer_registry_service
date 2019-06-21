package domain.models

import com.twitter.finatra.request.RouteParam
import org.joda.time.DateTime

case class Channel(
  number: Int,
  channelType: ChannelType.Value,
  isCumulative: Boolean,
  createdAt: Option[DateTime] = None,
  updatedAt: Option[DateTime] = None,
  @RouteParam id: Option[String] = None
) extends CompanyResource
