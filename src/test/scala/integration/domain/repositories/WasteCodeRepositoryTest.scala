package integration.domain.repositories

import domain.models.Company
import domain.repositories.{CompanyCollection, WasteCodeRepository}
import infrastructure.config.MongoDBConfig
import infrastructure.mongodb.MongoDB
import org.mongodb.scala.bson.BsonDocument
import org.scalatest.BeforeAndAfterEach
import support.builders.{CompanyBuilder, WasteCodeBuilder, WasteGroupBuilder}
import support.{MongoDBHelper, VisionAsyncSpec}

class WasteCodeRepositoryTest extends VisionAsyncSpec with BeforeAndAfterEach {
  implicit private val collectionName: String = "companies"
  private val mongoConfig: MongoDBConfig = new MongoDBConfig()
  private val db: MongoDB = new MongoDB(mongoConfig)
  private val companyCollection = new CompanyCollection(db)
  private val repository = new WasteCodeRepository(companyCollection)

  import domain.models.Company._

  override def beforeEach(): Unit = {
    super.beforeEach()
    MongoDBHelper.clearCollection()
  }

  behavior of "adding waste code"
  it should "add a waste code when waste codes is empty" in {
    val firstWasteCode = WasteCodeBuilder(name = "name1").build
    val secondWasteCode = WasteCodeBuilder(name = "name2").build
    val wasteGroup = WasteGroupBuilder(wasteCodes = List(firstWasteCode)).build
    implicit val rootCompany: Company = CompanyBuilder(
      wasteGroups = List(wasteGroup)
    ).build

    MongoDBHelper.insert[Company](rootCompany)

    repository
      .create(wasteGroup.id.get, secondWasteCode)
      .flatMap { _ =>
        MongoDBHelper
          .find[Company](BsonDocument("id" -> rootCompany.id))
          .map { updatedCompanies =>
            val wasteGroups = updatedCompanies.head.wasteGroups

            wasteGroups should have size 1

            val wastesCodes = wasteGroups.head.wasteCodes

            wastesCodes should have size 2

            val insertedWasteCode = wastesCodes.last

            insertedWasteCode.name shouldEqual secondWasteCode.name
            insertedWasteCode.reasonClass shouldEqual secondWasteCode.reasonClass
            insertedWasteCode.description shouldEqual secondWasteCode.description
            insertedWasteCode.createdAt.get.plusSeconds(2).isAfterNow shouldEqual true
            insertedWasteCode.updatedAt.get.plusSeconds(2).isAfterNow shouldEqual true
          }
      }
  }

  behavior of "updating waste code"
  it should "properly update waste code that exists" in {
    val firstWasteCode = WasteCodeBuilder().build
    val expectedWasteCode = WasteCodeBuilder().build
    val wasteGroup = WasteGroupBuilder(wasteCodes = List(firstWasteCode, expectedWasteCode)).build
    val noiseCompany: Company = CompanyBuilder().build

    implicit val rootCompany: Company = CompanyBuilder(wasteGroups = List(wasteGroup)).build

    MongoDBHelper.insert[Company](rootCompany)
    MongoDBHelper.insert[Company](noiseCompany)

    val updatedPayload = expectedWasteCode.copy(
      name = "updated name",
      description = Some("a-description-updated"),
      reasonClass = "reason-class-updated"
    )

    repository
      .update(wasteGroup.id.get, expectedWasteCode.id.get, updatedPayload)
      .flatMap { updateResult =>
        updateResult.getModifiedCount shouldEqual 1

        MongoDBHelper.find[Company](BsonDocument("id" -> rootCompany.id))
          .map { updatedCompanies =>
            updatedCompanies should have size 1
            val wasteGroups = updatedCompanies.head.wasteGroups

            wasteGroups should have size 1

            val wasteCodes = wasteGroups.head.wasteCodes
            wasteCodes should have size 2

            val updatedWasteCode = wasteCodes.find(_.id.equals(expectedWasteCode.id)).get

            updatedWasteCode.name shouldEqual updatedPayload.name
            updatedWasteCode.description shouldEqual updatedPayload.description
            updatedWasteCode.reasonClass shouldEqual updatedPayload.reasonClass
            updatedWasteCode.updatedAt.get.plusSeconds(2).isAfterNow shouldEqual true
          }
      }
  }

  behavior of "deleting waste code"
  it should "properly delete the waste code" in{
    val firstWasteCode = WasteCodeBuilder().build
    val secondWasteCode = WasteCodeBuilder().build
    val thirdWasteCode = WasteCodeBuilder().build
    val wasteGroup = WasteGroupBuilder(wasteCodes = List(
      firstWasteCode,
      secondWasteCode,
      thirdWasteCode
    )).build

    val noiseCompany: Company = CompanyBuilder().build

    implicit val rootCompany: Company = CompanyBuilder(wasteGroups = List(wasteGroup)).build

    MongoDBHelper.insert[Company](rootCompany)
    MongoDBHelper.insert[Company](noiseCompany)

    repository
      .delete(wasteGroup.id.get, secondWasteCode.id.get)
      .flatMap { deleteResult =>
        deleteResult.getModifiedCount shouldEqual 1

        MongoDBHelper.find[Company](BsonDocument("id" -> rootCompany.id))
          .map { foundCompanies =>
            foundCompanies should have size 1

            val wasteGroups = foundCompanies.head.wasteGroups

            wasteGroups should have size 1

            val wasteCodes = wasteGroups.head.wasteCodes

            wasteCodes should have size 2

            wasteCodes.find(_.id.equals(secondWasteCode.id)) shouldEqual None
          }
      }
  }
}
