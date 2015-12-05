package akka.codepot.engine.search.tiered.top

import akka.actor.{Actor, ActorLogging, Props, Stash}
import akka.codepot.engine.index.Indexing
import akka.codepot.engine.search.tiered.TieredSearchProtocol
import akka.stream.scaladsl.{ImplicitMaterializer, Sink}
import akka.util.ByteString

import scala.collection.immutable

object SimpleIndexedTopActor {
  def props(prefix: Char) =
    Props(classOf[SimpleIndexedTopActor], prefix)
}

class SimpleIndexedTopActor(prefix: Char) extends Actor with ActorLogging
with Stash
with ImplicitMaterializer
with Indexing {

  import TieredSearchProtocol._

  var inMemIndex: immutable.Set[String] = Set.empty

  override def preStart() =
    doIndex(prefix)

  override def receive: Receive = indexing

  def indexing: Receive = {
    case word: ByteString =>
      inMemIndex += word.utf8String
      logProgress()

    case IndexingCompleted =>
      val size = inMemIndex.size
      log.info("Indexing of {} keywords for prefix {} completed!", size, prefix)

      unstashAll()
      context.parent ! IndexingCompleted
      context become ready

    case _ => stash()
  }

  private def logProgress(): Unit = {
    val size = inMemIndex.size
    if (size % 1000 == 0) {
      log.info("Indexed {} keywords for {} prefix", size, prefix)
    }
  }

  def ready: Receive = {
    case Search(keyword, maxResults) =>
      val result = searchInMem(keyword, maxResults)
      // log.info("Search for {}, resulted in {} entries, on shard {}", keyword, result.size, prefix)
      sender() ! SearchResults(result.toList)
  }

  def searchInMem(keyword: String, maxResults: Int): Set[String] =
    inMemIndex.filter(_ contains keyword)
      .take(maxResults)

  private def doIndex(prefix: Char): Unit = {
    log.info("Indexing for index {}...", prefix)
    wikipediaCachedKeywordsSource
      .filter(_.head.toChar == prefix)
      .runWith(Sink.actorRef(self, onCompleteMessage = IndexingCompleted))
  }

}
