package support.build

import java.util.UUID

import io.alphash.faker._
import domain.User
import org.joda.time.DateTime

case class UserBuilder(
  id: String = UUID.randomUUID().toString,
  email: String = Internet().email,
  username: String = Person().firstNameFemale,
  password: String = Internet().password,
  isActive: Boolean = true,
  createdAt: DateTime = DateTime.now,
  updatedAt: DateTime = DateTime.now
) {

  def build: User = User(
    id = id,
    email = email,
    username = username,
    password = password,
    isActive = isActive,
    createdAt = createdAt,
    updatedAt = updatedAt
  )
}
