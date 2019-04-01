package integration.domain.repositories

import domain.Company
import domain.repositories.CompanyRepository
import infrastructure.config.MongoDBConfig
import infrastructure.mongodb.MongoDB
import org.mongodb.scala.bson.BsonDocument
import org.scalatest.BeforeAndAfterEach
import support.{MongoDBHelper, VisionAsyncSpec}

class CompanyRepositoryTest extends VisionAsyncSpec with BeforeAndAfterEach {
  implicit private val collectionName: String = "companies"
  implicit private val mongoConfig: MongoDBConfig = MongoDBConfig
  implicit private val db: MongoDB = MongoDB()
  private val companies: CompanyRepository = CompanyRepository()

  import domain.Company._

  override def beforeEach(): Unit = {
    super.beforeEach()
    MongoDBHelper.clearCollection()
  }

  behavior of "adding new company"
  it should "create new company" in {
    val companyName = "The Awesome Company CO."

    val companyFuture = companies.create(companyName)

    companyFuture.flatMap { actualCompany =>
      actualCompany.name shouldEqual companyName
      MongoDBHelper.find[Company](BsonDocument("id" -> actualCompany.id)).map { storedCompanies =>
        storedCompanies.head.name shouldEqual companyName
        storedCompanies should have size 1
      }
    }
  }
}
