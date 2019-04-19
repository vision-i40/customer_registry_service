package authentication.exceptions

class BadRequestException(message: String) extends RuntimeException(message)

object BadRequestException {
  def apply(message: String): BadRequestException = new BadRequestException(message)
}
