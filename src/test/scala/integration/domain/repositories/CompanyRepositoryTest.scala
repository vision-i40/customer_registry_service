package integration.domain.repositories

import domain.models.Company
import domain.repositories.{CompanyCollection, CompanyRepository}
import infrastructure.config.MongoDBConfig
import infrastructure.mongodb.MongoDB
import org.mongodb.scala.bson.BsonDocument
import org.scalatest.BeforeAndAfterEach
import support.builders.CompanyBuilder
import support.{MongoDBHelper, VisionAsyncSpec}

class CompanyRepositoryTest extends VisionAsyncSpec with BeforeAndAfterEach {
  implicit private val collectionName: String = "companies"
  private val mongoConfig: MongoDBConfig = new MongoDBConfig()
  private val db: MongoDB = new MongoDB(mongoConfig)
  private val companyCollection = new CompanyCollection(db)
  private val companies: CompanyRepository = new CompanyRepository(companyCollection)

  import domain.models.Company._

  override def beforeEach(): Unit = {
    super.beforeEach()
    MongoDBHelper.clearCollection()
  }

  behavior of "adding new company"
  it should "create new company" in {
    val companyName = "The Awesome Company CO."
    val companySlug = "theawseomecompanyco"
    val companyFuture = companies.create(companyName, companySlug)

    companyFuture.flatMap { actualCompany =>
      actualCompany.name shouldEqual companyName
      MongoDBHelper.find[Company](BsonDocument("id" -> actualCompany.id)).map { storedCompanies =>
        storedCompanies.head.name shouldEqual companyName
        storedCompanies.head.slug shouldEqual companySlug
        storedCompanies should have size 1
      }
    }
  }

  behavior of "finding a company by slug"
  it should "return a company when slug matches" in {
    val companyName = "The Awesome Company CO."
    val companySlug = "theawseomecompanyco"

    val expectedCompany = CompanyBuilder(name = companyName, slug = companySlug).build
    MongoDBHelper.insert[Company](expectedCompany)

    val companyFuture = companies.findBySlug(companySlug)

    companyFuture.map { actualCompany =>
      actualCompany.isDefined shouldEqual true
      actualCompany shouldEqual Some(expectedCompany)
    }
  }
}
