package company_admin.requests

import com.twitter.finatra.request.RouteParam

case class NestedResourceRequest (@RouteParam id: String, @RouteParam parent_id: String)