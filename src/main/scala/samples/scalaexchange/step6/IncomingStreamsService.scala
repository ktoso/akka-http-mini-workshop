package samples.scalaexchange.step6

import java.io.File

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonStreamingSupport
import akka.http.scaladsl.marshalling.Marshaller
import akka.http.scaladsl.server.directives.EntityStreamingDirectives
import akka.http.scaladsl.server.{Directives, JsonSourceRenderingMode}
import akka.http.scaladsl.unmarshalling.Unmarshaller
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, Flow, Sink, Source}
import akka.util.ByteString
import spray.json.{RootJsonFormat, DefaultJsonProtocol}

import scala.util.{Success, Failure}

trait IncomingStreamsService extends Directives
  with SprayJsonStreamingSupport
  with EntityStreamingDirectives
  with ModelJsonProtocol {

  implicit def system: ActorSystem
  implicit def materializer: ActorMaterializer
  private implicit def ec = system.dispatcher

  implicit val jsonRenderingMode = JsonSourceRenderingMode.LineByLine
  private val `/dev/null` = new File("/dev/null")

  def incomingStreams =
    rawIncoming ~ incomingJsonFramed


  /*
    http POST 127.0.0.1:8080/raw 'Accept:*' < tweets.js

    HTTP/1.1 200 OK
    Content-Length: 37
    Content-Type: text/plain; charset=UTF-8
    Date: Thu, 10 Dec 2015 12:57:52 GMT
    Server: akka-http/2.4.1

    Thanks for sending us 12165060 bytes!

   */
  val rawIncoming =   path("raw") {
    extractRequest { req =>
      val bytes: Source[ByteString, Any] = req.entity.dataBytes
      val doneDraining = bytes
        // .alsoTo(Flow[ByteString].map(_.length).fold(0)(_+_).map("Uploaded: " + _).to(Sink.foreach(println)))
        .runWith(FileIO.toFile(`/dev/null`))

      onComplete(doneDraining) {
        case Success(written) => complete(s"Thanks for sending us $written bytes!")
        case Failure(ex) => complete(500 -> s"Whoops, failed upload because: ${ex.getMessage}")
      }
      // TODO show truncation
    }
  }

  /*
    $ http POST 127.0.0.1:8080/jsonTweets 'Accept:*' < tweets.js
    HTTP/1.1 200 OK
    Content-Length: 102
    Content-Type: text/plain; charset=UTF-8
    Date: Thu, 10 Dec 2015 13:22:29 GMT
    Server: akka-http/2.4.1

    Sum of lengths of names: Tweet stats:
               Total tweets: 102695
      Avg length of nickname: 5.0

   */
  val incomingJsonFramed =
    path("jsonTweets") {
      entity(stream[Tweet]) { tweets =>

        complete {
          // TODO 1) what if we `fold` instead?
          // TODO 2) can we conflate instead?

          tweets
            .runFold(TweetStats())(_ updated _)
//            1)
//            .fold(TweetStats())(_ updated _)
//            .map(_.toString)
//            2)
//            .conflate(TweetStats().updated)(_ updated _)
        }

      }
    }
}

final case class Tweet(nickname: String, tweet: String)

trait ModelJsonProtocol extends DefaultJsonProtocol {
  implicit val tweetFormat: RootJsonFormat[Tweet] = jsonFormat2(Tweet)

  case class TweetStats(total: Long = 0, lengthOfNames: Long = 0) {
    def updated(t: Tweet): TweetStats = copy(total + 1, lengthOfNames + t.nickname.length)
    def avgLengthOfName: Double = lengthOfNames.toDouble / total
    override def toString = "Tweet stats: \n" +
      "            Total tweets: " + total + "\n" +
      "  Avg length of nickname: " + avgLengthOfName + "\n"
  }

  implicit val statsFormat: RootJsonFormat[TweetStats] = jsonFormat2(TweetStats)


  // TODO those will not be needed to be written explicitly
  // NOTE: these are work in progress, will be simplified or changed
  implicit def tweetMarshaller: Marshaller[Tweet, ByteString] = SprayJsonStreamingSupport.sprayByteStringMarshaller[Tweet]
  implicit def tweetUnmarshaller: Unmarshaller[ByteString, Tweet] = SprayJsonStreamingSupport.sprayJsonByteStringUnmarshaller[Tweet]
}