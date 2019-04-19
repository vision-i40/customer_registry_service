package support.builders

import java.util.UUID.randomUUID
import domain.models.User
import infrastructure.config.EncryptionConfig
import io.alphash.faker._
import org.joda.time.DateTime
import org.mindrot.jbcrypt.BCrypt

case class UserBuilder(
  id: String = randomUUID().toString,
  companyIds: List[String] = List(randomUUID().toString),
  defaultCompanyId: String = randomUUID().toString,
  email: String = Internet().email,
  username: String = Person().firstNameFemale,
  isActive: Boolean = true,
  createdAt: DateTime = DateTime.now,
  updatedAt: DateTime = DateTime.now,
  password: Option[String] = Some(BCrypt.hashpw("password", (new EncryptionConfig).salt))
) {
  def build: User = User(
    id = id,
    companyIds = companyIds,
    defaultCompanyId = defaultCompanyId,
    email = email,
    username = username,
    isActive = isActive,
    createdAt = createdAt,
    updatedAt = updatedAt,
    password = password
  )
}
