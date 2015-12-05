package akka.codepot.service

import akka.actor.ActorSystem
import akka.codepot.common.BaseApp
import akka.codepot.engine.SearchEngineNotYetInitializedException
import akka.http.scaladsl.Http
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer

object HttpServiceApp extends BaseApp {

  override protected def run(_system: ActorSystem, opts: Map[String, String]): Unit =
    new HelloWorldService with SearchService {
      // infrastructure:
      implicit def system = _system
      implicit val materializer: ActorMaterializer = ActorMaterializer()

      val config = system.settings.config

      val myExceptionHandler = ExceptionHandler {
        case SearchEngineNotYetInitializedException(msg) =>
          complete(<html>
            <body>
              {msg}
            </body>
          </html>)
      }


      // our routes:
      val route: Route = handleExceptions(myExceptionHandler) {
        helloRoutes ~ searchRoutes
      }

      // start the http server:
      val bindingFuture = Http().bindAndHandle(route, "127.0.0.1", config.getInt("codepot.http.port"))
    }
}