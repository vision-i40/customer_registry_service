package authentication

import domain.User
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

  private def encryptPassword(password: String)
                             (implicit config: EncryptionConfig): String = {
    BCrypt.hashpw(password, config.salt)
  }
}

object UserRepository {
  def apply()(implicit db: MongoDB, ec: ExecutionContext): UserRepository = new UserRepository()
}
