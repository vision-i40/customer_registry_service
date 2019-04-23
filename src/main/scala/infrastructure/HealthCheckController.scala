package infrastructure

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import infrastructure.mongodb.MongoDB
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class HealthCheckController @Inject()(mongoConnection: MongoDB) extends Controller {
  case class HealthStatus(status: String)

  get("/health") { _: Request =>
    mongoConnection
      .database
      .listCollectionNames()
      .toFuture()
      .map { _ =>
        response.ok.body(HealthStatus(
          status = "Alive"
        ))
      }

  }
}
