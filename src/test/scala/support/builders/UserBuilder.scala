package support.builders

import java.util.UUID.randomUUID

import domain.models.User
import infrastructure.config.AuthConfig
import io.alphash.faker._
import org.joda.time.DateTime
import org.mindrot.jbcrypt.BCrypt

case class UserBuilder(
  id: String = randomUUID().toString,
  defaultCompanyId: Option[String] = Some(randomUUID().toString),
  email: String = Internet().email,
  username: String = Person().firstNameFemale,
  name: String = Person().firstNameFemale,
  isActive: Boolean = true,
  createdAt: DateTime = DateTime.now,
  updatedAt: DateTime = DateTime.now,
  password: String = BCrypt.hashpw("password", (new AuthConfig).bcryptSalt)
) {
  def build: User = User(
    id = id,
    defaultCompanyId = defaultCompanyId,
    email = email,
    username = username,
    name = name,
    isActive = isActive,
    createdAt = createdAt,
    updatedAt = updatedAt,
    password = password
  )
}
