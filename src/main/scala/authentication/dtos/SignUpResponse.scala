package authentication.dtos

import org.joda.time.DateTime

case class SignUpResponse(
  email: String,
  name: String,
  verificationExpireAt: DateTime
)
