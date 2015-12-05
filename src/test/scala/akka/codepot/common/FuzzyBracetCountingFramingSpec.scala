package akka.codepot.common

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import org.scalatest.{BeforeAndAfter, WordSpec, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration._

class FuzzyBracetCountingFramingSpec extends WordSpec with Matchers with BeforeAndAfter {

  implicit val sys = ActorSystem()
  implicit val mat = ActorMaterializer()

  def afterAll(): Unit = {
    sys.terminate()
  }

  def run(in: String): Unit = {
    val res = Source.single(ByteString(in.stripMargin))
          .via(FuzzyJsonBracketCountingFraming.apply(false))
          .grouped(10)
          .runWith(Sink.head)

          info("" + Await.result(res, 3.seconds).map(_.utf8String).last)
  }

  "Fuzzy Framing" must {
    "work" in {
      run("""{
        "element": "name",
        "thing": { "inner": "yes" }
      }""")
    }

    "work with 2" in {
      run("""{
        "element": "name",
        "thing": { "inner": "yes" }
      },{
        "element": "name",
        "thing": { "inner": "yes" }
      }""")
    }
  }

}
