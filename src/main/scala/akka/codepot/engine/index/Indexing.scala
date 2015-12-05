package akka.codepot.engine.index

import java.io.{File, FileInputStream}
import java.util.zip.GZIPInputStream

import akka.codepot.common.FuzzyJsonBracketCountingFraming
import akka.codepot.engine.IndexerApp._
import akka.stream.io.{Framing, SynchronousFileSource}
import akka.stream.scaladsl.Source
import akka.stream.{ActorAttributes, Supervision}
import akka.util.ByteString
import spray.json.JsObject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

trait Indexing {

  private val ChunkSize = 8192

  final case class Value(key: String, word: Set[String])

  import akka.stream.io.Implicits._

  private val DumpFilename = "20150817.json.tar.gz"
  val indexFileSource = Source.inputStream(() => new GZIPInputStream(new FileInputStream(DumpFilename)))

  val wikipediaCachedKeywordsSource: Source[ByteString, Future[Long]] =
    SynchronousFileSource(new File("keywords.csv"))
      .via(Framing.delimiter(ByteString("\n"), Int.MaxValue))

  val rawWikipediaSource: Source[JsObject, Future[Long]] = {
    import spray.json._

    def parseJson(bs: ByteString) = bs.utf8String.parseJson.asJsObject
    Source.inputStream(() => new GZIPInputStream(new FileInputStream(input)))
      //  Source.synchronousFile(new File(input), ChunkSize)
      .via(FuzzyJsonBracketCountingFraming(allowTruncation = false))
      .map(parseJson)
      .filter(_.fields("type").toString() == """"item"""")
      .withAttributes(ActorAttributes.supervisionStrategy(_ => Supervision.Resume))
  }

  val wikipediaParsedKeywordsSource: Source[ByteString, Future[Long]] = {
      rawWikipediaSource
      .mapAsyncUnordered(8)(jsonToIndexValue)
      .map(in => {
        if (c.incrementAndGet() % 1000 == 0) println(in.key)
        ByteString(in.key + "\n")
      })
      .withAttributes(ActorAttributes.supervisionStrategy(_ => Supervision.Resume))
  }

  private def jsonToIndexValue(js: JsObject)(implicit d: ExecutionContext): Future[Value] = {
    Future {
      Value(
        js.fields("labels")
          .asJsObject.fields("en")
          .asJsObject.fields("value")
          .toString().replaceAll("\"", ""),
        js.fields("descriptions")
          .asJsObject.fields("en")
          .asJsObject.fields("value").toString()
          .drop(1).dropRight(1).split(" ").toSet
      )
    }
  }

}
