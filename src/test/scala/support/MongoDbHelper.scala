package support

import domain.Company
import infrastructure.config.MongoDBConfig
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.result.DeleteResult
import org.mongodb.scala.{Completed, MongoClient, MongoDatabase}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

object MongoDbHelper {
  private val COLLECTION_NAME = "companies_configs"
  implicit private val config: MongoDBConfig = MongoDBConfig
  private lazy val mongoClient: MongoClient = MongoClient(config.connectionString)
  private lazy val database: MongoDatabase = mongoClient.getDatabase(config.database)

  import domain.Company._

  private val collection = database
    .getCollection[Company](COLLECTION_NAME)
    .withCodecRegistry(companyCodecRegistry)

  private val timeout: FiniteDuration = 10.seconds

  def insert(company: Company)(implicit ec: ExecutionContext): Option[Completed] = {
    Await.result(collection
      .insertOne(company)
      .toFutureOption(), timeout)
  }

  def clearCompanyCollection(): DeleteResult = {
    Await.result(collection
      .deleteMany(BsonDocument())
      .toFuture(), timeout)
  }
}
