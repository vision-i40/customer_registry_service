package domain.repositories

import java.util.UUID.randomUUID

import domain.Company
import infrastructure.mongodb.MongoDB
import org.mongodb.scala.MongoCollection

import scala.concurrent.{ExecutionContext, Future}

class CompanyRepository(implicit db: MongoDB, ec: ExecutionContext) {
  private val COLLECTION_NAME = "companies"
  import domain.Company._

  private lazy val collectionFuture: Future[MongoCollection[Company]] =
    db
      .collection[Company](COLLECTION_NAME)
      .map(_.withCodecRegistry(companyCodecRegistry))

  def create(name: String): Future[Company] = {
    val company = buildCompany(name)
    
    collectionFuture
      .flatMap { collection =>
        collection
          .withCodecRegistry(companyCodecRegistry)
          .insertOne(company)
          .toFuture()
          .map(_ => company)
      }
  }

  private def buildCompany(name: String): Company = {
    Company(
      id = randomUUID().toString,
      name = name
    )
  }
}


object CompanyRepository {
  def apply()(implicit db: MongoDB, ec: ExecutionContext): CompanyRepository = new CompanyRepository()
}