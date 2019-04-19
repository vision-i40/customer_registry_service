package unit.authentication

import authentication.{SignInService, TokenBuilder}
import domain.models.{Company, User}
import domain.repositories.{CompanyRepository, UserRepository}
import infrastructure.config.EncryptionConfig
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import pdi.jwt.Jwt
import support.VisionAsyncSpec
import support.builders.{CompanyBuilder, UserBuilder}

import scala.concurrent.Future

class SignInServiceTest extends VisionAsyncSpec with MockitoSugar {
  private val config: EncryptionConfig = mock[EncryptionConfig]
  private val userRepository: UserRepository = mock[UserRepository]
  private val companyRepository: CompanyRepository = mock[CompanyRepository]
  private val tokenBuilder: TokenBuilder = mock[TokenBuilder]
  private val service = new SignInService(userRepository, companyRepository, tokenBuilder,config)

  behavior of "generate token"
  it should "return " in {
    val email = "email@@email.com"
    val password = "password"
    val expectedCompany: Company = CompanyBuilder().build
    val expectedUser: User = UserBuilder(defaultCompanyId = expectedCompany.id).build
    val expectedJWTPayload = s"""{"id":"${expectedUser.id}"}"""

    when(config.secretKey).thenReturn("a-secret-key")
    when(tokenBuilder.build(expectedUser)).thenReturn(Jwt.encode(expectedJWTPayload))
    when(userRepository.getByEmailAndPassword(email, password)).thenReturn(Future.successful(Some(expectedUser)))
    when(companyRepository.findById(expectedUser.defaultCompanyId)).thenReturn(Future.successful(Some(expectedCompany)))

    val tokenFuture = service.generateToken(email, password)

    tokenFuture.map { authenticationResponse =>
      val decodedToken = Jwt.decode(authenticationResponse.token)
      decodedToken.isSuccess shouldEqual true
      decodedToken.get shouldEqual expectedJWTPayload
      authenticationResponse.user shouldEqual expectedUser
      authenticationResponse.company shouldEqual expectedCompany
    }
  }
}
