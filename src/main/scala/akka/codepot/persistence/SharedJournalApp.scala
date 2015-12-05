package akka.codepot.persistence

import akka.actor.{ActorSystem, Props}
import akka.codepot.common.BaseApp
import akka.persistence.journal.leveldb.{SharedLeveldbStore, SharedLeveldbJournal}

object SharedJournalApp extends BaseApp {

  override def run(system: ActorSystem, opts: Map[String, String]): Unit = {
    val sharedJournal = system.actorOf(Props(new SharedLeveldbStore), "shared-journal")
    SharedLeveldbJournal.setStore(sharedJournal, system)
  }

}