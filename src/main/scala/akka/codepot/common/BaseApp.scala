package akka.codepot.common

import akka.actor._
import akka.codepot.persistence.SharedJournalSetter

import scala.collection.breakOut
import scala.concurrent.Await
import scala.concurrent.duration._

abstract class BaseApp {

  val Opt = """(\S+)=(\S+)""".r

  def main(args: Array[String]): Unit = {
    val opts = argsToOpts(args.toList)
    applySystemProperties(opts)

    val system = ActorSystem("codepot-system")

    // locate persistence journal:
    system.actorOf(SharedJournalSetter.props)

    system.actorOf(ClusterListener.props)

    run(system, opts)

    io.StdIn.readLine("Hit ENTER to quit ...")
    Await.ready(system.terminate(), 3.second)
  }

  protected def run(system: ActorSystem, opts: Map[String, String]): Unit

  private def argsToOpts(args: Seq[String]): Map[String, String] =
    args.collect { case Opt(key, value) => key -> value }(breakOut)

  private def applySystemProperties(options: Map[String, String]): Unit =
    for ((key, value) <- options if key startsWith "-D")
      System.setProperty(key substring 2, value)

}