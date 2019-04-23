package infrastructure.mongodb

import com.google.inject.{Inject, Singleton}
import com.mongodb.{Block, ConnectionString}
import infrastructure.config.MongoDBConfig
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.connection.{NettyStreamFactoryFactory, SslSettings}
import org.mongodb.scala.{MongoClient, MongoClientSettings, MongoCollection, MongoDatabase}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.reflect.ClassTag

@Singleton
class MongoDB @Inject()(config: MongoDBConfig) {
  private val sslSettings = SslSettings.builder().enabled(config.isSslEnabled).build()

  private val mongoClientSettings = MongoClientSettings.builder()
    .applyConnectionString(new ConnectionString(config.connectionString))
    .applyToSslSettings(
      new Block[SslSettings.Builder]() {
        override def apply(builder: SslSettings.Builder): Unit = {
          builder.applySettings(sslSettings)
        }
      }
    )
    .streamFactoryFactory(NettyStreamFactoryFactory.builder().build())
    .build()

  lazy val mongoClient: MongoClient = MongoClient(mongoClientSettings)
  lazy val database: MongoDatabase = mongoClient.getDatabase(config.database)

  def collection[W:ClassTag](name: String)(implicit codec: CodecRegistry): Future[MongoCollection[W]] = Future {
    database
      .withCodecRegistry(codec)
      .getCollection[W](name)
  }
}
