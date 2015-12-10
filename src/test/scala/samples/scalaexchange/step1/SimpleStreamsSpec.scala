package samples.scalaexchange.step1

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.stream.testkit.scaladsl.TestSink
import org.scalatest.{BeforeAndAfterAll, WordSpec, Matchers}

class SimpleStreamsSpec extends WordSpec with Matchers
  with BeforeAndAfterAll {

  implicit val sys = ActorSystem(getClass.getSimpleName)
  implicit val mat = ActorMaterializer()

  "SimpleStream" should {
    "yield result" in {
      val single = Source.single(1)
      val probe = single.runWith(TestSink.probe)

      probe.requestNext(1)
    }
    "yield results" in {
      val single = Source.single(1).concat(Source.single(2))
      val probe = single.runWith(TestSink.probe)

      probe
        .requestNext(1)
        .request(1)
          .expectNext(2)
    }
  }

}
