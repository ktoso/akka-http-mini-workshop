package samples.scalaexchange.step1

import java.io.File

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, Flow, Sink, Source}
import akka.util.ByteString
import samples.scalaexchange.utils.{SampleApp, PrintlnSupport, CsvSupport, MakingUpData}
import scala.concurrent.duration._

object PrepareDataApp extends SampleApp with MakingUpData
  with PrintlnSupport
  with CsvSupport {

  val MaxElements = 1000000
  val DataFile = new File("./data.csv")
  DataFile.delete()

  val forever: Source[Unit, NotUsed] = Source.repeat(())

  val names = forever
    .map(_ => shortNameId())
    .map(ByteString(_))

  val values = forever
    .map(_ => int())
    .map(i => ByteString(i.toString))

  val writeComplection =
    names.zip(values)
      .take(MaxElements) // or takeWithin
      .alsoTo(printlnEvery(1.second))
      .via(csvRendering[(ByteString, ByteString)])
      .intersperse(ByteString("\n"))
      .runWith(FileIO.toFile(DataFile))

  writeComplection onSuccess {
    case written =>
      println(s"Written $written bytes, shutting down...")
      system.terminate()
  }

}
