package samples.scalaexchange.step5

import akka.http.scaladsl.Http
import akka.http.scaladsl.server._
import samples.scalaexchange.utils.SampleApp

import scala.io.StdIn

object TweetsHttpServiceApp extends SampleApp
  with TweetsStreamService {

  // our routes:
  val route: Route =
    tweetsStreamRoutes

  // start the http server:
  val bindingFuture = Http().bindAndHandle(route, "127.0.0.1", 8080)

  StdIn.readLine("Press [RETURN] to quit...")
  system.terminate()
}