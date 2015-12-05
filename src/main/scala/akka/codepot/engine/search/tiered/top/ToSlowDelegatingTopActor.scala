package akka.codepot.engine.search.tiered.top

import akka.actor.{Actor, Props}
import akka.codepot.engine.search.tiered.middle.RandomlySlowMiddleActor

import scala.concurrent.duration._


object ToSlowDelegatingTopActor {
  def props(prefix: Char): Props =
    Props(classOf[ToSlowDelegatingTopActor], prefix: Char)
}

class ToSlowDelegatingTopActor(prefix: Char) extends Actor {

  val worker = context.actorOf(RandomlySlowMiddleActor
    .props(context.parent, prefix, slowness = 200.millis, chance = 25 /* % */),
    name = "slowWorker")

  override def receive: Receive = {
    case any => worker forward any
  }
}
