package authentication.filters

import authentication.exceptions.UnauthorizedException
import authentication.models.{AuthenticatedUser, JwtPayload}
import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.inject.Logging
import com.twitter.inject.requestscope.FinagleRequestScope
import com.twitter.util.{Future => TwitterFuture}
import domain.models.{Company, User}
import domain.repositories.{CompanyRepository, UserRepository}
import infrastructure.FutureConversions._
import infrastructure.config.EncryptionConfig
import pdi.jwt.{Jwt, JwtAlgorithm}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future => ScalaFuture}
import scala.util.Try

@Singleton
class AuthenticatedUserFilter @Inject()(encryptionConfig: EncryptionConfig,
                                        userRepository: UserRepository,
                                        companyRepository: CompanyRepository,
                                        userScope: AuthenticatedUser)
  extends SimpleFilter[Request, Response] with Logging {
  private val TOKEN_HEADER_PREFIX = "Bearer "
  private val INVALID_TOKEN_LOG = "An invalid Token was provided for user/company."
  private val NO_TOKEN_PROVIDED = "No token was provided."

  override def apply(request: Request, service: Service[Request, Response]): TwitterFuture[Response] = {
    val companySlug = request.getParam("company_slug")

    request.authorization match {
      case Some(token) => injectUserInScope(token, companySlug).flatMap(_ => service(request))
      case _ =>
        info(NO_TOKEN_PROVIDED)
        throw UnauthorizedException(NO_TOKEN_PROVIDED)
    }
  }


  private def injectUserInScope(token: String, companySlug: String): TwitterFuture[AuthenticatedUser] = {
    ScalaFuture
      .fromTry(getJwtPayload(token))
      .flatMap { payload =>retrieveUserAndCompany(payload, companySlug)
      }.map {
        case (Some(user), Some(company)) if user.companyIds.contains(company.id) =>
          userScope
            .setUser(user)
            .setCompany(company)
        case _ =>
          info("Token provided does not match with any user/company in database.")
          throw UnauthorizedException(INVALID_TOKEN_LOG)
      }.recover {
        case e =>
          error(s"Error while trying to parse and retrieve user information. Exception: ${e.getMessage}")
          throw UnauthorizedException(INVALID_TOKEN_LOG)
      }.asTwitter
  }

  private def retrieveUserAndCompany(payload: JwtPayload, companySlug: String):
    ScalaFuture[(Option[User], Option[Company])] = {

    userRepository
      .findById(payload.id)
      .flatMap { maybeUser =>
        companyRepository
          .findBySlug(companySlug)
          .map { maybeCompany => (maybeUser, maybeCompany) }
      }
  }

  private def getJwtPayload(token: String): Try[JwtPayload] = {
    Jwt
      .decode(normalizeToken(token), encryptionConfig.secretKey, Seq(JwtAlgorithm.HS256))
      .map(JwtPayload.parse)
  }

  private def normalizeToken(token: String): String = {
    token.replace(TOKEN_HEADER_PREFIX, "")
  }
}

