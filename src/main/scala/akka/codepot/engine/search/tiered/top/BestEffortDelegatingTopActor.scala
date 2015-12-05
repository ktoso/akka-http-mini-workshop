package akka.codepot.engine.search.tiered.top

import akka.actor.{ActorLogging, ReceiveTimeout, Actor, Props}
import akka.codepot.engine.search.tiered.TieredSearchProtocol.{Results, SearchResults}
import akka.codepot.engine.search.tiered.middle.RandomlySlowMiddleActor

import scala.concurrent.duration._


object BestEffortDelegatingTopActor {
  def props(prefix: Char, sla: FiniteDuration): Props =
    Props(classOf[BestEffortDelegatingTopActor], prefix, sla)
}

class BestEffortDelegatingTopActor(prefix: Char, sla: FiniteDuration) extends Actor {

  val worker = context.actorOf(RandomlySlowMiddleActor
    .props(context.parent, prefix, slowness = 200.millis, chance = 5 /* % */),
    name = "slowWorker")

  override def receive: Receive = {
    case any =>
      val replyTo = sender()
      val slaGuardian = context.actorOf(Props(new Actor with ActorLogging {
        context.setReceiveTimeout(sla)

        override def receive: Actor.Receive = {
          case res: Results =>
            replyTo ! res
            context.stop(self)

          case ReceiveTimeout     =>
            log.info("[{}] Did not meet {} SLA, degrading service quality with empty results.", prefix, sla)
            replyTo ! SearchResults(Nil)
            context.stop(self)
        }
    }))
    worker.tell(any, sender = slaGuardian)
  }
}
