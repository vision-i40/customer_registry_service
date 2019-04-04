import authentication.AuthenticationController
import com.twitter.finagle.http.filter.Cors
import com.twitter.finagle.http.filter.Cors.HttpFilter
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.routing.HttpRouter
import infrastructure.CorsController
import infrastructure.config.MongoDBConfig
import infrastructure.mongodb.MongoDB

object Main extends HttpServer {
  override def configureHttp(router: HttpRouter): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global
    implicit val mongoConfig: MongoDBConfig = MongoDBConfig
    implicit val mongodb: MongoDB = MongoDB()

    router
      .filter(new HttpFilter(Cors.UnsafePermissivePolicy))
      .add[CorsController]
      .add(AuthenticationController())
  }

}
