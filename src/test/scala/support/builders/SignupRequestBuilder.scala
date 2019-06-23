package support.builders

import authentication.dtos.SignUpRequest
import io.alphash.faker._

case class SignupRequestBuilder(
  email: String = Internet().email,
  username: String = Person().firstNameFemale,
  name: String = Person().firstNameFemale,
  password: String = "a-password"
) {
  def build: SignUpRequest = SignUpRequest(
    email = email,
    username = username,
    name = name,
    password = password
  )
}
