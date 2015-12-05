package akka.codepot.engine.search.tiered.top

import akka.actor.{Actor, Props}
import akka.codepot.engine.search.tiered.middle.RandomlySlowMiddleActor
import akka.routing.TailChoppingPool

object TailChoppingDelegatingTopActor {
  def props(prefix: Char) = Props(classOf[TailChoppingDelegatingTopActor], prefix: Char)
}

class TailChoppingDelegatingTopActor(prefix: Char) extends Actor {
  import scala.concurrent.duration._

  val worker = context.actorOf(
    TailChoppingPool(10, within = 200.millis, interval = 10.millis)
    .props(RandomlySlowMiddleActor.props(context.parent, prefix, slowness = 200.millis, chance = 25 /* % */)),
    "tailChopper")

  override def receive: Receive = {
    case any => worker forward any
  }
}
