package infrastructure.mongodb

import infrastructure.config.MongoDBConfig
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase}

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

class MongoDB(implicit config: MongoDBConfig, ec: ExecutionContext) {
  private lazy val mongoClient: MongoClient = MongoClient(config.connectionString)
  private lazy val database: MongoDatabase = mongoClient.getDatabase(config.database)

  def collection[W:ClassTag](name: String)(implicit codec: CodecRegistry): Future[MongoCollection[W]] = Future {
    database
      .withCodecRegistry(codec)
      .getCollection[W](name)
  }
}

object MongoDB {
  def apply()(implicit config: MongoDBConfig, ec: ExecutionContext): MongoDB = new MongoDB()
}
