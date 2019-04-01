package support

import infrastructure.config.MongoDBConfig
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.result.DeleteResult
import org.mongodb.scala.{Completed, MongoClient, MongoCollection, MongoDatabase}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}
import scala.reflect.ClassTag

object MongoDbHelper {
  implicit private val config: MongoDBConfig = MongoDBConfig
  private lazy val mongoClient: MongoClient = MongoClient(config.connectionString)
  private lazy val database: MongoDatabase = mongoClient.getDatabase(config.database)

  import domain.Company._
  import domain.User._

  private val timeout: FiniteDuration = 10.seconds

  def collection[W:ClassTag](implicit collectionName: String): MongoCollection[W] = database
    .getCollection[W](collectionName)
    .withCodecRegistry(companyCodecRegistry)
    .withCodecRegistry(userCodecRegistry)

  def insert[T:ClassTag](registry: T)(implicit ec: ExecutionContext, collectionName: String): Option[Completed] = {
    Await.result(collection[T]
      .insertOne(registry)
      .toFutureOption(), timeout)
  }

  def clearCompanyCollection()(implicit collectionName: String): DeleteResult = {
    Await.result(collection
      .deleteMany(BsonDocument())
      .toFuture(), timeout)
  }
}
