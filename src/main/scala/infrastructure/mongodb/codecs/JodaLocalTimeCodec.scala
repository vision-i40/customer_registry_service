package infrastructure.mongodb.codecs

import org.bson.codecs.{Codec, DecoderContext, EncoderContext}
import org.bson.{BsonReader, BsonWriter}
import org.joda.time.LocalTime

class JodaLocalTimeCodec extends Codec[LocalTime] {
  override def decode(bsonReader: BsonReader, decoderContext: DecoderContext): LocalTime = {
    LocalTime.fromMillisOfDay(bsonReader.readInt32)
  }

  override def encode(bsonWriter: BsonWriter, t: LocalTime, encoderContext: EncoderContext): Unit = {
    bsonWriter.writeInt32(t.getMillisOfDay)
  }

  override def getEncoderClass: Class[LocalTime] = classOf[LocalTime]
}
