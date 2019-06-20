package integration.domain.repositories

import domain.models.Company
import domain.repositories.{CompanyCollection, ReworkGroupRepository}
import infrastructure.config.MongoDBConfig
import infrastructure.mongodb.MongoDB
import org.mongodb.scala.bson.BsonDocument
import org.scalatest.BeforeAndAfterEach
import support.builders.{CompanyBuilder, ReworkGroupBuilder}
import support.{MongoDBHelper, VisionAsyncSpec}

class ReworkGroupRepositoryTest extends VisionAsyncSpec with BeforeAndAfterEach {
  implicit private val collectionName: String = "companies"
  private val mongoConfig: MongoDBConfig = new MongoDBConfig()
  private val db: MongoDB = new MongoDB(mongoConfig)
  private val companyCollection = new CompanyCollection(db)
  private val repository = new ReworkGroupRepository(companyCollection)

  import domain.models.Company._

  override def beforeEach(): Unit = {
    super.beforeEach()
    MongoDBHelper.clearCollection()
  }

  behavior of "adding rework group"
  it should "add a rework group when rework group is empty" in {
    implicit val rootCompany: Company = CompanyBuilder().build
    val reworkGroup = ReworkGroupBuilder().build

    MongoDBHelper.insert[Company](rootCompany)

    repository
      .create(reworkGroup)
      .flatMap { _ =>
        MongoDBHelper
          .find[Company](BsonDocument("id" -> rootCompany.id))
          .map { updatedCompanies =>
            val reworkGroups = updatedCompanies.head.reworkGroups

            reworkGroups should have size 1
            val insertedReworkGroup = reworkGroups.head

            insertedReworkGroup.name shouldEqual reworkGroup.name
            insertedReworkGroup.description shouldEqual reworkGroup.description
            insertedReworkGroup.createdAt.get.plusSeconds(2).isAfterNow shouldEqual true
            insertedReworkGroup.updatedAt.get.plusSeconds(2).isAfterNow shouldEqual true
          }
      }
  }

  behavior of "updating rework group"
  it should "properly update rework group that exists" in {
    val firstReworkGroup = ReworkGroupBuilder().build
    val secondReworkGroup = ReworkGroupBuilder().build
    val expectedReworkGroup = ReworkGroupBuilder().build

    val noiseCompany: Company = CompanyBuilder().build

    implicit val rootCompany: Company = CompanyBuilder(reworkGroups = List(
      firstReworkGroup,
      secondReworkGroup,
      expectedReworkGroup
    )).build

    MongoDBHelper.insert[Company](rootCompany)
    MongoDBHelper.insert[Company](noiseCompany)

    val updatedPayload = expectedReworkGroup.copy(
      name = "updated name",
      description = Some("other description")
    )

    repository
      .update(expectedReworkGroup.id.get, updatedPayload)
      .flatMap { updateResult =>
        updateResult.getModifiedCount shouldEqual 1

        MongoDBHelper.find[Company](BsonDocument("id" -> rootCompany.id))
          .map { updatedCompanies =>
            updatedCompanies should have size 1
            val reworkGroups = updatedCompanies.head.reworkGroups

            reworkGroups should have size 3

            val updatedReworkGroup = reworkGroups.find(_.id.equals(expectedReworkGroup.id)).get

            updatedReworkGroup.name shouldEqual updatedPayload.name
            updatedReworkGroup.description shouldEqual updatedPayload.description
            updatedReworkGroup.updatedAt.get.plusSeconds(2).isAfterNow shouldEqual true
          }
      }
  }

  behavior of "deleting rework group"
  it should "properly delete the rework group" in{
    val firstReworkGroup = ReworkGroupBuilder().build
    val secondReworkGroup = ReworkGroupBuilder().build
    val thirdReworkGroup = ReworkGroupBuilder().build

    val noiseCompany: Company = CompanyBuilder().build

    implicit val rootCompany: Company = CompanyBuilder(reworkGroups = List(
      firstReworkGroup,
      secondReworkGroup,
      thirdReworkGroup
    )).build

    MongoDBHelper.insert[Company](rootCompany)
    MongoDBHelper.insert[Company](noiseCompany)

    repository
      .delete(secondReworkGroup.id.get)
      .flatMap { deleteResult =>
        deleteResult.getModifiedCount shouldEqual 1

        MongoDBHelper.find[Company](BsonDocument("id" -> rootCompany.id))
          .map { foundCompanies =>
            foundCompanies should have size 1

            val reworkGroups = foundCompanies.head.reworkGroups

            reworkGroups should have size 2

            reworkGroups.find(_.id.equals(secondReworkGroup.id)) shouldEqual None
          }
      }
  }
}
