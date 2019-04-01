package authentication

class UnauthorizedException(message: String) extends RuntimeException(message)

object UnauthorizedException {
  def apply(message: String): UnauthorizedException = new UnauthorizedException(message)
}
