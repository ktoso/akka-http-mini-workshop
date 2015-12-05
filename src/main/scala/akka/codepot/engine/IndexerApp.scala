package akka.codepot.engine

import java.io.File
import java.util.concurrent.atomic.AtomicInteger

import akka.actor.ActorSystem
import akka.codepot.engine.index.Indexing
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import com.typesafe.config.ConfigFactory

object IndexerApp extends App with Indexing {
  implicit val system = ActorSystem("indexing-app", ConfigFactory.parseResources("nothing.conf"))
  implicit val mat = ActorMaterializer()
  import akka.stream.io.Implicits._
  import system.dispatcher

  val input = if (args.length == 1) args.head else "20150817.json.tar.gz"
//  val input = if (args.length == 1) args.head else "20150817.json"
  println(s"Indexing [$input]...")

    val c = new AtomicInteger()

  wikipediaParsedKeywordsSource
    .runWith(Sink.synchronousFile(new File("keywords.csv")))
    .onComplete(_ => system.terminate())


  io.StdIn.readLine("Press ENTER to quit...\n")
  system.terminate()
}
