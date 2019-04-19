package authentication.filters

import authentication.exceptions.BadRequestException
import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.exceptions.ExceptionMapper
import com.twitter.finatra.http.response.ResponseBuilder

@Singleton
class BadRequestMapper @Inject()(response: ResponseBuilder)
  extends ExceptionMapper[BadRequestException] {

  override def toResponse(request: Request, exception: BadRequestException): Response = {
    response.unauthorized(s"Bad request - ${exception.getMessage}")
  }
}

