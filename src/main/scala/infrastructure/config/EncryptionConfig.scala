package infrastructure.config

import java.util.Base64

import com.google.inject.Singleton

@Singleton
class EncryptionConfig extends Configuration {
  private val ROOT = "encryption"
  private val SALT = s"$ROOT.salt"
  private val SECRET_KEY = s"$ROOT.secret_key"

  def salt: String = Base64.getDecoder.decode(config.getString(SALT)).map(_.toChar).mkString
  def secretKey: String = config.getString(SECRET_KEY)
}


