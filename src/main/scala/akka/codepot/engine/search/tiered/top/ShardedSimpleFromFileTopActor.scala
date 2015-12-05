package akka.codepot.engine.search.tiered.top

import java.util.Locale

import akka.actor.{Actor, ActorLogging, Props, Stash}
import akka.codepot.engine.index.Indexing
import akka.codepot.engine.search.tiered.TieredSearchProtocol
import akka.stream.scaladsl.{ImplicitMaterializer, Sink}
import akka.util.ByteString

import scala.collection.immutable

object  ShardedSimpleFromFileTopActor {
  def props() =
    Props(classOf[ShardedSimpleFromFileTopActor])

  final case class PrepareIndex(char: Char)
}
class ShardedSimpleFromFileTopActor extends Actor with ActorLogging
  with Stash
  with ImplicitMaterializer
  with Indexing {

  import TieredSearchProtocol._

  val key = self.path.name

  var inMemIndex: immutable.Set[String] = Set.empty

  override def preStart() = {
    log.info("Started Entity Actor for key [{}], indexing...", key)
    doIndex(key)
  }

  override def receive: Receive = indexing

  def indexing: Receive = {
    case word: String => inMemIndex += word
    case word: ByteString => inMemIndex += word.utf8String

    case IndexingCompleted =>
      log.info("Finished indexing for key [{}] (entries: {})", key, inMemIndex.size)
      unstashAll()
      context become ready

    case _ => stash()
  }

  def ready: Receive = {
    case Search(keyword, maxResults) =>
      val results = inMemIndex
        .filter(_ contains keyword).take(maxResults).toList
      log.info("Search for: [{}], resulted in [{}] results on [{}]", keyword, results.size, key)
      sender() ! SearchResults(results)
  }

  private def doIndex(part: String): Unit =
    wikipediaCachedKeywordsSource
      .filter(_.utf8String.toLowerCase(Locale.ROOT) contains part)
      .runWith(Sink.actorRef(self, onCompleteMessage = IndexingCompleted))

}
