package samples.scalaexchange.step8

import akka.http.scaladsl.Http
import akka.http.scaladsl.server._
import samples.scalaexchange.utils.SampleApp

import scala.io.StdIn

object PassThroughStreamsHttpServiceApp extends SampleApp
  with PassThroughStreamsService {

  /*
    We can implement:
      - request comes in
        - we request from a downstream http service
      - we respond to the request using the response _we_ got

      end to end back-pressured streaming :-)
   */


  // our routes:
  val route: Route =
    passThroughRoutes

  // start the http server:
  val bindingFuture = Http().bindAndHandle(route, "127.0.0.1", 8080)

  StdIn.readLine("Press [RETURN] to quit...")
}
