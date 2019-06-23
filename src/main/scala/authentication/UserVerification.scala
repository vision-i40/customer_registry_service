package authentication

import infrastructure.mongodb.codecs.JodaDateTimeCodec
import org.joda.time.DateTime

case class UserVerification(
  token: String,
  expiresAt: DateTime,
  wasUsed: Boolean = false,
  createdAt: DateTime,
  activatedAt: Option[DateTime] = None
)

object UserVerification {
  import org.bson.codecs.configuration.CodecRegistries.{fromCodecs, fromProviders, fromRegistries}
  import org.bson.codecs.configuration.CodecRegistry
  import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
  import org.mongodb.scala.bson.codecs.Macros._

  implicit val userCodecRegistry: CodecRegistry = fromRegistries(
    fromProviders(classOf[UserVerification]),
    fromCodecs(new JodaDateTimeCodec),
    DEFAULT_CODEC_REGISTRY
  )
}