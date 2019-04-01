package authentication

class UnauthorizedException extends RuntimeException

object UnauthorizedException {
  def apply(): UnauthorizedException = new UnauthorizedException()
}
