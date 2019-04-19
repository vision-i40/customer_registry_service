package authentication.dtos

import domain.models.{Company, User}

case class UserInformation(user: User, defaultCompany: Option[Company])
