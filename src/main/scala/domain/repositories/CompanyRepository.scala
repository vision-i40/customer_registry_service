package domain.repositories

import java.util.UUID.randomUUID

import com.google.inject.{Inject, Singleton}
import domain.models.Company
import org.mongodb.scala.bson.BsonDocument

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class CompanyRepository @Inject()(companyCollection: CompanyCollection) {
  def create(name: String, slug: String): Future[Company] = {
    val company = buildCompany(name, slug)

    companyCollection
      .collectionFuture()
      .flatMap { collection =>
        collection
          .insertOne(company)
          .toFuture()
          .map(_ => company)
      }
  }

  def findBySlug(slug: String): Future[Option[Company]] = {
    companyCollection
      .collectionFuture()
      .flatMap { collection =>
        collection
          .find(BsonDocument("slug" -> slug))
          .first()
          .toFutureOption()
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