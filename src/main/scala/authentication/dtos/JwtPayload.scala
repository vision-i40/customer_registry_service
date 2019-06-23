package authentication.dtos

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper

case class JwtPayload(
  id: String,
  exp: Long,
  iat: Long
)

object JwtPayload {
  val objectMapper = new ObjectMapper() with ScalaObjectMapper
  objectMapper.registerModule(DefaultScalaModule)

  def parse(payload: String): JwtPayload = {
    objectMapper.readValue(payload, classOf[JwtPayload])
  }
}
