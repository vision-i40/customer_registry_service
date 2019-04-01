package authentication

case class SignupRequest(
  companyName: String,
  userEmail: String,
  userName: String,
  userPassword: String
)
