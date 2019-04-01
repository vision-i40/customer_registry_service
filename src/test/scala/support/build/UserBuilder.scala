package support.build

import java.util.UUID

import io.alphash.faker._
import domain.User
import infrastructure.config.EncryptionConfig
import org.joda.time.DateTime
import org.mindrot.jbcrypt.BCrypt

case class UserBuilder(
  id: String = UUID.randomUUID().toString,
  companyId: String = UUID.randomUUID().toString,
  email: String = Internet().email,
  username: String = Person().firstNameFemale,
  password: String = BCrypt.hashpw("password", EncryptionConfig.salt),
  isActive: Boolean = true,
  createdAt: DateTime = DateTime.now,
  updatedAt: DateTime = DateTime.now
) {
  def build: User = User(
    id = id,
    companyId = companyId,
    email = email,
    username = username,
    password = password,
    isActive = isActive,
    createdAt = createdAt,
    updatedAt = updatedAt
  )
}
