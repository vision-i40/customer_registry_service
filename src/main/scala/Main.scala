import authentication.AuthenticationController
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.routing.HttpRouter
import infrastructure.config.MongoDBConfig
import infrastructure.mongodb.MongoDB

object Main extends HttpServer {
  override def configureHttp(router: HttpRouter): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global
    implicit val mongoConfig: MongoDBConfig = MongoDBConfig
    implicit val mongodb: MongoDB = MongoDB()

    router
      .add(AuthenticationController())
  }

}
