package akka.codepot.engine.search

import java.util.Locale

import akka.actor.{Actor, ActorLogging, Props}
import akka.cluster.sharding.ShardRegion.{ExtractEntityId, ExtractShardId}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import akka.codepot.engine.index.Indexing
import akka.codepot.engine.search.tiered.TieredSearchProtocol._
import akka.codepot.engine.search.tiered.top.ShardedSimpleFromFileTopActor
import akka.stream.scaladsl.ImplicitMaterializer
import akka.util.Timeout

import scala.concurrent.duration._

object SearchMaster {
  def props(): Props =
    Props(classOf[SearchMaster])
}

class SearchMaster extends Actor with ImplicitMaterializer with ActorLogging
  with Indexing {
  implicit val timeout = Timeout(10.seconds)

  val extractShardId: ExtractShardId = {
    case x: Search => x.keyword.toLowerCase(Locale.ROOT).take(1)
  }

  val extractEntityId: ExtractEntityId = {
    case x: Search => (x.keyword.toLowerCase(Locale.ROOT).take(2), x)
  }

  val workerShading = ClusterSharding(context.system)
    .start(
      typeName = "search",
      entityProps = ShardedSimpleFromFileTopActor.props(),
      settings = ClusterShardingSettings(context.system).withRememberEntities(true),
      extractShardId = extractShardId,
      extractEntityId = extractEntityId)

  override def receive: Receive = {
    case search: Search =>
      import akka.pattern.{ask, pipe}
      import context.dispatcher

      (workerShading ? search).pipeTo(sender())
  }

}
