package integration.infrastructure.mongodb

import infrastructure.config.MongoDBConfig
import infrastructure.mongodb.MongoDB
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.scalatest.concurrent.Eventually
import org.scalatest.time.{Seconds, Span}
import support.VisionSpec

class MongoDBTest extends VisionSpec with Eventually {
  implicit private val config: MongoDBConfig = MongoDBConfig
  private val db = MongoDB()
  implicit private val codec: CodecRegistry = DEFAULT_CODEC_REGISTRY

  behavior of "Mongo DB Connection"
  it should "connect to database provided db and collection" in {
    val collectionName = "any_collection"
    val collectionFuture = db.collection(collectionName)

    eventually(timeout(Span(10, Seconds))) {
      collectionFuture.value.get.isSuccess shouldEqual true
    }
  }

}
