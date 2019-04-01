package domain.repositories

import java.util.UUID
import java.util.UUID.randomUUID

import domain.{Company, User}
import infrastructure.config.EncryptionConfig
import infrastructure.mongodb.MongoDB
import org.mindrot.jbcrypt.BCrypt
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.bson.BsonDocument

import scala.concurrent.{ExecutionContext, Future}

class UserRepository(implicit db: MongoDB, ec: ExecutionContext) {
  import User._
  private val COLLECTION_NAME = "users"

  private lazy val collectionFuture: Future[MongoCollection[User]] = db.collection[User](COLLECTION_NAME)

  def getByEmailAndPassword(email: String, password: String)
                           (implicit config: EncryptionConfig): Future[Option[User]] = {
    collectionFuture
      .flatMap(collection => {
        collection
          .find[User](
          BsonDocument(
            "email" -> email,
            "password" -> encryptPassword(password)
          ))
          .first()
          .toFutureOption()
        }
      )
  }

  def create(email: String, username: String, password: String)
            (implicit company: Company): Future[User] = {
    val user = User(
      id = randomUUID().toString,
      companyId = company.id,
      email = email,
      username = username,
      password = password,
      isActive = true
    )

    collectionFuture
      .flatMap { collection =>
        collection
          .insertOne(user)
          .toFuture()
          .map(_ => user)
      }
  }

  private def encryptPassword(password: String)
                             (implicit config: EncryptionConfig): String = {
    BCrypt.hashpw(password, config.salt)
  }
}

object UserRepository {
  def apply()(implicit db: MongoDB, ec: ExecutionContext): UserRepository = new UserRepository()
}
