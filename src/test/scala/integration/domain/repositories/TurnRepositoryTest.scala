package integration.domain.repositories

import domain.models.Company
import domain.repositories.{CompanyCollection, TurnRepository}
import infrastructure.config.MongoDBConfig
import infrastructure.mongodb.MongoDB
import org.joda.time.LocalTime
import org.mongodb.scala.bson.BsonDocument
import org.scalatest.BeforeAndAfterEach
import support.builders.{CompanyBuilder, TurnBuilder}
import support.{MongoDBHelper, VisionAsyncSpec}

class TurnRepositoryTest extends VisionAsyncSpec with BeforeAndAfterEach {
  implicit private val collectionName: String = "companies"
  private val mongoConfig: MongoDBConfig = new MongoDBConfig()
  private val db: MongoDB = new MongoDB(mongoConfig)
  private val companyCollection = new CompanyCollection(db)
  private val repository = new TurnRepository(companyCollection)

  import domain.models.Company._

  override def beforeEach(): Unit = {
    super.beforeEach()
    MongoDBHelper.clearCollection()
  }

  behavior of "adding turn"
  it should "add a turn when turns list is empty" in {
    implicit val rootCompany: Company = CompanyBuilder().build
    val turn = TurnBuilder().build

    MongoDBHelper.insert[Company](rootCompany)

    repository
      .create(turn)
      .flatMap { _ =>
        MongoDBHelper
          .find[Company](BsonDocument("id" -> rootCompany.id))
          .map { updatedCompanies =>
            val turns = updatedCompanies.head.turns

            turns should have size 1
            val insertedTurn = turns.head

            insertedTurn.name shouldEqual turn.name
            insertedTurn.startTime shouldEqual turn.startTime
            insertedTurn.endTime shouldEqual turn.endTime
            insertedTurn.description shouldEqual turn.description
          }
      }
  }

  behavior of "updating turn"
  it should "update resource of provided id" in {
    val firstTurn = TurnBuilder().build
    val secondTurn = TurnBuilder().build

    val noiseCompany: Company = CompanyBuilder().build

    implicit val rootCompany: Company = CompanyBuilder(
      turns = List(firstTurn, secondTurn)
    ).build

    MongoDBHelper.insert[Company](rootCompany)
    MongoDBHelper.insert[Company](noiseCompany)

    val updatedStartTime = LocalTime.now.minusHours(1)
    val updatedEndTime = LocalTime.now.minusHours(2)

    val updatePayload = firstTurn.copy(
      name = "update payload",
      startTime = updatedStartTime,
      endTime = updatedEndTime,
      description = Some("update description")
    )

    repository
      .update(firstTurn.id.get, updatePayload)
      .flatMap { updateResult =>
        updateResult.getModifiedCount shouldEqual 1

        MongoDBHelper.find[Company](BsonDocument("id" -> rootCompany.id))
          .map { updateCompanies =>
            updateCompanies should have size 1
            val turn = updateCompanies.head.turns

            turn should have size 2

            val updateTurn = turn.find(_.id.equals(firstTurn.id)).get

            updateTurn.name shouldEqual updatePayload.name
            updateTurn.startTime shouldEqual updatePayload.startTime
            updateTurn.endTime shouldEqual updatePayload.endTime
            updateTurn.description shouldEqual updatePayload.description
            updateTurn.updatedAt.get.plusSeconds(2).isAfterNow shouldEqual true
          }
      }
  }

  behavior of "deleting turn"
  it should "delete resource of provided id" in {
    val firstTurn = TurnBuilder().build
    val secondTurn = TurnBuilder().build
    val treeTurn = TurnBuilder().build

    val noiseCompany: Company = CompanyBuilder(
      turns = List(treeTurn)
    ).build

    implicit val rootCompany: Company = CompanyBuilder(
      turns = List(firstTurn, secondTurn)
    ).build

    MongoDBHelper.insert[Company](rootCompany)
    MongoDBHelper.insert[Company](noiseCompany)

    repository
      .delete(firstTurn.id.get)
      .flatMap{ deleteResult =>
        deleteResult.getModifiedCount shouldEqual 1

        MongoDBHelper
          .find[Company](BsonDocument("id" -> rootCompany.id))
          .flatMap{ companies =>
            companies should have size 1
            val actualCompany = companies.head

            actualCompany.turns should have size 1

            actualCompany.turns.find(_.id.equals(firstTurn.id)) shouldEqual None

            MongoDBHelper
              .find[Company](BsonDocument("id" -> noiseCompany.id))
              .map{ companies =>
                companies should have size 1

                companies.find(_.id.equals(rootCompany.id)) shouldEqual None

                val actualCompany = companies.head

                actualCompany.turns should have size 1
              }
          }
      }

  }

}
