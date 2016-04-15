package samples.scalaexchange.step3

import java.io.File

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.xml.ScalaXmlSupport
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.HttpCharsets.`UTF-8`
import akka.http.scaladsl.server.Directives
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, Source}

trait SimpleStreamService extends Directives with ScalaXmlSupport {
  implicit def system: ActorSystem

  implicit def materializer: ActorMaterializer

  val `text/csv` = ContentType(MediaTypes.`text/csv`, `UTF-8`)
  val DataFile = new File("./data.csv")

  def simpleStreamRoutes =
    path("stream" / "simple") {
      complete {
        HttpEntity(`text/csv`, FileIO.fromFile(DataFile))
      }
    }
}
