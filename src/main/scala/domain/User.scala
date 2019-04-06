package domain

import infrastructure.mongodb.codecs.JodaCodec
import org.joda.time.DateTime

case class User(
  id: String,
  companyIds: List[String],
  email: String,
  username: String,
  password: String,
  isActive: Boolean,
  createdAt: DateTime,
  updatedAt: DateTime
)

object User {
  import org.bson.codecs.configuration.CodecRegistries.{fromCodecs, fromProviders, fromRegistries}
  import org.bson.codecs.configuration.CodecRegistry
  import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
  import org.mongodb.scala.bson.codecs.Macros._

  implicit val userCodecRegistry: CodecRegistry = fromRegistries(
    fromProviders(classOf[User]),
    fromCodecs(new JodaCodec),
    DEFAULT_CODEC_REGISTRY
  )
}