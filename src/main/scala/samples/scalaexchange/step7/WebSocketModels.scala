package samples.scalaexchange.step7

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonStreamingSupport
import akka.http.scaladsl.marshalling.Marshaller
import akka.http.scaladsl.unmarshalling.Unmarshaller
import akka.util.ByteString
import spray.json.{RootJsonFormat, DefaultJsonProtocol}

object WebSocketModels extends DefaultJsonProtocol {

  final case class Tweet(nickname: String, tweet: String)

    implicit val statsFormat: RootJsonFormat[Tweet] = jsonFormat2(Tweet)

    // TODO those will not be needed to be written explicitly
    // NOTE: these are work in progress, will be simplified or changed
    implicit def tweetMarshaller: Marshaller[Tweet, ByteString] = SprayJsonStreamingSupport.sprayByteStringMarshaller[Tweet]
    implicit def tweetUnmarshaller: Unmarshaller[ByteString, Tweet] = SprayJsonStreamingSupport.sprayJsonByteStringUnmarshaller[Tweet]

}
