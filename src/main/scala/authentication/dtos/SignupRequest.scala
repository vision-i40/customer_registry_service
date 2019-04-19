package authentication.dtos

case class SignupRequest(
  companyName: String,
  companySlug: String,
  userEmail: String,
  userName: String,
  userPassword: String
)
