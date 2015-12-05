package akka.codepot.common

import akka.stream.io.Framing.FramingException
import akka.stream.scaladsl.Flow
import akka.stream.stage.{TerminationDirective, SyncDirective, Context, PushPullStage}
import akka.util.ByteString

import scala.annotation.tailrec

object FuzzyJsonBracketCountingFraming {
  def apply(allowTruncation: Boolean = false): Flow[ByteString, ByteString, Unit] =
    Flow[ByteString].transform(() => new FuzzyJsonBracketCountingFramingStage(allowTruncation))
}

class FuzzyJsonBracketCountingFramingStage(val allowTruncation: Boolean)
  extends PushPullStage[ByteString, ByteString] {

  val ElementStart = ByteString("{")
  val ElementStartByte: Byte = ElementStart.head

  val ElementStop = ByteString("}")
  val ElementStopByte: Byte = ElementStop.head

  private var insideJsonElement = false
  private var openBracesInside = 0
  private var elementStartsAt = 0
  private var buffer = ByteString.empty
  private var nextPossibleMatch = 0
  private var inside = false
  private var finishing = false

  private var skippedListOpening = false

  override def onPush(chunk: ByteString, ctx: Context[ByteString]): SyncDirective = {
    buffer ++= (
    if (skippedListOpening) chunk
    else chunk.head match {
      case '[' => { skippedListOpening = true; chunk.tail }
      case _   => chunk
    })

    if (!insideJsonElement) findElementStart()
    doParse(ctx)
  }

  override def onPull(ctx: Context[ByteString]): SyncDirective = {
    if (!insideJsonElement) findElementStart()
    doParse(ctx)
  }

  override def onUpstreamFinish(ctx: Context[ByteString]): TerminationDirective = {
    if (buffer.nonEmpty) ctx.absorbTermination()
    else ctx.finish()
  }

  private def tryPull(ctx: Context[ByteString]): SyncDirective = {
    if (ctx.isFinishing) {
      if (allowTruncation) ctx.pushAndFinish(buffer)
      else ctx.fail(new FramingException("Stream finished but there was a truncated final frame in the buffer"))
    } else ctx.pull()
  }

  private def findElementStart(): Unit = {
    val possibleMatchPos = buffer.indexOf(ElementStartByte, from = nextPossibleMatch)
    if (possibleMatchPos == -1) {
      () // do nothing...
    }  else {
      insideJsonElement = true
      nextPossibleMatch = possibleMatchPos
    }
  }

  @tailrec
  private def doParse(ctx: Context[ByteString]): SyncDirective = {
    val startPos = buffer.indexOf(ElementStartByte, from = nextPossibleMatch)
    val stopPos = buffer.indexOf(ElementStopByte, from = nextPossibleMatch)

//    println("----")
//    println("buffer = " + buffer.take(nextPossibleMatch).utf8String)
//    println(">>nextPossibleMatch = " + nextPossibleMatch)
//    println("startPos = " + startPos)
//    println("stopPos = " + stopPos)
//    println("openBracesInside = " + openBracesInside)


    def emit() = {
      // Found a match
      inside = false
      val parsedFrame = buffer.slice(elementStartsAt, nextPossibleMatch).compact
      buffer = buffer.drop(nextPossibleMatch)
      nextPossibleMatch = 0
      elementStartsAt = 0

//      println("parsed = " + parsedFrame)

      insideJsonElement = false // exitting due to push of element
      if (ctx.isFinishing && buffer.isEmpty) {
        ctx.pushAndFinish(parsedFrame)
      } else {
        ctx.push(parsedFrame)
      }
    }

    if (openBracesInside == 0 && inside) {
      emit()
    } else if (startPos < stopPos && startPos != -1) {
      inside = true
      openBracesInside += 1
      if (openBracesInside == 1) elementStartsAt = startPos
      nextPossibleMatch = startPos + 1
      doParse(ctx)
    } else if (stopPos < startPos && stopPos != -1) {
      openBracesInside -= 1
      nextPossibleMatch = stopPos + 1
      doParse(ctx)
    } else if (stopPos != -1) {
      openBracesInside -= 1
      nextPossibleMatch = stopPos + 1
      doParse(ctx)
    } else {
      tryPull(ctx)
    }

//
//    if (startPos != -1 && startPos < stopPos) {
//      openBracesInside += 1
//      nextPossibleMatch = startPos + 1
//      doParse(ctx)
//    } else if (stopPos < startPos) {
//      openBracesInside -= 1
//      nextPossibleMatch = stopPos + 1
//      doParse(ctx)
//    } else if (stopPos > 0 && openBracesInside > 0) {
//      openBracesInside -= 1
//      nextPossibleMatch = stopPos +1
//      if (openBracesInside == 0) emit()
//      else doParse(ctx)
//    } else if (stopPos == -1) {
//      // No matching character, we need to accumulate more bytes into the buffer
//      nextPossibleMatch = buffer.size
//      tryPull(ctx)
//    } else if (openBracesInside == 0) {
//      emit()
//    } else {
//      tryPull(ctx)
//    }
  }

  override def postStop(): Unit = buffer = null
}
