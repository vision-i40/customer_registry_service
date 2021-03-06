package infrastructure.config

import com.google.inject.Singleton

@Singleton
class MongoDBConfig extends Configuration {
  private val ROOT = "mongodb"
  private val DATABASE = s"$ROOT.database"
  private val SSL_ENABLED = s"$ROOT.ssl_enabled"
  private val CONNECTION_STRING = s"$ROOT.connection_string"

  def database: String = config.getString(DATABASE)
  def isSslEnabled: Boolean = config.getBoolean(SSL_ENABLED)
  def connectionString: String = config.getString(CONNECTION_STRING)
}
