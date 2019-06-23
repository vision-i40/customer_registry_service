package authentication

import com.google.inject.{Inject, Singleton}
import infrastructure.mongodb.MongoDB
import org.mongodb.scala.MongoCollection
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class UserVerificationRepository @Inject()(db: MongoDB) {
  import UserVerification._
  private val COLLECTION_NAME = "user_verifications"

  private lazy val collectionFuture: Future[MongoCollection[UserVerification]] =
    db.collection[UserVerification](COLLECTION_NAME)

  def create(userVerification: UserVerification): Future[UserVerification] = {
    collectionFuture
      .flatMap { collection =>
        collection
          .insertOne(userVerification)
          .toFuture()
          .map(_ => userVerification)
      }
  }
}
