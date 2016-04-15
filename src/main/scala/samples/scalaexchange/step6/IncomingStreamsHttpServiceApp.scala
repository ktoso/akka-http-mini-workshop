package samples.scalaexchange.step6

import akka.http.scaladsl.Http
import akka.http.scaladsl.server._
import samples.scalaexchange.utils.SampleApp

import scala.io.StdIn

object IncomingStreamsHttpServiceApp extends SampleApp
  with IncomingStreamsService {

  // our routes:
  val route: Route =
    incomingStreams


  // start the http server:
  val bindingFuture = Http().bindAndHandle(route, "127.0.0.1", 8080)

}