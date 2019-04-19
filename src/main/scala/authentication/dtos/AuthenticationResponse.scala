package authentication.dtos

import domain.models.{Company, User}

case class AuthenticationResponse(
  token: String,
  user: User,
  company: Company
)
