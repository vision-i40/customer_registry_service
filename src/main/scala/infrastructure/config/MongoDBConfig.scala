package infrastructure.config

trait MongoDBConfig extends Configuration {
  private val ROOT = "mongodb"
  private val HOST = s"$ROOT.host"
  private val PORT = s"$ROOT.port"
  private val DATABASE = s"$ROOT.database"
  private val USERNAME = s"$ROOT.username"
  private val PASSWORD = s"$ROOT.password"
  private val CONNECTION_STRING = s"$ROOT.connection_string"

  def host: String = config.getString(HOST)
  def port: Int = config.getInt(PORT)
  def database: String = config.getString(DATABASE)
  def username: String = config.getString(USERNAME)
  def password: String = config.getString(PASSWORD)
  def connectionString: String = config.getString(CONNECTION_STRING)
}

object MongoDBConfig extends MongoDBConfig
