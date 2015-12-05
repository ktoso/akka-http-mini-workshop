package akka.codepot.persistence

import akka.actor._
import akka.cluster.ClusterEvent.{MemberUp, InitialStateAsEvents}
import akka.cluster.{Cluster, Member}
import akka.persistence.journal.leveldb.SharedLeveldbJournal


object SharedJournalSetter {

  def props: Props =
    Props(new SharedJournalSetter)
}

/**
 * This actor must be created by all applications that want to use the shared journal,
 * e.g. in order to use persistence or cluster sharding.
 */
class SharedJournalSetter extends Actor with ActorLogging {

  override def preStart(): Unit =
    Cluster(context.system).subscribe(self, InitialStateAsEvents, classOf[MemberUp])

  override def receive: Receive =
    waiting

  override def postStop(): Unit =
    Cluster(context.system).unsubscribe(self)

  def waiting: Receive = {
    case MemberUp(member) if member hasRole "shared-journal" => onSharedJournalMemberUp(member)
  }

  def becomeIdentifying(): Unit = {
    import scala.concurrent.duration._
    context.setReceiveTimeout(10 seconds)
    context become identifying
  }

  def identifying: Receive = {
    case ActorIdentity(_, Some(sharedJournal)) =>
      SharedLeveldbJournal.setStore(sharedJournal, context.system)
      log.info("Succssfully set shared journal {}", sharedJournal)
      context.stop(self)
    case ActorIdentity(_, None) =>
      log.error("Can't identify shared journal!")
      context.stop(self)
    case ReceiveTimeout =>
      log.error("Timeout identifying shared journal!")
      context.stop(self)
  }

  def onSharedJournalMemberUp(member: Member): Unit = {
    val sharedJournal = context actorSelection (RootActorPath(member.address) / "user" / "shared-journal")
    sharedJournal ! Identify(None)
    becomeIdentifying()
  }
}
