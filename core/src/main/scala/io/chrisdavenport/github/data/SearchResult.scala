package io.chrisdavenport.github.data

import io.circe.Decoder
import cats.implicits._
import io.chrisdavenport.github.data.SearchResult.Order.{Ascending, Descending}

case class SearchResult[A](totalCount: Int, incompleteResults: Boolean, items: List[A])

object SearchResult {

  implicit def searchResultDecoder[A](implicit aDecoder: Decoder[A]): Decoder[SearchResult[A]] =
    cursor => (
      cursor.downField("total_count").as[Int],
      cursor.downField("incomplete_results").as[Boolean],
      cursor.downField("items").as[List[A]]
    ).mapN(SearchResult.apply)

  sealed trait Sort

  object Sort {

    case object Stars extends Sort
    case object Forks extends Sort
    case object HelpWantedIssues extends Sort
    case object Updated extends Sort
    case object BestMatch extends Sort

    def toOptionalParam(sort: Sort): Option[String] =
      sort match {
        case Stars => Some("stars")
        case Forks => Some("forks")
        case HelpWantedIssues => Some("help-wanted-issues")
        case Updated => Some("updated")
        case BestMatch => None
      }

  }

  sealed trait Order

  object Order {

    case object Ascending extends Order
    case object Descending extends Order

    def toOptionalParam(order: Order): Option[String] =
      order match {
        case Ascending => Some("asc")
        case Descending => Some("desc")
      }

  }

}
