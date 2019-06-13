package domain.repositories

import java.util.UUID.randomUUID

import com.google.inject.{Inject, Singleton}
import domain.models.{Company, UnitOfMeasurement}
import org.joda.time.DateTime
import org.mongodb.scala.bson.{BsonDateTime, BsonDocument}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class UnitOfMeasurementRepository @Inject()(companyCollection: CompanyCollection) {
  def create(name: String, conversionFactor: Double, description: Option[String])
            (implicit company: Company): Future[UnitOfMeasurement] = {
    val unitOfMeasurement = buildUnitOfMeasurements(name, conversionFactor, description)

    companyCollection
      .collectionFuture
      .flatMap { collection =>
        collection
          .updateOne(BsonDocument("id" -> company.id),
            BsonDocument(
              "$push" -> BsonDocument(
                "unitsOfMeasurement" -> buildUnitOfMeasurementsDocument(unitOfMeasurement)
              )
            ))
          .toFuture()
          .map{ _ => unitOfMeasurement}
      }
  }

  private def buildUnitOfMeasurementsDocument(unitOfMeasurement: UnitOfMeasurement): BsonDocument = {
    BsonDocument(
      "id" -> unitOfMeasurement.id,
      "name" -> unitOfMeasurement.name,
      "conversionFactor" -> unitOfMeasurement.conversionFactor,
      "createdAt" -> BsonDateTime(unitOfMeasurement.createdAt.getMillis),
      "updatedAt" -> BsonDateTime(unitOfMeasurement.updatedAt.getMillis)
    )
  }

  private def buildUnitOfMeasurements(name: String, conversionFactor: Double,
                                              description: Option[String]): UnitOfMeasurement = {
    UnitOfMeasurement(
      id = randomUUID().toString,
      name = name,
      conversionFactor = conversionFactor,
      description = description,
      createdAt = DateTime.now,
      updatedAt = DateTime.now
    )
  }
}
