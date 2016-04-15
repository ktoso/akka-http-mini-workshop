package samples.scalaexchange.step7

import akka.event.Logging
import akka.stream.Attributes
import akka.stream.ThrottleMode.{Shaping, Enforcing}
import akka.stream.scaladsl.{Flow, Sink, Source}
import samples.scalaexchange.utils.SampleApp

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.io.StdIn

/**
  * See: https://github.com/jrudolph/akka-http-scala-js-websocket-chat/blob/master/cli/src/main/scala/example/akkawschat/cli/ChatClient.scala
  */
object WebsocketClient extends SampleApp
  with WebsocketClientSupport {

  private val uri = "ws://127.0.0.1:8080/ws/tweetEcho"
  println(s"Connecting to: $uri")

  private val helloSource = Source.repeat("Hello!").throttle(1, per = 1.second, maximumBurst = 1, mode = Shaping)
  private val printlnSink = Flow[Any].log("from-server").withAttributes(Attributes.logLevels(Logging.InfoLevel)).to(Sink.foreach(println))

  private val singleExchange = connect(uri, printlnSink, helloSource)
  Await.result(singleExchange, 10.seconds) // connection OK.

}
