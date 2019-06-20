package domain.repositories

import java.util.UUID
import domain.models.{Company, CompanyResource}
import org.bson.{BsonNull, BsonValue}
import org.joda.time.DateTime
import org.mongodb.scala.bson.{BsonBoolean, BsonDateTime, BsonDocument, BsonNumber, BsonString}
import org.mongodb.scala.result.UpdateResult
import scala.concurrent.{ExecutionContext, Future}

trait CompanyResourceRepository[T <: CompanyResource] {
  protected val resourceName: String
  protected val companyCollection: CompanyCollection

  def create(resource: T)
            (implicit company: Company, ec: ExecutionContext): Future[T] = {
    companyCollection
      .collectionFuture
      .flatMap { collection =>
        collection
          .updateOne(BsonDocument("id" -> company.id),
            BsonDocument(
              "$push" -> BsonDocument(
                resourceName -> buildCreationDocument(resource)
              )
            ))
          .toFuture()
          .map(_ => resource)
      }
  }

  def delete(id: String)
            (implicit company: Company, ec: ExecutionContext): Future[UpdateResult] = {
    companyCollection
      .collectionFuture
      .flatMap{ collection =>
        collection
          .updateOne(
            BsonDocument("id" -> company.id),
            BsonDocument("$pull" ->
              BsonDocument(
                resourceName -> BsonDocument("id" -> id)
              )
            )
          ).toFuture()
      }
  }

  def update(id: String, resourcePayload: T)
            (implicit company: Company, ec: ExecutionContext): Future[UpdateResult] = {
    companyCollection
      .collectionFuture
      .flatMap { collection =>
        collection
          .updateOne(BsonDocument(
            "id" -> company.id,
            resourceName -> BsonDocument("$elemMatch" -> BsonDocument("id" -> id))),
            BsonDocument(
              "$set" -> buildUpdateDocument(resourcePayload)
            )
          )
          .toFuture()
      }
  }

  private def buildCreationDocument(resource: T): BsonDocument = {
    val ccMap = overrideCreationParams(caseClassToTraversable(resource))
    BsonDocument(ccMap)
  }

  private def overrideCreationParams(resource: Map[String, BsonValue]): Map[String, BsonValue] = {
    val creationSetupValues = Map[String, BsonValue](
      "id" -> BsonString(UUID.randomUUID().toString),
      "createdAt" -> BsonDateTime(DateTime.now.getMillis),
      "updatedAt" -> BsonDateTime(DateTime.now.getMillis)
    )

    resource ++ creationSetupValues
  }

  private def buildUpdateDocument(resourcePayload: T): BsonDocument = {
    val ccMap = overrideUpdateParams(caseClassToTraversable(resourcePayload))
    BsonDocument(ccMap)
  }

  private def overrideUpdateParams(resource: Map[String, BsonValue]): Map[String, BsonValue] = {
    val setStructure: String => String = value => s"$resourceName.$$.$value"

    val staticFields = List("id", "createdAt")

    val updateSetupValues = Map[String, BsonValue](
      "updatedAt" -> BsonDateTime(DateTime.now.getMillis)
    )

    (resource ++ updateSetupValues -- staticFields).map(m => setStructure(m._1) -> m._2)
  }

  private def caseClassToTraversable(resource: T): Map[String, BsonValue] =
    resource
      .getClass
      .getDeclaredFields
      .foldLeft(Map[String, BsonValue]()) { (a, f) =>
        f.setAccessible(true)
        a + (f.getName -> destructBsonValue(f.get(resource)))
      }

  private def destructBsonValue(value: Any): BsonValue = value match {
    case x: Boolean => BsonBoolean(x)
    case x: Int => BsonNumber(x)
    case x: Long => BsonNumber(x)
    case x: Double => BsonNumber(x)
    case x: String => BsonString(x)
    case Some(x: String) => BsonString(x)
    case x: DateTime => BsonDateTime(x.getMillis)
    case _ => new BsonNull
  }
}
