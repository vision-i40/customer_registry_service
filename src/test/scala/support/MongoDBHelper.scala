package support

import infrastructure.config.MongoDBConfig
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.result.DeleteResult
import org.mongodb.scala.{Completed, MongoClient, MongoCollection, MongoDatabase}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.reflect.ClassTag

object MongoDBHelper {
  implicit private val config: MongoDBConfig = MongoDBConfig
  private lazy val mongoClient: MongoClient = MongoClient(config.connectionString)
  private lazy val database: MongoDatabase = mongoClient.getDatabase(config.database)

  private val timeout: FiniteDuration = 10.seconds

  def collection[W:ClassTag](implicit collectionName: String): MongoCollection[W] =
    database
      .getCollection[W](collectionName)

  def insert[T:ClassTag](registry: T)
                        (implicit ec: ExecutionContext,
                         collectionName: String,
                         codec: CodecRegistry): Option[Completed] = {
    Await.result(collection[T]
      .withCodecRegistry(codec)
      .insertOne(registry)
      .toFutureOption(), timeout)
  }

  def find[T:ClassTag](filter: BsonDocument)
                      (implicit ec: ExecutionContext,
                       collectionName: String,
                       codec: CodecRegistry): Future[Seq[T]] = {
    collection[T]
      .withCodecRegistry(codec)
      .find[T](filter)
      .toFuture()
  }

  def clearCollection()(implicit collectionName: String): DeleteResult = {
    Await.result(collection
      .deleteMany(BsonDocument())
      .toFuture(), timeout)
  }
}
