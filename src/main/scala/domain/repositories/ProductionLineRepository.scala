package domain.repositories

import java.util.UUID.randomUUID

import com.google.inject.Inject
import domain.models.{Company, ProductionLine}
import org.joda.time.DateTime
import org.mongodb.scala.bson.{BsonDateTime, BsonDocument}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ProductionLineRepository @Inject()(companyCollection: CompanyCollection) {
  def addProductionLine(name: String, oeeGoal: Double, resetProduction: Boolean, discountRework: Boolean,
                        discountWaste: Boolean)
                       (implicit company: Company): Future[ProductionLine] = {
    val productionLine = ProductionLine(
      id = randomUUID().toString,
      name = name,
      oeeGoal = oeeGoal,
      resetProduction = resetProduction,
      discountRework = discountRework,
      discountWaste = discountWaste,
      createdAt = DateTime.now,
      updatedAt = DateTime.now
    )

    companyCollection
      .collectionFuture()
      .flatMap { collection =>
        collection
          .updateOne(BsonDocument("id" -> company.id), BsonDocument(
            "$push" -> BsonDocument(
              "productionLines" -> BsonDocument(
                "id" -> productionLine.id,
                "name" -> productionLine.name,
                "oeeGoal" -> productionLine.oeeGoal,
                "resetProduction" -> productionLine.resetProduction,
                "discountRework" -> productionLine.discountRework,
                "discountWaste" -> productionLine.discountWaste,
                "createdAt" -> BsonDateTime(productionLine.createdAt.getMillis),
                "updatedAt" -> BsonDateTime(productionLine.updatedAt.getMillis)
              )
            )
          ))
          .toFuture()
          .map{ _ => productionLine}
      }
  }
}
