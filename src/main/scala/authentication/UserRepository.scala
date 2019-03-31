package authentication

import domain.{Company, User}
import infrastructure.mongodb.MongoDB
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.bson.BsonDocument

import scala.concurrent.{ExecutionContext, Future}

class UserRepository(implicit db: MongoDB, ec: ExecutionContext) {
  import Company._
  private val COLLECTION_NAME = "companies_configs"

  private lazy val collectionFuture: Future[MongoCollection[Company]] = db.collection[Company](COLLECTION_NAME)

  def getByEmailAndPassword(companyId: String, email: String, password: String): Future[Option[User]] = {
    val userMatch = "$elemMatch" -> BsonDocument(
      "email" -> email,
      "password" -> encryptPassword(password)
    )

    collectionFuture
      .flatMap(collection => {
        collection
          .find[Company](
          BsonDocument(
            "id" -> companyId,
            "users" -> BsonDocument(userMatch)
          ))
          .projection(BsonDocument("users" -> BsonDocument(userMatch)))
          .first()
          .map(extractUser)
          .toFuture()
        }
        .map(extractUserOption)
      )
  }

  private def encryptPassword(password: String): String = {
    // TODO: encrypt password
    password
  }

  private def extractUserOption(users: Seq[Option[User]]): Option[User] = {
    users match {
      case Seq(maybeUser) => maybeUser
      case _ => None
    }
  }

  private def extractUser(company: Company): Option[User] = {
    company.users match {
      case Some(List(user: User)) => Some(user)
      case _ => None
    }
  }
}

object UserRepository {
  def apply()(implicit db: MongoDB, ec: ExecutionContext): UserRepository = new UserRepository()

}
