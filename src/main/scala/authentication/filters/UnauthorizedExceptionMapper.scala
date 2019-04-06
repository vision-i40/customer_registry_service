package authentication.filters

import authentication.exceptions.UnauthorizedException
import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.exceptions.ExceptionMapper
import com.twitter.finatra.http.response.ResponseBuilder

@Singleton
class UnauthorizedExceptionMapper @Inject()(response: ResponseBuilder)
  extends ExceptionMapper[UnauthorizedException] {

  override def toResponse(request: Request, exception: UnauthorizedException): Response = {
    response.unauthorized(s"Unauthorized - ${exception.getMessage}")
  }
}
