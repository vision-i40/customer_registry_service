package integration.domain.repositories

import domain.models.Company
import domain.repositories.{CompanyCollection, ProductionLineRepository, UnitOfMeasurementRepository}
import infrastructure.config.MongoDBConfig
import infrastructure.mongodb.MongoDB
import org.mongodb.scala.bson.BsonDocument
import org.scalatest.BeforeAndAfterEach
import support.{MongoDBHelper, VisionSpec}
import support.builders.{CompanyBuilder, ProductionLineBuilder, UnitOfMeasurementBuilder}

class UnitOfMeasurementRepositoryTest extends VisionSpec with BeforeAndAfterEach {
  implicit private val collectionName: String = "companies"
  private val mongoConfig: MongoDBConfig = new MongoDBConfig()
  private val db: MongoDB = new MongoDB(mongoConfig)
  private val companyCollection = new CompanyCollection(db)
  private val repository = new UnitOfMeasurementRepository(companyCollection)

  import domain.models.Company._

  override def beforeEach(): Unit = {
    super.beforeEach()
    MongoDBHelper.clearCollection()
  }

  behavior of "UnitOfMeasurementRepositoryTest"

  it should "add a unit of measurement when unit of measurement array is empty" in {
    implicit val rootCompany: Company = CompanyBuilder().build
    val unitOfMeasurement = UnitOfMeasurementBuilder().build

    MongoDBHelper.insert[Company](rootCompany)

    repository
      .create(
        name = unitOfMeasurement.name,
        conversionFactor = unitOfMeasurement.conversionFactor,
        description = unitOfMeasurement.description
      )
      .flatMap { _ =>
        MongoDBHelper
          .find[Company](BsonDocument("id" -> rootCompany.id))
          .map { updatedCompanies =>
            val productionLines = updatedCompanies.head.unitsOfMeasurement

            productionLines should have size 1
            val insertedProductionLine = productionLines.head

            insertedProductionLine.name shouldEqual unitOfMeasurement.name
            insertedProductionLine.conversionFactor shouldEqual unitOfMeasurement.conversionFactor
            insertedProductionLine.createdAt.plusSeconds(2).isAfterNow shouldEqual true
            insertedProductionLine.updatedAt.plusSeconds(2).isAfterNow shouldEqual true
          }
      }
  }

}
