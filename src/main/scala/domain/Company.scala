package domain

import infrastructure.mongodb.codecs.JodaCodec
import org.joda.time.DateTime

case class Company(
  id: Option[String] = None,
  name: Option[String] = None,
  users: Option[List[User]] = None,
  productionLines: Option[List[ProductionLine]] = None,
  turns: Option[List[Turn]] = None,
  collectors: Option[List[Collector]] = None,
  stopGroups: Option[List[StopGroup]] = None,
  reworkGroups: Option[List[ReworkGroup]] = None,
  wasteGroups: Option[List[WasteGroup]] = None,
  unitOfMeasurements: Option[List[UnitOfMeasurement]] = None,
  createdAt: Option[DateTime] = None,
  updatedAt: Option[DateTime] = None
)

object Company {
  import org.bson.codecs.configuration.CodecRegistries.{fromCodecs, fromProviders, fromRegistries}
  import org.bson.codecs.configuration.CodecRegistry
  import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
  import org.mongodb.scala.bson.codecs.Macros._

  implicit val companyCodecRegistry: CodecRegistry = fromRegistries(
    fromProviders(
      classOf[Company],
      classOf[User],
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
