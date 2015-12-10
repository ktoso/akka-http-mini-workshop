package samples.scalaexchange.step4

import java.io.File

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.scaladsl.Sink
import samples.scalaexchange.utils.SampleApp

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object RawClientApp extends SampleApp {

  // --- app ---

  val downloadedOut = new File("downloaded.out")
  val NoLimit = Long.MaxValue

  val request = HttpRequest(uri = "https://skillsmatter.com/conferences/6862-scala-exchange-2015")
  val response: Future[HttpResponse] =
    Http().singleRequest(request)

  val bytesWritten =
    response flatMap { r =>
      val bytes = r.entity.withSizeLimit(NoLimit).dataBytes

      val completion =
        bytes
          .runWith(Sink.file(downloadedOut))

      completion
    }

  val result = Await.result(bytesWritten, atMost = 1.minute)
  println(s"Download of ${request.uri} successful! Was $result bytes.".green)
  system.terminate()
}