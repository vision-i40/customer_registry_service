package infrastructure.postgres

import com.twitter.finagle.Postgres
import com.twitter.finagle.postgres.PostgresClientImpl
import infrastructure.config.PostgresConfig

trait PostgresDb {
  def client(implicit config: PostgresConfig): PostgresClientImpl
}

object PostgresDb extends PostgresDb {

  private var client: Option[PostgresClientImpl] = None

  override def client(implicit config: PostgresConfig): PostgresClientImpl = {
    client.getOrElse {
      createClient
    }
  }

  private def createClient(implicit config: PostgresConfig): PostgresClientImpl = {
    val postgresClient = Postgres.Client()
      .withCredentials(config.username, Some(config.password))
      .database(config.database)
      .withSessionPool.maxSize(config.sessions_per_host)
      .withBinaryResults(true)
      .withBinaryParams(true)
      .newRichClient(s"${config.host}:${config.port}")

    client = this.synchronized {
      Some(postgresClient)
    }

    postgresClient
  }
}
