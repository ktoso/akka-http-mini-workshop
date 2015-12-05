package akka.codepot.engine.search.tiered.bottom

import akka.actor.{Actor, Props}
import akka.codepot.engine.index.Indexing
import akka.codepot.engine.search.tiered.TieredSearchProtocol
import akka.stream.scaladsl.ImplicitMaterializer

object BottomTierActor {
  def props(prefix: Char) =
    Props(classOf[BottomTierActor])
}

class BottomTierActor extends Actor with Indexing with ImplicitMaterializer {

  override def receive: Receive = {
    case TieredSearchProtocol.Search(key, max) =>
      import context.dispatcher
      import akka.pattern.pipe

      wikipediaCachedKeywordsSource
        .filter(_.utf8String contains key)
        .take(max)
        .runFold(List.empty[String])((acc, bs) => bs.toString() :: acc)
        .pipeTo(sender())
  }
}
