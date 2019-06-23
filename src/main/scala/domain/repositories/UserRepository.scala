package domain.repositories

import com.google.inject.{Inject, Singleton}
import domain.models.User
import infrastructure.config.AuthConfig
import infrastructure.mongodb.MongoDB
import org.mindrot.jbcrypt.BCrypt
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.bson.BsonDocument

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class UserRepository @Inject()(db: MongoDB, config: AuthConfig) {
  import User._
  private val COLLECTION_NAME = "users"

  private lazy val collectionFuture: Future[MongoCollection[User]] = db.collection[User](COLLECTION_NAME)

  def getByEmailAndPassword(email: String, password: String): Future[Option[User]] = {
    findBy(BsonDocument("email" -> email, "password" -> encryptPassword(password)))
  }

  def findById(id: String): Future[Option[User]] = {
    findBy(BsonDocument("id" -> id))
  }

  def findBy(filter: BsonDocument): Future[Option[User]] = {
    collectionFuture
      .flatMap(collection => {
        collection
          .find[User](filter)
          .first()
          .toFutureOption()
      })
  }

  def create(user: User): Future[User] = {
    collectionFuture
      .flatMap { collection =>
        collection
          .insertOne(user)
          .toFuture()
          .map(_ => user)
      }
  }

  private def encryptPassword(password: String): String = {
    BCrypt.hashpw(password, config.bcryptSalt)
  }
}