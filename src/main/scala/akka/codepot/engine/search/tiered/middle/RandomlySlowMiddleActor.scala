package akka.codepot.engine.search.tiered.middle

import java.util.concurrent.ThreadLocalRandom

import akka.actor._
import akka.codepot.engine.index.Indexing
import akka.codepot.engine.search.tiered.TieredSearchProtocol
import akka.stream.scaladsl.{ImplicitMaterializer, Sink}
import akka.util.ByteString

import scala.collection.immutable
import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

object  RandomlySlowMiddleActor {
  /**
   * @param chance percent (0 - 100)
   */
  def props(master: ActorRef, prefix: Char, slowness: FiniteDuration, chance: Int = 50) = {
    require(chance >= 0 && chance <= 100, "chance must be: >= 0 && <= 100")
    Props(classOf[RandomlySlowMiddleActor], master, prefix, slowness, chance)
  }
}
class RandomlySlowMiddleActor(master: ActorRef, prefix: Char, slowness: FiniteDuration, chance: Int = 50) extends Actor with ActorLogging
  with Stash
  with ImplicitMaterializer
  with Indexing {

  import TieredSearchProtocol._

  val rand = ThreadLocalRandom.current()
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
      master ! IndexingCompleted
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
      actSlow_!!! {
        val result = searchInMem(keyword, maxResults)
//         log.info("Search for {}, resulted in {} entries, on shard {}", keyword, result.size, prefix)
        sender() ! SearchResults(result.toList)
      }
  }

  private def actSlow_!!!(call: => Unit): Unit = {
    import context.dispatcher
    val n = rand.nextInt(0, 101)
    if (n < chance) {
      akka.pattern.after(slowness, context.system.scheduler)(Future.successful("")).map { _ =>
        call
      }
    } else call
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
