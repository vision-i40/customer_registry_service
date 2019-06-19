package integration.domain.repositories

import domain.models.Company
import domain.repositories.{CompanyCollection, UnitOfMeasurementRepository}
import infrastructure.config.MongoDBConfig
import infrastructure.mongodb.MongoDB
import org.mongodb.scala.bson.BsonDocument
import org.scalatest.BeforeAndAfterEach
import support.builders.{CompanyBuilder, UnitOfMeasurementBuilder}
import support.{MongoDBHelper, VisionAsyncSpec}

class UnitOfMeasurementRepositoryTest extends VisionAsyncSpec with BeforeAndAfterEach {
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

  behavior of "adding unit of measurement"
  it should "add a unit of measurement when unit of measurement array is empty" in {
    implicit val rootCompany: Company = CompanyBuilder().build
    val unitOfMeasurement = UnitOfMeasurementBuilder(
      name = "update payload",
      conversionFactor = 1.7,
      description = Some("update description")
    ).build

    MongoDBHelper.insert[Company](rootCompany)

    repository
      .create(unitOfMeasurement)
      .flatMap { _ =>
        MongoDBHelper
          .find[Company](BsonDocument("id" -> rootCompany.id))
          .map { updatedCompanies =>
            val unitsOfMeasurement = updatedCompanies.head.unitsOfMeasurement

            unitsOfMeasurement should have size 1
            val insertedUnitOfMeasurement = unitsOfMeasurement.head

            insertedUnitOfMeasurement.name shouldEqual unitOfMeasurement.name
            insertedUnitOfMeasurement.conversionFactor shouldEqual unitOfMeasurement.conversionFactor
            insertedUnitOfMeasurement.description shouldEqual unitOfMeasurement.description
          }
      }
  }


  behavior of "updating unit of measurement"
  it should "update resource of provide id" in {
    val firstUnitOfMeasurement = UnitOfMeasurementBuilder().build
    val secondUnitOfMeasurement = UnitOfMeasurementBuilder().build

    val noiseCompany: Company = CompanyBuilder().build

    implicit val rootCompany: Company = CompanyBuilder(
      unitOfMeasurements = List(firstUnitOfMeasurement, secondUnitOfMeasurement)
    ).build

    MongoDBHelper.insert[Company](rootCompany)
    MongoDBHelper.insert[Company](noiseCompany)

    val updatePayload = firstUnitOfMeasurement.copy(
      name = "update payload",
      conversionFactor = 1.7,
      description = Some("update description")
    )

    repository
      .update(firstUnitOfMeasurement.id.get, updatePayload)
      .flatMap { updateResult =>
        updateResult.getModifiedCount shouldEqual 1

        MongoDBHelper.find[Company](BsonDocument("id" -> rootCompany.id))
          .map { updateCompanies =>
            updateCompanies should have size 1
            val unitsOfMeasurement = updateCompanies.head.unitsOfMeasurement

            unitsOfMeasurement should have size 2

            val updateUnitOfMeasurement = unitsOfMeasurement.find(_.id.equals(firstUnitOfMeasurement.id)).get

            updateUnitOfMeasurement.name shouldEqual updatePayload.name
            updateUnitOfMeasurement.conversionFactor shouldEqual updatePayload.conversionFactor
            updateUnitOfMeasurement.description shouldEqual updatePayload.description
            updateUnitOfMeasurement.updatedAt.get.plusSeconds(2).isAfterNow shouldEqual true
          }
      }
  }

  behavior of "deleting unit of measurement"
  it should "delete resource of provided id" in {
    val firstUnitOfMeasurement = UnitOfMeasurementBuilder().build
    val secondUnitOfMeasurement = UnitOfMeasurementBuilder().build
    val treeUnitOfMeasurement = UnitOfMeasurementBuilder().build

    val noiseCompany: Company = CompanyBuilder(
      unitOfMeasurements = List(treeUnitOfMeasurement)
    ).build

    implicit val rootCompany: Company = CompanyBuilder(
      unitOfMeasurements = List(firstUnitOfMeasurement, secondUnitOfMeasurement)
    ).build

    MongoDBHelper.insert[Company](rootCompany)
    MongoDBHelper.insert[Company](noiseCompany)

    repository
      .delete(firstUnitOfMeasurement.id.get)
      .flatMap{ deleteResult =>
        deleteResult.getModifiedCount shouldEqual 1

        MongoDBHelper
          .find[Company](BsonDocument("id" -> rootCompany.id))
          .flatMap{ companies =>
            companies should have size 1
            val actualCompany = companies.head

            actualCompany.unitsOfMeasurement should have size 1

            actualCompany.unitsOfMeasurement.find(_.id.equals(firstUnitOfMeasurement.id)) shouldEqual None

            MongoDBHelper
              .find[Company](BsonDocument("id" -> noiseCompany.id))
              .map{ companies =>
                companies should have size 1

                companies.find(_.id.equals(rootCompany.id)) shouldEqual None

                val actualCompany = companies.head

                actualCompany.unitsOfMeasurement should have size 1
              }
          }
      }

  }

}
