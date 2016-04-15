package samples.scalaexchange.step7

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonStreamingSupport
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.ws.{TextMessage, Message}
import akka.http.scaladsl.server.directives.EntityStreamingDirectives
import akka.http.scaladsl.server.{Directives, JsonSourceRenderingMode}
import akka.stream.{Attributes, ActorMaterializer}
import akka.stream.scaladsl.Flow
import akka.util.ByteString
import samples.scalaexchange.step7.WebSocketModels.Tweet
import samples.scalaexchange.utils.MakingUpData

trait WebsocketStreamsService extends Directives
  with SprayJsonStreamingSupport
  with EntityStreamingDirectives
  with MakingUpData {

  implicit def system: ActorSystem

  implicit def materializer: ActorMaterializer

  private implicit def ec = system.dispatcher

  val websocketStreams =
    pathPrefix("ws") {
      path("tweetEcho") {
        handleWebSocketMessages {
          Flow[Message]
            .take(10)
            .log("from-client").withAttributes(Attributes.logLevels(Logging.InfoLevel))
            .collect { case m: TextMessage.Strict => Tweet(shortName(), m.text) }
            .map(_.toJson.compactPrint)
            .map(TextMessage.Strict)
            .log("to-client").withAttributes(Attributes.logLevels(Logging.InfoLevel))
        }
      }
    }

}