package domain

import infrastructure.mongodb.codecs.JodaCodec
import org.joda.time.DateTime

case class Company(
  id: String,
  name: String,
  productionLines: List[ProductionLine],
  turns: List[Turn],
  collectors: List[Collector],
  stopGroups: List[StopGroup],
  reworkGroups: List[ReworkGroup],
  wasteGroups: List[WasteGroup],
  unitOfMeasurements: List[UnitOfMeasurement],
  createdAt: DateTime,
  updatedAt: DateTime
)

object Company {
  import org.bson.codecs.configuration.CodecRegistries.{fromCodecs, fromProviders, fromRegistries}
  import org.bson.codecs.configuration.CodecRegistry
  import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
  import org.mongodb.scala.bson.codecs.Macros._

  implicit val companyCodecRegistry: CodecRegistry = fromRegistries(
    fromProviders(
      classOf[Company],
      classOf[ProductionLine],
      classOf[Turn],
      classOf[Collector],
      classOf[Channel],
      classOf[ReworkGroup],
      classOf[ReworkCode],
      classOf[StopGroup],
      classOf[StopCode],
      classOf[WasteGroup],
      classOf[WasteCode],
      classOf[UnitOfMeasurement]
    ),
    fromCodecs(new JodaCodec),
    DEFAULT_CODEC_REGISTRY
  )
}
