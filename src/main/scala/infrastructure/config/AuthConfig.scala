package infrastructure.config

import java.util.Base64

import com.google.inject.Singleton

@Singleton
class AuthConfig extends Configuration {
  private val ROOT = "authentication"
  private val VERIFICATION_EXPIRATION = s"$ROOT.verification_expiration_in_minutes"
  private val VERIFICATION_SALT = s"$ROOT.verification_salt"
  private val BCRYPT_SALT = s"$ROOT.bcrypt_salt"
  private val TOKEN_EXPIRATION = s"$ROOT.token_expiration_in_seconds"
  private val TOKEN_SECRET_KEY = s"$ROOT.token_secret_key"

  def bcryptSalt: String = Base64.getDecoder.decode(config.getString(BCRYPT_SALT)).map(_.toChar).mkString
  def secretKey: String = config.getString(TOKEN_SECRET_KEY)
  def tokenExpirationInSeconds: Long = config.getLong(TOKEN_EXPIRATION)
  def verificationExpirationInMinutes: Int = config.getInt(VERIFICATION_EXPIRATION)
  def verificationSalt: String = config.getString(VERIFICATION_SALT)
}


