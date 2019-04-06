package authentication

import authentication.models.AuthenticationToken
import com.google.inject.{Inject, Singleton}
import domain.User
import infrastructure.config.EncryptionConfig
import pdi.jwt.{Jwt, JwtAlgorithm}

@Singleton
class TokenBuilder @Inject()(encryptionConfig: EncryptionConfig) {
  def build(user: User): AuthenticationToken = {
    AuthenticationToken(Jwt.encode(generateUserData(user), encryptionConfig.secretKey, JwtAlgorithm.HS256))
  }

  private def generateUserData(user: User): String = s"""{"id":"${user.id}"}"""
}
