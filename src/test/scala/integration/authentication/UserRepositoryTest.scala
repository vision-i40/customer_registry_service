package integration.authentication

import java.util.UUID

import authentication.UserRepository
import domain.{Company, User}
import infrastructure.config.MongoDBConfig
import infrastructure.mongodb.MongoDB
import org.joda.time.DateTime
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import support.build.{CompanyBuilder, UserBuilder}
import support.{MongoDbHelper, VisionAsyncSpec}

import scala.concurrent.Future

class UserRepositoryTest extends VisionAsyncSpec with BeforeAndAfterEach with BeforeAndAfterAll {
  implicit private val config: MongoDBConfig = MongoDBConfig
  implicit private val db: MongoDB = MongoDB()
  private val repository: UserRepository = UserRepository()

  override def beforeEach(): Unit = {
    super.beforeEach()
    MongoDbHelper.clearCompanyCollection()
  }

  behavior of "retrieving user by user and password"
  it should "retrieve user when the user is on the list" in {
    val email = "email@email"
    val password = "password"
    val firstNoiseUser = UserBuilder().build
    val expectedUser = UserBuilder(email = email, password = password).build
    val secondNoiseUser = UserBuilder().build
    val expectedCompany = CompanyBuilder(users = List(firstNoiseUser, expectedUser, secondNoiseUser)).build

    MongoDbHelper.insert(expectedCompany)

    val userFuture: Future[Option[User]] = repository.getByEmailAndPassword(expectedCompany.id.get, email, password)

    userFuture.map { actualUser =>
      actualUser shouldEqual Some(expectedUser)
    }
  }

  it should "NOT retrieve user when the user is not on the list" in {
    val email = "email@email"
    val password = "password"
    val firstNoiseUser = UserBuilder().build
    val secondNoiseUser = UserBuilder().build
    val expectedCompany = CompanyBuilder(users = List(firstNoiseUser, secondNoiseUser)).build

    MongoDbHelper.insert(expectedCompany)

    val userFuture: Future[Option[User]] = repository.getByEmailAndPassword(expectedCompany.id.get, email, password)

    userFuture.map { actualUser =>
      actualUser shouldEqual None
    }
  }

  it should "NOT retrieve user when there is no user" in {
    val email = "email@email"
    val password = "password"
    val expectedCompany = CompanyBuilder(users = List()).build

    MongoDbHelper.insert(expectedCompany)

    val userFuture: Future[Option[User]] = repository.getByEmailAndPassword(expectedCompany.id.get, email, password)

    userFuture.map { actualUser =>
      actualUser shouldEqual None
    }
  }

  it should "NOT retrieve user when there is no company" in {
    val email = "email@email"
    val password = "password"

    val anyCompanyId = UUID.randomUUID().toString

    val userFuture: Future[Option[User]] = repository.getByEmailAndPassword(anyCompanyId, email, password)

    userFuture.map { actualUser =>
      actualUser shouldEqual None
    }
  }

}
