package infrastructure.mongodb

import com.google.inject.{Inject, Singleton}
import infrastructure.config.MongoDBConfig
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.reflect.ClassTag

@Singleton
class MongoDB @Inject()(config: MongoDBConfig) {
  private lazy val mongoClient: MongoClient = MongoClient(config.connectionString)
  private lazy val database: MongoDatabase = mongoClient.getDatabase(config.database)

  def collection[W:ClassTag](name: String)(implicit codec: CodecRegistry): Future[MongoCollection[W]] = Future {
    database
      .withCodecRegistry(codec)
      .getCollection[W](name)
  }
}
