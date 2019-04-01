package infrastructure.config

trait EncryptionConfig extends Configuration {
  private val ROOT = "encryption"
  private val SALT = s"$ROOT.salt"
  private val SECRET_KEY = s"$ROOT.secret_key"

  def salt: String = config.getString(SALT)
  def secretKey: String = config.getString(SECRET_KEY)
}

object EncryptionConfig extends EncryptionConfig


