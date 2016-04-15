package samples.scalaexchange.step3

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer
import samples.scalaexchange.utils.SampleApp

import scala.io.StdIn

object SimpleStreamHttpServiceApp extends SampleApp
  with HelloWorldService
  with SimpleStreamService {

  val myExceptionHandler = ExceptionHandler {
    case ex: Exception =>
      complete {
        <html>
          <body>
            {ex.getMessage}
          </body>
        </html>
      }
  }

  // our routes:
  val route: Route =
    respondWithHeader(RawHeader("X-lol", "outer")) {
    handleExceptions(myExceptionHandler) {
      helloRoutes ~ simpleStreamRoutes ~
        respondWithHeader(RawHeader("X-lol", "inner")) {
          throw new Exception("Boom!")
        }
    }
    }

  // start the http server:
  val bindingFuture = Http().bindAndHandle(route, "127.0.0.1", 8080)

}