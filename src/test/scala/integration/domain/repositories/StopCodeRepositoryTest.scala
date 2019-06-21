package integration.domain.repositories

import domain.models.Company
import domain.repositories.{CompanyCollection, StopCodeRepository}
import infrastructure.config.MongoDBConfig
import infrastructure.mongodb.MongoDB
import org.mongodb.scala.bson.BsonDocument
import org.scalatest.BeforeAndAfterEach
import support.builders.{CompanyBuilder, StopCodeBuilder, StopGroupBuilder}
import support.{MongoDBHelper, VisionAsyncSpec}

class StopCodeRepositoryTest extends VisionAsyncSpec with BeforeAndAfterEach {
  implicit private val collectionName: String = "companies"
  private val mongoConfig: MongoDBConfig = new MongoDBConfig()
  private val db: MongoDB = new MongoDB(mongoConfig)
  private val companyCollection = new CompanyCollection(db)
  private val repository = new StopCodeRepository(companyCollection)

  import domain.models.Company._

  override def beforeEach(): Unit = {
    super.beforeEach()
    MongoDBHelper.clearCollection()
  }

  behavior of "adding stop code"
  it should "add a stop code when stop codes is empty" in {
    val firstStopCode = StopCodeBuilder(code = "code1").build
    val secondStopCode = StopCodeBuilder(code = "code2").build
    val stopGroup = StopGroupBuilder(stopCodes = List(firstStopCode)).build
    implicit val rootCompany: Company = CompanyBuilder(
      stopGroups = List(stopGroup)
    ).build

    MongoDBHelper.insert[Company](rootCompany)

    repository
      .create(stopGroup.id.get, secondStopCode)
      .flatMap { _ =>
        MongoDBHelper
          .find[Company](BsonDocument("id" -> rootCompany.id))
          .map { updatedCompanies =>
            val stopGroups = updatedCompanies.head.stopGroups

            stopGroups should have size 1

            val stopsCodes = stopGroups.head.stopCodes

            stopsCodes should have size 2

            val insertedStopCode = stopsCodes.last

            insertedStopCode.code shouldEqual secondStopCode.code
            insertedStopCode.isManual shouldEqual secondStopCode.isManual
            insertedStopCode.isPlanned shouldEqual secondStopCode.isPlanned
            insertedStopCode.allowChangeInPendingStops shouldEqual secondStopCode.allowChangeInPendingStops
            insertedStopCode.createdAt.get.plusSeconds(2).isAfterNow shouldEqual true
            insertedStopCode.updatedAt.get.plusSeconds(2).isAfterNow shouldEqual true
          }
      }
  }

  behavior of "updating stop code"
  it should "properly update stop code that exists" in {
    val firstStopCode = StopCodeBuilder().build
    val expectedStopCode = StopCodeBuilder().build
    val stopGroup = StopGroupBuilder(stopCodes = List(firstStopCode, expectedStopCode)).build
    val noiseCompany: Company = CompanyBuilder().build

    implicit val rootCompany: Company = CompanyBuilder(stopGroups = List(stopGroup)).build

    MongoDBHelper.insert[Company](rootCompany)
    MongoDBHelper.insert[Company](noiseCompany)

    val updatedPayload = expectedStopCode.copy(
      code = "updated code",
      isManual = false,
      isPlanned = false,
      allowChangeInPendingStops = false
    )

    repository
      .update(stopGroup.id.get, expectedStopCode.id.get, updatedPayload)
      .flatMap { updateResult =>
        updateResult.getModifiedCount shouldEqual 1

        MongoDBHelper.find[Company](BsonDocument("id" -> rootCompany.id))
          .map { updatedCompanies =>
            updatedCompanies should have size 1
            val stopGroups = updatedCompanies.head.stopGroups

            stopGroups should have size 1

            val stopCodes = stopGroups.head.stopCodes
            stopCodes should have size 2

            val updatedStopCode = stopCodes.find(_.id.equals(expectedStopCode.id)).get

            updatedStopCode.code shouldEqual updatedPayload.code
            updatedStopCode.isManual shouldEqual updatedPayload.isManual
            updatedStopCode.isPlanned shouldEqual updatedPayload.isPlanned
            updatedStopCode.allowChangeInPendingStops shouldEqual updatedPayload.allowChangeInPendingStops
            updatedStopCode.updatedAt.get.plusSeconds(2).isAfterNow shouldEqual true
          }
      }
  }

  behavior of "deleting stop code"
  it should "properly delete the stop code" in{
    val firstStopCode = StopCodeBuilder().build
    val secondStopCode = StopCodeBuilder().build
    val thirdStopCode = StopCodeBuilder().build
    val stopGroup = StopGroupBuilder(stopCodes = List(
      firstStopCode,
      secondStopCode,
      thirdStopCode
    )).build

    val noiseCompany: Company = CompanyBuilder().build

    implicit val rootCompany: Company = CompanyBuilder(stopGroups = List(stopGroup)).build

    MongoDBHelper.insert[Company](rootCompany)
    MongoDBHelper.insert[Company](noiseCompany)

    repository
      .delete(stopGroup.id.get, secondStopCode.id.get)
      .flatMap { deleteResult =>
        deleteResult.getModifiedCount shouldEqual 1

        MongoDBHelper.find[Company](BsonDocument("id" -> rootCompany.id))
          .map { foundCompanies =>
            foundCompanies should have size 1

            val stopGroups = foundCompanies.head.stopGroups

            stopGroups should have size 1

            val stopCodes = stopGroups.head.stopCodes

            stopCodes should have size 2

            stopCodes.find(_.id.equals(secondStopCode.id)) shouldEqual None
          }
      }
  }
}
