package authentication

import com.twitter.util.Future

object GenerateToken {
  def apply(implicit repository: UserRepository): Future[AuthenticationToken] = ???
}
