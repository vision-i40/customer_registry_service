package domain.repositories

import java.util.UUID.randomUUID

import com.google.inject.{Inject, Singleton}
import domain.{Company, ProductionLine}
import infrastructure.mongodb.MongoDB
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.bson.BsonDocument

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class CompanyRepository @Inject()(db: MongoDB) {
  private val COLLECTION_NAME = "companies"
  import domain.Company._

  private lazy val collectionFuture: Future[MongoCollection[Company]] =
    db
      .collection[Company](COLLECTION_NAME)
      .map(_.withCodecRegistry(companyCodecRegistry))

  def create(name: String, slug: String): Future[Company] = {
    val company = buildCompany(name, slug)
    
    collectionFuture
      .flatMap { collection =>
        collection
          .insertOne(company)
          .toFuture()
          .map(_ => company)
      }
  }

  def findBySlug(slug: String): Future[Option[Company]] = {
    collectionFuture
      .flatMap { collection =>
        collection
          .withCodecRegistry(companyCodecRegistry)
          .find(BsonDocument("slug" -> slug))
          .first()
          .toFutureOption()
      }
  }

  def addProductionLine(productionLine: ProductionLine)(implicit company: Company) = {
    collectionFuture
      .flatMap { collection =>
        collection
          .withCodecRegistry(companyCodecRegistry)
          .updateOne(BsonDocument("id" -> company.id), BsonDocument(
            "$push" -> BsonDocument(
              "productionLines" -> BsonDocument(
                "id" -> productionLine.id,
                "name" -> productionLine.name,
                "oee_goal" -> productionLine.oee_goal
              )
            )
          ))
          .toFuture()
      }
  }

  private def buildCompany(name: String, slug: String): Company = {
    Company(
      id = randomUUID().toString,
      slug = slug,
      name = name
    )
  }
}