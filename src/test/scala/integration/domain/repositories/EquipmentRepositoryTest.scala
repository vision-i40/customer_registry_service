package integration.domain.repositories

import domain.models.{Company, Equipment}
import domain.repositories.{CompanyCollection, EquipmentRepository}
import infrastructure.config.MongoDBConfig
import infrastructure.mongodb.MongoDB
import org.mongodb.scala.bson.BsonDocument
import org.scalatest.BeforeAndAfterEach
import support.builders.{CompanyBuilder, EquipmentBuilder}
import support.{MongoDBHelper, VisionAsyncSpec}

class EquipmentRepositoryTest extends VisionAsyncSpec with BeforeAndAfterEach {
  implicit private val collectionName: String = "companies"
  private val mongoConfig: MongoDBConfig = new MongoDBConfig()
  private val db: MongoDB = new MongoDB(mongoConfig)
  private val companyCollection = new CompanyCollection(db)
  private val repository = new EquipmentRepository(companyCollection)

  import domain.models.Company._

  override def beforeEach(): Unit = {
    super.beforeEach()
    MongoDBHelper.clearCollection()
  }

  behavior of "adding equipment"
  it should "add an equipment when equipments is empty" in {
    implicit val rootCompany: Company = CompanyBuilder().build
    val equipment = EquipmentBuilder().build

    MongoDBHelper.insert[Company](rootCompany)

    repository
      .create(equipment)
      .flatMap { _ =>
        MongoDBHelper
          .find[Company](BsonDocument("id" -> rootCompany.id))
          .map { updateCompanies =>
            val equipments = updateCompanies.head.equipments

            equipments should have size 1
            val insertedEquipment = equipments.head

            insertedEquipment.name shouldEqual equipment.name
            insertedEquipment.createdAt.get.plusSeconds(2).isAfterNow shouldEqual true
            insertedEquipment.updatedAt.get.plusSeconds(2).isAfterNow shouldEqual true
            insertedEquipment.description shouldEqual equipment.description
          }
      }
  }

  it should "add an equipment when equipments has others equipments" in {
    val firstEquipment = EquipmentBuilder().build
    val secondEquipment = EquipmentBuilder().build
    val noiseCompany: Company = CompanyBuilder().build
    implicit val rootCompany: Company = CompanyBuilder(equipments = List(
      firstEquipment,
      secondEquipment
    )).build
    val expectedEquipment = EquipmentBuilder().build

    MongoDBHelper.insert[Company](rootCompany)
    MongoDBHelper.insert[Company](noiseCompany)

    repository
      .create(expectedEquipment)
      .flatMap { _ =>
        MongoDBHelper
          .find[Company](BsonDocument("id" -> rootCompany.id))
          .map { updateCompanies =>
            val equipments = updateCompanies.head.equipments

            equipments should have size 3
            val actualEquipment = equipments.find(_.name.equals(expectedEquipment.name)).get

            actualEquipment.name shouldEqual expectedEquipment.name
            actualEquipment.createdAt.get.plusSeconds(2).isAfterNow shouldEqual true
            actualEquipment.updatedAt.get.plusSeconds(2).isAfterNow shouldEqual true
            actualEquipment.description shouldEqual expectedEquipment.description
          }
      }
  }

  behavior of "updating equipment"
  it should "properly update equipment that exists" in {
    val firstEquipment = EquipmentBuilder().build
    val secondEquipment = EquipmentBuilder().build
    val expectedEquipment = EquipmentBuilder().build

    val noiseCompany: Company = CompanyBuilder().build

    implicit val rootCompany: Company = CompanyBuilder(equipments = List(
      firstEquipment,
      secondEquipment,
      expectedEquipment
    )).build

    MongoDBHelper.insert[Company](rootCompany)
    MongoDBHelper.insert[Company](noiseCompany)

    val updatedPayload = expectedEquipment.copy(
      name = "updated name",
      description = Some("updated description")
    )

    repository
      .update(expectedEquipment.id.get, updatedPayload)
      .flatMap { updateResult =>
        updateResult.getModifiedCount shouldEqual 1

        MongoDBHelper.find[Company](BsonDocument("id" -> rootCompany.id))
          .map { updatedCompanies =>
            updatedCompanies should have size 1
            val equipments = updatedCompanies.head.equipments

            equipments should have size 3

            val updatedEquipments = equipments.find(_.id.equals(expectedEquipment.id)).get

            updatedEquipments.name shouldEqual updatedPayload.name
            updatedEquipments.updatedAt.get.plusSeconds(2).isAfterNow shouldEqual true
            updatedEquipments.description shouldEqual  updatedPayload.description
          }
      }
  }

  it should "not update equipment that does not exist" in {
    implicit val rootCompany: Company = CompanyBuilder().build

    val id = "any-id"
    val updatedPlayload = Equipment(
      id = None,
      name = "updated name"
    )

    repository
      .update(id, updatedPlayload)
      .flatMap { updateResult =>
        updateResult.getModifiedCount shouldEqual 0

        MongoDBHelper.find[Company](BsonDocument("id" -> rootCompany.id))
          .map { updatedCompanies =>
            updatedCompanies should have size 0
          }
      }
  }

  behavior of "deleting equipment"
  it should "properly delete the equipment" in {
    val firstEquipment = EquipmentBuilder().build
    val secondEquipment = EquipmentBuilder().build
    val thirdEquipment = EquipmentBuilder().build

    val noiseCompany: Company = CompanyBuilder().build

    implicit val rootCompany: Company = CompanyBuilder(equipments = List(
      firstEquipment,
      secondEquipment,
      thirdEquipment
    )).build

    MongoDBHelper.insert[Company](rootCompany)
    MongoDBHelper.insert[Company](noiseCompany)

    repository
      .delete(secondEquipment.id.get)
      .flatMap { deleteResult =>
        deleteResult.getModifiedCount shouldEqual 1

        MongoDBHelper.find[Company](BsonDocument("id" -> rootCompany.id))
          .map { foundCompanies =>
            foundCompanies should have size 1

            val equipments = foundCompanies.head.equipments

            equipments should have size 2

            equipments.find(_.id.equals(secondEquipment.id)) shouldEqual None
          }
      }
  }
}
