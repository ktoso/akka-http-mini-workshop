package akka.codepot.engine.search.tiered

import akka.stream.scaladsl.Source

import scala.collection.immutable

object TieredSearchProtocol {
  final case class Search(keyword: String, maxResults: Int)

  abstract class Results {
    def strict: immutable.Seq[String]
    def source: Source[String, Unit] = Source(strict)
  }
  final case class SearchResults(strict: immutable.Seq[String]) extends Results
  final case class SeachFailed(ex: Exception) extends Results {
    override def strict: immutable.Seq[String] = throw ex
    override def source: Source[String, Unit] = Source.failed(ex)
  }

  // indexing protocol
  final case class PrepareIndex(prefix: Char)
  final case object IndexingCompleted

}
