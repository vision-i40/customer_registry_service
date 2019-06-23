package authentication.dtos

import org.joda.time.DateTime

case class AuthenticationResponse(
  token: String,
  expiresAt: DateTime
)
