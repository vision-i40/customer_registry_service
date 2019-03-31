package domain

import org.joda.time.DateTime

case class User(
  id: String,
  email: String,
  username: String,
  password: String,
  isActive: Boolean,
  createdAt: DateTime,
  updatedAt: DateTime
)
