package authentication.dtos

case class SignUpRequest(
  email: String,
  name: String,
  username: String,
  password: String
)
