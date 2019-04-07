package integration.domain.repositories

import company_admin.requests.ProductionLinePayload
import domain.models.Company
import domain.repositories.{CompanyCollection, ProductionLineRepository}
import infrastructure.config.MongoDBConfig
import infrastructure.mongodb.MongoDB
import org.mongodb.scala.bson.BsonDocument
import org.scalatest.BeforeAndAfterEach
import support.builders.{CompanyBuilder, ProductionLineBuilder}
import support.{MongoDBHelper, VisionAsyncSpec}

class ProductionLineRepositoryTest extends VisionAsyncSpec with BeforeAndAfterEach {
  implicit private val collectionName: String = "companies"
  private val mongoConfig: MongoDBConfig = new MongoDBConfig()
  private val db: MongoDB = new MongoDB(mongoConfig)
  private val companyCollection = new CompanyCollection(db)
  private val repository = new ProductionLineRepository(companyCollection)

  import domain.models.Company._

  override def beforeEach(): Unit = {
    super.beforeEach()
    MongoDBHelper.clearCollection()
  }

  behavior of "adding production line"
  it should "add a production line when production lines is empty" in {
    implicit val rootCompany: Company = CompanyBuilder().build
    val productionLine = ProductionLineBuilder().build

    MongoDBHelper.insert[Company](rootCompany)

    repository
      .create(
        name = productionLine.name,
        oeeGoal = productionLine.oeeGoal,
        resetProduction = productionLine.resetProduction,
        discountRework = productionLine.discountRework,
        discountWaste = productionLine.discountWaste
      )
      .flatMap { _ =>
        MongoDBHelper.find[Company](BsonDocument("id" -> rootCompany.id)).map { updatedCompanies =>
          val productionLines = updatedCompanies.head.productionLines

          productionLines should have size 1
          val insertedProductionLine = productionLines.head

          insertedProductionLine.name shouldEqual productionLine.name
          insertedProductionLine.oeeGoal shouldEqual productionLine.oeeGoal
          insertedProductionLine.resetProduction shouldEqual productionLine.resetProduction
          insertedProductionLine.discountRework shouldEqual productionLine.discountRework
          insertedProductionLine.discountWaste shouldEqual productionLine.discountWaste
          insertedProductionLine.createdAt.plusSeconds(2).isAfterNow shouldEqual true
          insertedProductionLine.updatedAt.plusSeconds(2).isAfterNow shouldEqual true
        }
      }
  }

  it should "add a production line when production lines has others production lines" in {
    val firstProductionLine = ProductionLineBuilder().build
    val secondProductionLine = ProductionLineBuilder().build
    val noiseCompany: Company = CompanyBuilder().build
    implicit val rootCompany: Company = CompanyBuilder(productionLines = List(
      firstProductionLine,
      secondProductionLine
    )).build
    val expectedProductionLine = ProductionLineBuilder().build

    MongoDBHelper.insert[Company](rootCompany)
    MongoDBHelper.insert[Company](noiseCompany)

    repository
      .create(
        name = expectedProductionLine.name,
        oeeGoal = expectedProductionLine.oeeGoal,
        resetProduction = expectedProductionLine.resetProduction,
        discountRework = expectedProductionLine.discountRework,
        discountWaste = expectedProductionLine.discountWaste
      )
      .flatMap { _ =>

        MongoDBHelper
          .find[Company](BsonDocument("id" -> rootCompany.id))
          .map { updatedCompanies =>
            val productionLines = updatedCompanies.head.productionLines

            productionLines should have size 3
            val actualProductionLine = productionLines.find(_.name.equals(expectedProductionLine.name)).get

            actualProductionLine.name shouldEqual expectedProductionLine.name
            actualProductionLine.oeeGoal shouldEqual expectedProductionLine.oeeGoal
            actualProductionLine.resetProduction shouldEqual expectedProductionLine.resetProduction
            actualProductionLine.discountRework shouldEqual expectedProductionLine.discountRework
            actualProductionLine.discountWaste shouldEqual expectedProductionLine.discountWaste
            actualProductionLine.createdAt.plusSeconds(2).isAfterNow shouldEqual true
            actualProductionLine.updatedAt.plusSeconds(2).isAfterNow shouldEqual true
        }
      }
  }

  behavior of "updating production line"
  it should "properly update production line that exists" in {
    val firstProductionLine = ProductionLineBuilder().build
    val secondProductionLine = ProductionLineBuilder().build
    val expectedProductionLine = ProductionLineBuilder().build

    val noiseCompany: Company = CompanyBuilder().build

    implicit val rootCompany: Company = CompanyBuilder(productionLines = List(
      firstProductionLine,
      secondProductionLine,
      expectedProductionLine
    )).build

    MongoDBHelper.insert[Company](rootCompany)
    MongoDBHelper.insert[Company](noiseCompany)

    val updatedPayload = ProductionLinePayload(
      id = Some(expectedProductionLine.id),
      name = "updated name",
      oeeGoal = 1.9,
      resetProduction = true,
      discountRework = true,
      discountWaste = true
    )

    repository
      .update(expectedProductionLine.id, updatedPayload)
      .flatMap { updateResult =>
        updateResult.getModifiedCount shouldEqual 1

        MongoDBHelper.find[Company](BsonDocument("id" -> rootCompany.id))
          .map { updatedCompanies =>
            updatedCompanies should have size 1
            val productionLines = updatedCompanies.head.productionLines

            productionLines should have size 3

            val updatedProductionLine = productionLines.find(_.id.equals(expectedProductionLine.id)).get

            updatedProductionLine.name shouldEqual updatedPayload.name
            updatedProductionLine.oeeGoal shouldEqual updatedPayload.oeeGoal
            updatedProductionLine.resetProduction shouldEqual updatedPayload.resetProduction
            updatedProductionLine.discountRework shouldEqual updatedPayload.discountRework
            updatedProductionLine.discountWaste shouldEqual updatedPayload.discountWaste
          }
      }
  }

  it should "not update production line that does not exist" in {
    implicit val rootCompany: Company = CompanyBuilder().build

    val id = "any-id"
    val updatedPayload = ProductionLinePayload(
      id = None,
      name = "updated name",
      oeeGoal = 1.9,
      resetProduction = true,
      discountRework = true,
      discountWaste = true
    )

    repository
      .update(id, updatedPayload)
      .flatMap { updateResult =>
        updateResult.getModifiedCount shouldEqual 0

        MongoDBHelper.find[Company](BsonDocument("id" -> rootCompany.id))
          .map { updatedCompanies =>
            updatedCompanies should have size 0
          }
      }
  }


  behavior of "deleting production line"
  it should "properly delete the production line" in{
    val firstProductionLine = ProductionLineBuilder().build
    val secondProductionLine = ProductionLineBuilder().build
    val thirdProductionLine = ProductionLineBuilder().build

    val noiseCompany: Company = CompanyBuilder().build

    implicit val rootCompany: Company = CompanyBuilder(productionLines = List(
      firstProductionLine,
      secondProductionLine,
      thirdProductionLine
    )).build

    MongoDBHelper.insert[Company](rootCompany)
    MongoDBHelper.insert[Company](noiseCompany)

    repository
      .delete(secondProductionLine.id)
      .flatMap { deleteResult =>
        deleteResult.getModifiedCount shouldEqual 1

        MongoDBHelper.find[Company](BsonDocument("id" -> rootCompany.id))
          .map { foundCompanies =>
            foundCompanies should have size 1

            val productionLines = foundCompanies.head.productionLines

            productionLines should have size 2

            productionLines.find(_.id.equals(secondProductionLine.id)) shouldEqual None
          }
      }

  }


}
