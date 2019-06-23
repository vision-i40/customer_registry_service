package support.builders

import java.util.UUID.randomUUID

import authentication.UserVerification
import org.joda.time.DateTime

case class UserVerificationBuilder(
  token: String = randomUUID().toString,
  wasUsed: Boolean = false,
  createdAt: DateTime = DateTime.now,
  expiresAt: DateTime = DateTime.now,
  activatedAt: Option[DateTime] = None
) {
  def build: UserVerification = UserVerification(
    token = token,
    wasUsed = wasUsed,
    expiresAt = expiresAt,
    createdAt = createdAt,
    activatedAt = activatedAt
  )
}
