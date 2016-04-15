package samples.scalaexchange.utils

import akka.NotUsed
import akka.http.scaladsl.marshalling.{Marshaller, Marshalling}
import akka.http.scaladsl.model.{ContentTypes, HttpCharsets, MediaTypes}
import akka.stream.Materializer
import akka.stream.scaladsl.Flow
import akka.util.ByteString

trait CsvSupport extends CsvMarshalling {

  // TODO make inference work out better here
  def csvRendering[T]()(implicit render: Marshaller[T, ByteString], mat: Materializer): Flow[T, ByteString, NotUsed] =
    Flow[T]
      .mapAsync(1)(in => render(in)(mat.executionContext))
      .mapConcat(identity)
      .collect {
        case Marshalling.WithFixedContentType(ContentTypes.`text/csv(UTF-8)`, marshal) => marshal()
        case unsupported => throw new UnsupportedOperationException("Unsupported marshalling found, was: " + unsupported)
      }

}

trait CsvMarshalling {
  type BS = ByteString

  // implicit marshallers
//  implicit def t1AsCsvString(implicit mat: Materializer): Marshaller[Tuple1[BS], BS] =
//    Marshaller.withFixedContentType(ContentTypes.`text/csv(UTF-8)`) { productToCsv(_) }
  implicit def t2AsCsvString(implicit mat: Materializer): Marshaller[(BS, BS), BS] =
    Marshaller.withFixedContentType(ContentTypes.`text/csv(UTF-8)`) { productToCsv(_) }
  implicit def t3AsCsvString(implicit mat: Materializer): Marshaller[(BS, BS, BS), BS] =
    Marshaller.withFixedContentType(ContentTypes.`text/csv(UTF-8)`) { productToCsv(_) }
  implicit def t4AsCsvString(implicit mat: Materializer): Marshaller[(BS, BS, BS, BS), BS] =
    Marshaller.withFixedContentType(ContentTypes.`text/csv(UTF-8)`) { productToCsv(_) }
  implicit def t5AsCsvString(implicit mat: Materializer): Marshaller[(BS, BS, BS, BS, BS), BS] =
    Marshaller.withFixedContentType(ContentTypes.`text/csv(UTF-8)`) { productToCsv(_) }
  implicit def t6AsCsvString(implicit mat: Materializer): Marshaller[(BS, BS, BS, BS, BS, BS), BS] =
    Marshaller.withFixedContentType(ContentTypes.`text/csv(UTF-8)`) { productToCsv(_) }
  implicit def t7AsCsvString(implicit mat: Materializer): Marshaller[(BS, BS, BS, BS, BS, BS, BS), BS] =
    Marshaller.withFixedContentType(ContentTypes.`text/csv(UTF-8)`) { productToCsv(_) }
  implicit def t8AsCsvString(implicit mat: Materializer): Marshaller[(BS, BS, BS, BS, BS, BS, BS, BS), BS] =
    Marshaller.withFixedContentType(ContentTypes.`text/csv(UTF-8)`) { productToCsv(_) }
  implicit def t9AsCsvString(implicit mat: Materializer): Marshaller[(BS, BS, BS, BS, BS, BS, BS, BS, BS), BS] =
    Marshaller.withFixedContentType(ContentTypes.`text/csv(UTF-8)`) { productToCsv(_) }
  implicit def t10AsCsvString(implicit mat: Materializer): Marshaller[(BS, BS, BS, BS, BS, BS, BS, BS, BS, BS), BS] =
    Marshaller.withFixedContentType(ContentTypes.`text/csv(UTF-8)`) { productToCsv(_) }
  implicit def t11AsCsvString(implicit mat: Materializer): Marshaller[(BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS), BS] =
    Marshaller.withFixedContentType(ContentTypes.`text/csv(UTF-8)`) { productToCsv(_) }
  implicit def t12AsCsvString(implicit mat: Materializer): Marshaller[(BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS), BS] =
    Marshaller.withFixedContentType(ContentTypes.`text/csv(UTF-8)`) { productToCsv(_) }
  implicit def t13AsCsvString(implicit mat: Materializer): Marshaller[(BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS), BS] =
    Marshaller.withFixedContentType(ContentTypes.`text/csv(UTF-8)`) { productToCsv(_) }
  implicit def t14AsCsvString(implicit mat: Materializer): Marshaller[(BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS), BS] =
    Marshaller.withFixedContentType(ContentTypes.`text/csv(UTF-8)`) { productToCsv(_) }
  implicit def t15AsCsvString(implicit mat: Materializer): Marshaller[(BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS), BS] =
    Marshaller.withFixedContentType(ContentTypes.`text/csv(UTF-8)`) { productToCsv(_) }
  implicit def t16AsCsvString(implicit mat: Materializer): Marshaller[(BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS), BS] =
    Marshaller.withFixedContentType(ContentTypes.`text/csv(UTF-8)`) { productToCsv(_) }
  implicit def t17AsCsvString(implicit mat: Materializer): Marshaller[(BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS), BS] =
    Marshaller.withFixedContentType(ContentTypes.`text/csv(UTF-8)`) { productToCsv(_) }
  implicit def t18AsCsvString(implicit mat: Materializer): Marshaller[(BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS), BS] =
    Marshaller.withFixedContentType(ContentTypes.`text/csv(UTF-8)`) { productToCsv(_) }
  implicit def t19AsCsvString(implicit mat: Materializer): Marshaller[(BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS), BS] =
    Marshaller.withFixedContentType(ContentTypes.`text/csv(UTF-8)`) { productToCsv(_) }
  implicit def t20AsCsvString(implicit mat: Materializer): Marshaller[(BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS), BS] =
    Marshaller.withFixedContentType(ContentTypes.`text/csv(UTF-8)`) { productToCsv(_) }
  implicit def t21AsCsvString(implicit mat: Materializer): Marshaller[(BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS), BS] =
    Marshaller.withFixedContentType(ContentTypes.`text/csv(UTF-8)`) { productToCsv(_) }
  implicit def t22AsCsvString(implicit mat: Materializer): Marshaller[(BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS, BS), BS] =
    Marshaller.withFixedContentType(ContentTypes.`text/csv(UTF-8)`) { productToCsv(_) }

  implicit def lAsCsvString(implicit mat: Materializer): Marshaller[Iterable[BS], BS] =
    Marshaller.withFixedContentType(ContentTypes.`text/csv(UTF-8)`) { l => productToCsv(l.toList) } // TODO cuz' lazy today

  private def intersperse[A](a : List[A], b : List[A]): List[A] = a match {
      case first :: rest => first :: intersperse(b, rest)
      case _             => b
    }

  private def productToCsv(t: Product): BS =
    productToCsv(t.productIterator.toList.asInstanceOf[List[BS]])

  private def productToCsv(els: List[BS]): BS = // TODO lazy today
    intersperse(els, List.fill(els.length - 1)(ByteString(","))).foldLeft(ByteString.empty)(_ ++ _)
}