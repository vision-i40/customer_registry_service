package domain.repositories

import java.util.UUID.randomUUID

import com.google.inject.{Inject, Singleton}
import company_admin.requests.ProductionLinePayload
import domain.models.{Company, ProductionLine}
import org.joda.time.DateTime
import org.mongodb.scala.bson.{BsonDateTime, BsonDocument}
import org.mongodb.scala.result.UpdateResult
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ProductionLineRepository @Inject()(companyCollection: CompanyCollection) {
  def create(name: String, oeeGoal: Double, resetProduction: Boolean, discountRework: Boolean, discountWaste: Boolean)
            (implicit company: Company): Future[ProductionLine] = {
    val productionLine = buildProductionLine(name, oeeGoal, resetProduction, discountRework, discountWaste)

    companyCollection
      .collectionFuture
      .flatMap { collection =>
        collection
          .updateOne(BsonDocument("id" -> company.id),
            BsonDocument(
            "$push" -> BsonDocument(
              "productionLines" -> buildProductionDocument(productionLine)
            )
          ))
          .toFuture()
          .map{ _ => productionLine}
      }
  }

  def update(id: String, productionLine: ProductionLinePayload)(implicit company: Company): Future[UpdateResult] = {
    companyCollection
      .collectionFuture
      .flatMap { collection =>
        collection
          .updateOne(BsonDocument(
            "id" -> company.id,
            "productionLines" -> BsonDocument("$elemMatch" -> BsonDocument("id" -> id))),
            BsonDocument(
              "$set" -> BsonDocument(
                "productionLines.$.name" -> productionLine.name,
                "productionLines.$.oeeGoal" -> productionLine.oeeGoal,
                "productionLines.$.resetProduction" -> productionLine.resetProduction,
                "productionLines.$.discountRework" -> productionLine.discountRework,
                "productionLines.$.discountWaste" -> productionLine.discountWaste,
                "productionLines.$.updatedAt" -> BsonDateTime(DateTime.now.getMillis)
              )
            )
          )
          .toFuture()
      }
  }

  def delete(id: String)(implicit company: Company): Future[UpdateResult] = {
    companyCollection
      .collectionFuture
      .flatMap{ collection =>
        collection
          .updateOne(
            BsonDocument("id" -> company.id),
            BsonDocument("$pull" ->
              BsonDocument(
                "productionLines" -> BsonDocument("id" -> id)
              )
            )
          ).toFuture()
      }
  }

  private def buildProductionDocument(productionLine: ProductionLine): BsonDocument = {
    BsonDocument(
      "id" -> productionLine.id,
      "name" -> productionLine.name,
      "oeeGoal" -> productionLine.oeeGoal,
      "resetProduction" -> productionLine.resetProduction,
      "discountRework" -> productionLine.discountRework,
      "discountWaste" -> productionLine.discountWaste,
      "createdAt" -> BsonDateTime(productionLine.createdAt.getMillis),
      "updatedAt" -> BsonDateTime(productionLine.updatedAt.getMillis)
    )
  }

  private def buildProductionLine(name: String, oeeGoal: Double, resetProduction: Boolean, discountRework: Boolean,
                                  discountWaste: Boolean): ProductionLine = {
    ProductionLine(
      id = randomUUID().toString,
      name = name,
      oeeGoal = oeeGoal,
      resetProduction = resetProduction,
      discountRework = discountRework,
      discountWaste = discountWaste,
      createdAt = DateTime.now,
      updatedAt = DateTime.now
    )
  }
}
