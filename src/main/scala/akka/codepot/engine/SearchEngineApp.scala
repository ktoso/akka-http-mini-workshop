package akka.codepot.engine

import akka.actor.ActorSystem
import akka.codepot.common.BaseApp
import akka.codepot.engine.search.SearchMaster

object SearchEngineApp extends BaseApp {
  override protected def run(system: ActorSystem, opts: Map[String, String]): Unit = {
    // just start it, it will join the cluster
    val searchMaster = system.actorOf(SearchMaster.props())
  }
}
