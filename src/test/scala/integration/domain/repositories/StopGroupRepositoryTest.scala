package integration.domain.repositories

import domain.models.Company
import domain.repositories.{CompanyCollection, StopGroupRepository}
import infrastructure.config.MongoDBConfig
import infrastructure.mongodb.MongoDB
import org.mongodb.scala.bson.BsonDocument
import org.scalatest.BeforeAndAfterEach
import support.builders.{CompanyBuilder, StopGroupBuilder}
import support.{MongoDBHelper, VisionAsyncSpec}

class StopGroupRepositoryTest extends VisionAsyncSpec with BeforeAndAfterEach {
  implicit private val collectionName: String = "companies"
  private val mongoConfig: MongoDBConfig = new MongoDBConfig()
  private val db: MongoDB = new MongoDB(mongoConfig)
  private val companyCollection = new CompanyCollection(db)
  private val repository = new StopGroupRepository(companyCollection)

  import domain.models.Company._

  override def beforeEach(): Unit = {
    super.beforeEach()
    MongoDBHelper.clearCollection()
  }

  behavior of "adding stop group"
  it should "add a stop group when stop group is empty" in {
    implicit val rootCompany: Company = CompanyBuilder().build
    val stopGroup = StopGroupBuilder().build

    MongoDBHelper.insert[Company](rootCompany)

    repository
      .create(stopGroup)
      .flatMap { _ =>
        MongoDBHelper
          .find[Company](BsonDocument("id" -> rootCompany.id))
          .map { updatedCompanies =>
            val stopGroups = updatedCompanies.head.stopGroups

            stopGroups should have size 1
            val insertedStopGroup = stopGroups.head

            insertedStopGroup.name shouldEqual stopGroup.name
            insertedStopGroup.stopCodes should have size 0
            insertedStopGroup.description shouldEqual stopGroup.description
            insertedStopGroup.createdAt.get.plusSeconds(2).isAfterNow shouldEqual true
            insertedStopGroup.updatedAt.get.plusSeconds(2).isAfterNow shouldEqual true
          }
      }
  }

  behavior of "updating stop group"
  it should "properly update stop group that exists" in {
    val firstStopGroup = StopGroupBuilder().build
    val secondStopGroup = StopGroupBuilder().build
    val expectedStopGroup = StopGroupBuilder().build

    val noiseCompany: Company = CompanyBuilder().build

    implicit val rootCompany: Company = CompanyBuilder(stopGroups = List(
      firstStopGroup,
      secondStopGroup,
      expectedStopGroup
    )).build

    MongoDBHelper.insert[Company](rootCompany)
    MongoDBHelper.insert[Company](noiseCompany)

    val updatedPayload = expectedStopGroup.copy(
      name = "updated name",
      stopCodes = List(),
      description = Some("other description")
    )

    repository
      .update(expectedStopGroup.id.get, updatedPayload)
      .flatMap { updateResult =>
        updateResult.getModifiedCount shouldEqual 1

        MongoDBHelper.find[Company](BsonDocument("id" -> rootCompany.id))
          .map { updatedCompanies =>
            updatedCompanies should have size 1
            val stopGroups = updatedCompanies.head.stopGroups

            stopGroups should have size 3

            val updatedStopGroup = stopGroups.find(_.id.equals(expectedStopGroup.id)).get

            updatedStopGroup.name shouldEqual updatedPayload.name
            updatedStopGroup.stopCodes should have size 1
            updatedStopGroup.description shouldEqual updatedPayload.description
            updatedStopGroup.updatedAt.get.plusSeconds(2).isAfterNow shouldEqual true
          }
      }
  }

  behavior of "deleting stop group"
  it should "properly delete the stop group" in{
    val firstStopGroup = StopGroupBuilder().build
    val secondStopGroup = StopGroupBuilder().build
    val thirdStopGroup = StopGroupBuilder().build

    val noiseCompany: Company = CompanyBuilder().build

    implicit val rootCompany: Company = CompanyBuilder(stopGroups = List(
      firstStopGroup,
      secondStopGroup,
      thirdStopGroup
    )).build

    MongoDBHelper.insert[Company](rootCompany)
    MongoDBHelper.insert[Company](noiseCompany)

    repository
      .delete(secondStopGroup.id.get)
      .flatMap { deleteResult =>
        deleteResult.getModifiedCount shouldEqual 1

        MongoDBHelper.find[Company](BsonDocument("id" -> rootCompany.id))
          .map { foundCompanies =>
            foundCompanies should have size 1

            val stopGroups = foundCompanies.head.stopGroups

            stopGroups should have size 2

            stopGroups.find(_.id.equals(secondStopGroup.id)) shouldEqual None
          }
      }
  }
}
