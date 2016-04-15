package samples.scalaexchange.step7

import akka.http.scaladsl.Http
import akka.http.scaladsl.server._
import samples.scalaexchange.utils.SampleApp

import scala.io.StdIn

object WebsocketStreamsHttpServiceApp extends SampleApp
  with WebsocketStreamsService {

  // our routes:
  val route: Route =
    websocketStreams

  // start the http server:
  val bindingFuture = Http().bindAndHandle(route, "127.0.0.1", 8080)

}