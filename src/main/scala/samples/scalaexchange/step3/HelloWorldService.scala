package samples.scalaexchange.step3

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.xml.ScalaXmlSupport
import akka.http.scaladsl.server.Directives
import akka.stream.ActorMaterializer

trait HelloWorldService extends Directives with ScalaXmlSupport {
  implicit def system: ActorSystem
  implicit def materializer: ActorMaterializer

  def helloRoutes =
    path("hello") {
      get {
        complete {
          <html>
            <head>
              <title>Hello world from akka-http</title>
            </head>
            <body>
              <h1>Say hello to akka-http</h1>
            </body>
          </html>
        }
      }
    }
}
