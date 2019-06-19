package integration.domain.repositories

import domain.models.Company
import domain.repositories.{CompanyCollection, ReworkCodeRepository}
import infrastructure.config.MongoDBConfig
import infrastructure.mongodb.MongoDB
import org.mongodb.scala.bson.BsonDocument
import org.scalatest.BeforeAndAfterEach
import support.builders.{CompanyBuilder, ReworkCodeBuilder}
import support.{MongoDBHelper, VisionAsyncSpec}

class ReworkCodeRepositoryTest extends VisionAsyncSpec with BeforeAndAfterEach {
  implicit private val collectionName: String = "companies"
  private val mongoConfig: MongoDBConfig = new MongoDBConfig()
  private val db: MongoDB = new MongoDB(mongoConfig)
  private val companyCollection = new CompanyCollection(db)
  private val repository = new ReworkCodeRepository(companyCollection)

  import domain.models.Company._

  override def beforeEach(): Unit = {
    super.beforeEach()
    MongoDBHelper.clearCollection()
  }

  behavior of "adding rework code"
  it should "add a rework code when rework codes is empty" in {
    implicit val rootCompany: Company = CompanyBuilder().build
    val reworkCode = ReworkCodeBuilder().build

    MongoDBHelper.insert[Company](rootCompany)

    repository
      .create(reworkCode)
      .flatMap { _ =>
        MongoDBHelper
          .find[Company](BsonDocument("id" -> rootCompany.id))
          .map { updatedCompanies =>
            val reworkCodes = updatedCompanies.head.reworkCodes

            reworkCodes should have size 1
            val insertedReworkCode = reworkCodes.head

            insertedReworkCode.name shouldEqual reworkCode.name
            insertedReworkCode.reasonClass shouldEqual reworkCode.reasonClass
            insertedReworkCode.description shouldEqual reworkCode.description
            insertedReworkCode.createdAt.get.plusSeconds(2).isAfterNow shouldEqual true
            insertedReworkCode.updatedAt.get.plusSeconds(2).isAfterNow shouldEqual true
          }
      }
  }

  behavior of "updating rework code"
  it should "properly update rework code that exists" in {
    val firstReworkCode = ReworkCodeBuilder().build
    val secondReworkCode = ReworkCodeBuilder().build
    val expectedReworkCode = ReworkCodeBuilder().build

    val noiseCompany: Company = CompanyBuilder().build

    implicit val rootCompany: Company = CompanyBuilder(reworkCodes = List(
      firstReworkCode,
      secondReworkCode,
      expectedReworkCode
    )).build

    MongoDBHelper.insert[Company](rootCompany)
    MongoDBHelper.insert[Company](noiseCompany)

    val updatedPayload = expectedReworkCode.copy(
      name = "updated name",
      reasonClass = "other reason class",
      description = Some("other description")
    )

    repository
      .update(expectedReworkCode.id.get, updatedPayload)
      .flatMap { updateResult =>
        updateResult.getModifiedCount shouldEqual 1

        MongoDBHelper.find[Company](BsonDocument("id" -> rootCompany.id))
          .map { updatedCompanies =>
            updatedCompanies should have size 1
            val reworkCodes = updatedCompanies.head.reworkCodes

            reworkCodes should have size 3

            val updatedReworkCode = reworkCodes.find(_.id.equals(expectedReworkCode.id)).get

            updatedReworkCode.name shouldEqual updatedPayload.name
            updatedReworkCode.reasonClass shouldEqual updatedPayload.reasonClass
            updatedReworkCode.description shouldEqual updatedPayload.description
            updatedReworkCode.updatedAt.get.plusSeconds(2).isAfterNow shouldEqual true
          }
      }
  }

  behavior of "deleting rework code"
  it should "properly delete the rework code" in{
    val firstReworkCode = ReworkCodeBuilder().build
    val secondReworkCode = ReworkCodeBuilder().build
    val thirdReworkCode = ReworkCodeBuilder().build

    val noiseCompany: Company = CompanyBuilder().build

    implicit val rootCompany: Company = CompanyBuilder(reworkCodes = List(
      firstReworkCode,
      secondReworkCode,
      thirdReworkCode
    )).build

    MongoDBHelper.insert[Company](rootCompany)
    MongoDBHelper.insert[Company](noiseCompany)

    repository
      .delete(secondReworkCode.id.get)
      .flatMap { deleteResult =>
        deleteResult.getModifiedCount shouldEqual 1

        MongoDBHelper.find[Company](BsonDocument("id" -> rootCompany.id))
          .map { foundCompanies =>
            foundCompanies should have size 1

            val reworkCodes = foundCompanies.head.reworkCodes

            reworkCodes should have size 2

            reworkCodes.find(_.id.equals(secondReworkCode.id)) shouldEqual None
          }
      }
  }
}
