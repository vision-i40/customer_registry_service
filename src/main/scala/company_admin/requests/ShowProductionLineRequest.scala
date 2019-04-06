package company_admin.requests

import com.twitter.finatra.request.RouteParam

case class ShowProductionLineRequest(@RouteParam id: String)
