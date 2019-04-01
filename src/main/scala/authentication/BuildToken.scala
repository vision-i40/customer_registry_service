package authentication

import domain.User
import infrastructure.config.EncryptionConfig
import pdi.jwt.{Jwt, JwtAlgorithm}

object BuildToken {
  def apply(user: User)(implicit encryptionConfig: EncryptionConfig): AuthenticationToken = {
    AuthenticationToken(Jwt.encode(generateUserData(user), encryptionConfig.secretKey, JwtAlgorithm.HS256))
  }

  private def generateUserData(user: User): String = s"""{"id":"${user.id}"}"""
}
