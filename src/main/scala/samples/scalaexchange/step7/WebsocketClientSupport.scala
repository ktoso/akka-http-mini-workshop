package samples.scalaexchange.step7

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.model.ws._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import akka.stream.scaladsl.{Source, Sink, Keep, Flow}
import akka.util.ByteString
import samples.scalaexchange.step7.WebSocketModels.Tweet

import scala.concurrent.Future

trait WebsocketClientSupport {
  def connect[T](endpoint: Uri, handler: Flow[Tweet, String, T])(implicit sys: ActorSystem, materializer: Materializer): Future[T] = {
    implicit val ec = materializer.executionContext
    val wsFlow: Flow[Message, Message, T] =
      Flow[Message]
        .collect {
          case TextMessage.Strict(msg) ⇒ Unmarshal(ByteString(msg)).to[Tweet] // Future[Tweet]
        }
        .mapAsync(1)(identity) // flattening the future
        .viaMat(handler)(Keep.right)
        .map(TextMessage(_))

    val (fut, t) = Http().singleWebSocketRequest(WebSocketRequest(endpoint), wsFlow)
    fut.map {
      case v: ValidUpgrade ⇒ t
      case InvalidUpgradeResponse(response, cause) ⇒ throw new RuntimeException(s"Connection to chat at $endpoint failed with $cause")
    }
  }

    def connect[T](endpoint: Uri, in: Sink[Tweet, Any], out: Source[String, Any])(implicit system: ActorSystem, materializer: Materializer): Future[Unit] =
      connect(endpoint, Flow.fromSinkAndSource(in, out)).map(_ ⇒ ())(system.dispatcher)

}
