package authentication

import authentication.exceptions.UnauthorizedException
import com.google.inject.Singleton
import domain.models.{Company, User}

@Singleton
class AuthenticatedUser {
  private var user: Option[User] = None
  private var company: Option[Company] = None

  def setUser(newUser: User): AuthenticatedUser = {
    this.user.synchronized {
      this.user = Some(newUser)
    }

    this
  }

  def setCompany(newCompany: Company): AuthenticatedUser = {
    this.company.synchronized {
      this.company = Some(newCompany)
    }

    this
  }

  def getUser: User = {
    this.user.getOrElse {
      throw UnauthorizedException("User is not set in context.")
    }
  }

  def getCompany: Company = {
    this.company.getOrElse {
      throw UnauthorizedException("Company is not set in context.")
    }
  }
}
