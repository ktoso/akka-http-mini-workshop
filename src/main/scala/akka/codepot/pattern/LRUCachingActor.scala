package akka.codepot.pattern

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Cancellable
import akka.actor.Props
import akka.actor._
import akka.util.Timeout
import scala.concurrent.duration._

object LRUCachingActor {
  def spawnLRUCachingOf(target: ActorRef, keepDuration: FiniteDuration, timeout: FiniteDuration)(implicit sys: ActorSystem): ActorRef =
    sys.actorOf(Props(new LRUCachingActor(target, keepDuration, timeout)))

  private case object CacheCleanupTick
}

class LRUCachingActor(target: ActorRef, keepDuration: FiniteDuration, _timeout: FiniteDuration) extends Actor {
  import LRUCachingActor._
  import akka.pattern.ask
  import akka.pattern.pipe
  import context.dispatcher
  implicit val timeout = Timeout(_timeout)

  case class ValueHolder(it: Any, deadline: Deadline = Deadline.now + keepDuration)
  var cache: Map[Any, ValueHolder] = Map.empty

  override def preStart() =
    scheduleCleanupTick()

  override def receive: Receive =
    passThroughCache orElse cleanupTick

  def passThroughCache: Receive = {
      case msg if cache.contains(msg) =>
        askDownstream(msg)

      case msg =>
        val holder = cache(msg)

        if (holder.deadline.isOverdue()) {
          removeFromCache(msg)
          askDownstream(msg)
        } else {
          markHot(holder)
          sender() ! holder.it
        }
    }

  def cleanupTick: Receive = {
    case CacheCleanupTick =>
      for {
        k <- cache.keys
        if cache(k).deadline.isOverdue()
      } cache -= k

      scheduleCleanupTick()
  }

  private def scheduleCleanupTick(): Cancellable =
    context.system.scheduler.scheduleOnce(keepDuration * 2, self, CacheCleanupTick)

  def removeFromCache(msg: Any): Unit =
    cache -= msg

  def askDownstream(msg: Any): Unit =
    (target ? msg).map(storeInCache(msg, _)).pipeTo(sender())

  def markHot(key: Any): Unit =
    storeInCache(key, cache(key).it)

  def storeInCache(key: Any, it: Any): Any = {
    cache = cache.updated(key, ValueHolder(it))
    it
  }
}
