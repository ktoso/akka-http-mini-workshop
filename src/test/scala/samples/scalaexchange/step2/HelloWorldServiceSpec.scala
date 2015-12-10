package samples.scalaexchange.step2

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest._

class HelloWorldServiceSpec extends WordSpec with Matchers
  with ScalatestRouteTest
  with HelloWorldService {

  "HelloWorldSerivice" should {
    "hello world" in {
      Get("/hello") ~> helloRoutes ~> check {
        status should === (StatusCodes.OK)
        responseAs[String] should include ("Hello world")
      }
    }
  }
}