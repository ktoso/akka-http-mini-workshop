package samples.scalaexchange.step8

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonStreamingSupport
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.directives.EntityStreamingDirectives
import akka.stream.ActorMaterializer
import samples.scalaexchange.utils.MakingUpData

import scala.util.Success

trait PassThroughStreamsService extends Directives
  with SprayJsonStreamingSupport
  with EntityStreamingDirectives
  with MakingUpData {

  implicit def system: ActorSystem
  implicit def materializer: ActorMaterializer
  private implicit def ec = system.dispatcher

  final val NoLimit = Long.MaxValue

  val passThroughRoutes =
    pathPrefix("get") {
      parameter('site) { site =>
        val req = HttpRequest(uri = s"http://$site")

        onComplete(Http().singleRequest(req)) {
          case Success(res) =>
            val entityStream = res.entity.withSizeLimit(NoLimit).dataBytes
            complete(HttpEntity(ContentTypes.`text/html`, entityStream))
        }
      }
    }

}