package domain.repositories

import com.google.inject.{Inject, Singleton}
import domain.models.Company
import infrastructure.mongodb.MongoDB
import org.mongodb.scala.MongoCollection
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class CompanyCollection @Inject()(db: MongoDB) {
  private val COLLECTION_NAME = "companies"
  import domain.models.Company._

  def collectionFuture(): Future[MongoCollection[Company]] =
    db
      .collection[Company](COLLECTION_NAME)
      .map(_.withCodecRegistry(companyCodecRegistry))
}
