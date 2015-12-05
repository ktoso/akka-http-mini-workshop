package akka.codepot.service

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
          <h1>Say hello to akka-http</h1>
        }
      }
    }
}
