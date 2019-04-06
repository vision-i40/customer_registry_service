package integration.domain.repositories

import domain.models.User
import domain.repositories.UserRepository
import infrastructure.config.{EncryptionConfig, MongoDBConfig}
import infrastructure.mongodb.MongoDB
import org.mindrot.jbcrypt.BCrypt
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import support.builders.UserBuilder
import support.{MongoDBHelper, VisionAsyncSpec}

import scala.concurrent.Future

class UserRepositoryTest extends VisionAsyncSpec with BeforeAndAfterEach with BeforeAndAfterAll {
  implicit private val collectionName: String = "users"
  private val mongoConfig: MongoDBConfig = new MongoDBConfig()
  private val db: MongoDB = new MongoDB(mongoConfig)
  private val encryptionConfig: EncryptionConfig = new EncryptionConfig()
  private val repository: UserRepository = new UserRepository(db, encryptionConfig)

  import domain.models.User._

  override def beforeEach(): Unit = {
    super.beforeEach()
    MongoDBHelper.clearCollection()
  }

  behavior of "retrieving user by user and password"
  it should "retrieve user when the user is on the list" in {
    val email = "email@email"
    val password = "password"
    val firstNoiseUser = UserBuilder().build
    val expectedUser = UserBuilder(email = email, password = BCrypt.hashpw(password, encryptionConfig.salt)).build
    val secondNoiseUser = UserBuilder().build

    MongoDBHelper.insert(firstNoiseUser)
    MongoDBHelper.insert(expectedUser)
    MongoDBHelper.insert(secondNoiseUser)

    val userFuture: Future[Option[User]] = repository.getByEmailAndPassword(email, password)

    userFuture.map { actualUser =>
      actualUser shouldEqual Some(expectedUser)
    }
  }

  it should "NOT retrieve user when the user is not on the list" in {
    val email = "email@email"
    val password = "password"
    val firstNoiseUser = UserBuilder().build
    val secondNoiseUser = UserBuilder().build

    MongoDBHelper.insert(firstNoiseUser)
    MongoDBHelper.insert(secondNoiseUser)

    val userFuture: Future[Option[User]] = repository.getByEmailAndPassword(email, password)

    userFuture.map { actualUser =>
      actualUser shouldEqual None
    }
  }

}
