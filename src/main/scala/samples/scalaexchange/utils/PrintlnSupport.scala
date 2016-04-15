package samples.scalaexchange.utils

import akka.NotUsed
import akka.http.scaladsl.marshalling.{Marshaller, Marshalling}
import akka.http.scaladsl.model.MediaTypes
import akka.stream.{Attributes, Materializer}
import akka.stream.scaladsl.{Sink, Keep, Source, Flow}
import akka.util.ByteString

import scala.concurrent.duration.{Duration, FiniteDuration}

trait PrintlnSupport {

  // specialized for our demo
  def printlnEvery[T](interval: FiniteDuration): Sink[T, NotUsed] = {
    val ticks = Source.tick(interval, interval, ())

    Flow[T]
      .conflateWithSeed(_ => 1) { case (aggregated, n) => aggregated + 1 }
      .zipWith(ticks) { (counter, ticks) => counter } // show Keep.left
      .to(Sink.foreach(it => println(s"$it elements were passed through during $interval...")))
      .withAttributes(Attributes.inputBuffer(1, 1))
  }

}

