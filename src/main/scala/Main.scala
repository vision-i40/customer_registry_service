import authentication.AuthenticationController
import authentication.filters.{AuthenticatedUserFilter, UnauthorizedExceptionMapper}
import com.twitter.finagle.http.filter.Cors
import com.twitter.finagle.http.filter.Cors.HttpFilter
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.{CommonFilters, ExceptionMappingFilter, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.inject.requestscope.FinagleRequestScopeFilter
import company_admin.ProductionLineController
import infrastructure.CorsController
import user.UserController

object Main extends HttpServer {
  override def configureHttp(router: HttpRouter): Unit = {
    router
      .exceptionMapper[UnauthorizedExceptionMapper]
      .filter[LoggingMDCFilter[Request, Response]]
      .filter[TraceIdMDCFilter[Request, Response]]
      .filter[CommonFilters]
      .filter(new HttpFilter(Cors.UnsafePermissivePolicy))
      .filter[ExceptionMappingFilter[Request]]
      .filter[FinagleRequestScopeFilter[Request, Response]]
      .add[CorsController]
      .add[AuthenticationController]
      .add[AuthenticatedUserFilter, ProductionLineController]
      .add[AuthenticatedUserFilter, UserController]
  }

}
