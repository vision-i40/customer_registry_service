package domain.repositories

import java.util.Arrays.asList
import java.util.UUID

import com.mongodb.client.model.Filters
import domain.models.{Company, CompanyResource}
import org.bson.{BsonArray, BsonNull, BsonValue}
import org.joda.time.{DateTime, LocalTime}
import org.mongodb.scala.bson.{BsonBoolean, BsonDateTime, BsonDocument, BsonInt32, BsonNumber, BsonString}
import org.mongodb.scala.model.UpdateOptions
import org.mongodb.scala.result.UpdateResult

import scala.concurrent.{ExecutionContext, Future}

trait CompanyResourceRepository[T <: CompanyResource] {
  protected val resourceName: String
  protected val companyCollection: CompanyCollection
  protected val parentResource: Option[String] = None

  private val immutableFields = List("id", "createdAt")

  def create(resource: T)
            (implicit company: Company, ec: ExecutionContext): Future[T] = {
    create(resource, None)
  }

  def create(parentId: String, resource: T)
            (implicit company: Company, ec: ExecutionContext): Future[T] = {
    create(resource, Some(parentId))
  }

  private def create(resource: T, maybeParentId: Option[String] = None)
                    (implicit company: Company, ec: ExecutionContext): Future[T] = {
    companyCollection
      .collectionFuture
      .flatMap { collection =>
        collection
          .updateOne(buildPushFilter(maybeParentId),
            BsonDocument(
              "$push" -> BsonDocument(
                buildPushKey -> buildCreationDocument(resource)
              )
            ))
          .toFuture()
          .map(_ => resource)
      }
  }

  def update(id: String, resourcePayload: T)
            (implicit company: Company, ec: ExecutionContext): Future[UpdateResult] = {
    companyCollection
      .collectionFuture
      .flatMap { collection =>
        collection
          .updateOne(
            BsonDocument("id" -> company.id, s"$resourceName.id" -> id),
            BsonDocument("$set" -> buildUpdateDocument(resourcePayload))
          )
          .toFuture()
      }
  }

  def update(parentId: String, id: String, resourcePayload: T)
            (implicit company: Company, ec: ExecutionContext): Future[UpdateResult] = {
    companyCollection
      .collectionFuture
      .flatMap { collection =>
        collection
          .updateOne(
            BsonDocument("id" -> company.id),
            BsonDocument("$set" -> buildUpdateDocument(resourcePayload)),
            new UpdateOptions().arrayFilters(asList(
              Filters.eq("parent.id", parentId),
              Filters.eq("child.id", id)
            ))
          )
          .toFuture()
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

  def delete(parentId: String, id: String)
            (implicit company: Company, ec: ExecutionContext): Future[UpdateResult] = {
    companyCollection
      .collectionFuture
      .flatMap{ collection =>
        collection
          .updateOne(
            BsonDocument(
              "id" -> company.id,
              s"${parentResource.get}.$resourceName.id" -> id
            ),
            BsonDocument("$pull" ->
              BsonDocument(s"${parentResource.get}.$$.$resourceName" -> BsonDocument("id" -> id))
            )
          ).toFuture()
      }
  }

  private def buildPushFilter(maybeParentId: Option[String] = None)
                             (implicit company: Company): BsonDocument = {
    maybeParentId
      .map { parentId =>
        val parentKey = parentResource.getOrElse {
          throw new IllegalStateException("Nested resource without parent setup.")
        }

        BsonDocument("id" -> company.id, s"$parentKey.id" -> parentId)
      }
      .getOrElse(BsonDocument("id" -> company.id))
  }

  private def buildPushKey: String = {
    parentResource
      .map(k => s"$k.$$.$resourceName")
      .getOrElse(resourceName)
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
    val collectionFields = resource.filter(_._2.isArray).keys

    val updateSetupValues = Map[String, BsonValue](
      "updatedAt" -> BsonDateTime(DateTime.now.getMillis)
    )

    (resource ++ updateSetupValues -- immutableFields -- collectionFields).map(m => buildUpdateKey(m._1) -> m._2)
  }

  private def buildUpdateKey(key: String) = {
    parentResource
      .map { parentKey =>
        s"$parentKey.$$[parent].$resourceName.$$[child].$key"
      }
      .getOrElse(s"$resourceName.$$.$key")
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
    case _: List[Any] => new BsonArray()
    case Some(x: String) => BsonString(x)
    case x: DateTime => BsonDateTime(x.getMillis)
    case x: LocalTime => BsonInt32(x.getMillisOfDay)
    case _ => new BsonNull
  }
}
