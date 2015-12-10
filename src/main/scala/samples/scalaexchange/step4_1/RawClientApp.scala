//package samples.scalaexchange.step4_1
//
//import java.io.File
//
//import akka.http.scaladsl.Http
//import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
//import akka.stream._
//import akka.stream.scaladsl.{Flow, Sink}
//import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, SinkStage}
//import akka.util.ByteString
//import samples.scalaexchange.utils.SampleApp
//
//import scala.concurrent.Future
//import scala.io.StdIn
//
//object RawClientApp extends SampleApp {
//
//  // --- app ---
//
//  val downloadedOut = new File("downloaded.out")
//
//  val request = HttpRequest(uri = "http://127.0.0.1:8080/stream/simple")
//  val NoLimit = Long.MaxValue
//
//  implicit case class UntilChangedAdd[A, M](f: Flow[A, A, M]) {
//    def stage = new GraphStage[FlowShape[A, A]] {
//      val in = Inlet[A]("in")
//      val out = Outlet[A]("out")
//
//      override def createLogic(inheritedAttributes: Attributes) = new GraphStageLogic(shape) {
//        var lastTime: A = _
//        setHandler(in, new InHandler {
//          override def onPush() =
//            grab(in) match {
//              case same if same == lastTime => // don't emit
//              case differentThanLast =>
//                lastTime = differentThanLast
//                push(out, differentThanLast)
//            }
//        })
//
//        setHandler(out, eagerTerminateOutput)
//      }
//
//      override def shape: FlowShape[A, A] = FlowShape(in, out)
//    }
//
//    /**
//     * {{{
//     *   1 1 1 2 3 3 4 4 => 1 2 3 4
//     * }}}
//     */
//    def debounce() =
//      f.via(stage)
//  }
//
//  def progressLine(total: Long): Sink[ByteString, Unit] = {
//    Sink.fromGraph(new SinkStage[ByteString, Unit]("drawPercent") {
//      override def createLogicAndMaterializedValue(inheritedAttributes: Attributes): (GraphStageLogic, Unit) = {
//      new GraphStageLogic(shape) {
//        var last: ByteString = null
//
//        setHandler(in, new InHandler {
//          override def onPush(): Unit =
//          grab(in) match {
//            case it if it == last => // don't emit
//            case it => push(el, out)
//          }
//        })
//        override def preStart(): Unit = pull(in)
//
//      }
//      } -> ()
//    })
//  }
//
//
//  def progressBar(total: Long): Sink[ByteString, Unit] = {
//    Flow[ByteString]
//      .fold(0)(_ + _.length)
//      .via(progressLine(total = total))
//  }
//
//  val response: Future[HttpResponse] =
//    Http().singleRequest(request)
//
//  response map { r =>
//    val bytes = r.entity.withSizeLimit(NoLimit).dataBytes
//
//    bytes
//      .alsoTo(progressBar(total = r.entity.contentLengthOption))
//      .runWith(Sink.file(downloadedOut))
//  }
//
//  StdIn.readLine("Press [RETURN] to quit...")
//  system.terminate()
//}