import authentication.AuthenticationController
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.routing.HttpRouter

object Main extends HttpServer {
  override def configureHttp(router: HttpRouter): Unit = {
    router
      .add(AuthenticationController())
  }

}
