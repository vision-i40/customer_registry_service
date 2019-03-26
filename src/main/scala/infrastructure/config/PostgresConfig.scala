package infrastructure.config

trait PostgresConfig extends Configuration {
  private val ROOT = "postgres"
  private val HOST = s"$ROOT.host"
  private val PORT = s"$ROOT.port"
  private val DATABASE = s"$ROOT.database"
  private val USERNAME = s"$ROOT.username"
  private val PASSWORD = s"$ROOT.password"
  private val SESSIONS_PER_HOST = s"$ROOT.sessions_per_host"

  def host: String = config.getString(HOST)
  def port: Int = config.getInt(PORT)
  def database: String = config.getString(DATABASE)
  def username: String = config.getString(USERNAME)
  def password: String = config.getString(PASSWORD)
  def sessions_per_host: Int = config.getInt(SESSIONS_PER_HOST)
}

object PostgresConfig extends PostgresConfig
