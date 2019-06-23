package authentication

import java.time.Instant

import com.google.inject.{Inject, Singleton}
import domain.models.User
import infrastructure.config.AuthConfig
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}

@Singleton
class TokenBuilder @Inject()(authConfig: AuthConfig) {
  def build(user: User,
            expiresIn: Long = authConfig.tokenExpirationInSeconds,
            issuedAt: Instant = Instant.now): String = {
    Jwt.encode(
      JwtClaim(
        content = generateUserData(user),
        issuedAt = Some(issuedAt.getEpochSecond)
      ).expiresIn(expiresIn),
      authConfig.secretKey,
      JwtAlgorithm.HS256
    )
  }

  private def generateUserData(user: User): String = s"""{"id":"${user.id}"}"""
}
