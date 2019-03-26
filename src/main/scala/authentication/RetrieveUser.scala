package authentication

import java.time.LocalDateTime

import com.twitter.finagle.postgres.Row
import com.twitter.util.Future
import domain.User
import infrastructure.config.PostgresConfig
import infrastructure.postgres.PostgresDb
import org.joda.time.DateTime

object RetrieveUser {
  private val SELECT_QUERY = """SELECT * FROM users WHERE (email="%s" OR username="%s") AND password="%s" LIMIT 1;"""

  def apply(identity: String, password: String)
           (implicit postgresDb: PostgresDb, config: PostgresConfig): Future[Option[User]] = {
    val encryptedPassword = password

    val selectQuery = SELECT_QUERY.format(identity, identity, encryptedPassword)

    postgresDb
      .client
      .select(selectQuery)(buildUser)
      .map(_.headOption)
  }

  private def buildUser(row: Row): User = {
    User(
      id = row.get[String]("id"),
      email = row.get[String]("email"),
      username = row.get[String]("username"),
      password = row.get[String]("password"),
      createdAt = new DateTime(row.get[LocalDateTime]("createdAt")),
      updatedAt = new DateTime(row.get[LocalDateTime]("updatedAt"))
    )
  }
}
