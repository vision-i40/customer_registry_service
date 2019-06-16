package company_admin.requests

import com.twitter.finatra.request.RouteParam

case class SingleResourceRequest(@RouteParam id: String)
