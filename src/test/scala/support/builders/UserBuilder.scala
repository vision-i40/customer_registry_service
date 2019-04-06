package support.builders

import java.util.UUID
import java.util.UUID.randomUUID

import io.alphash.faker._
import domain.User
import infrastructure.config.EncryptionConfig
import org.joda.time.DateTime
import org.mindrot.jbcrypt.BCrypt

case class UserBuilder(
  id: String = randomUUID().toString,
  companyIds: List[String] = List(randomUUID().toString),
  email: String = Internet().email,
  username: String = Person().firstNameFemale,
  password: String = BCrypt.hashpw("password", (new EncryptionConfig).salt),
  isActive: Boolean = true,
  createdAt: DateTime = DateTime.now,
  updatedAt: DateTime = DateTime.now
) {
  def build: User = User(
    id = id,
    companyIds = companyIds,
    email = email,
    username = username,
    password = password,
    isActive = isActive,
    createdAt = createdAt,
    updatedAt = updatedAt
  )
}
