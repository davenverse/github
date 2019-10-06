package io.chrisdavenport.github.data

import io.circe.Decoder
import cats.implicits._

case class SearchResult[A](totalCount: Int, incompleteResults: Boolean, items: List[A])

object SearchResult {

  implicit def searchResultDecoder[A](implicit aDecoder: Decoder[A]): Decoder[SearchResult[A]] =
    cursor => (
      cursor.downField("total_count").as[Int],
      cursor.downField("incomplete_results").as[Boolean],
      cursor.downField("items").as[List[A]]
    ).mapN(SearchResult.apply)

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


  sealed trait Sort

  object Sort {

    case object BestMatch extends Repos.Sort with Users.Sort
    case object Stars extends Repos.Sort
    case object Forks extends Repos.Sort
    case object HelpWantedIssues extends Repos.Sort
    case object Updated extends Repos.Sort
    case object Followers extends Users.Sort
    case object Repositories extends Users.Sort
    case object Joined extends Users.Sort

    def toOptionalParam(sort: Sort): Option[String] =
      sort match {
        case BestMatch => None
        case Stars => Some("stars")
        case Forks => Some("forks")
        case HelpWantedIssues => Some("help-wanted-issues")
        case Updated => Some("updated")
        case Followers => Some("followers")
        case Repositories => Some("repositories")
        case Joined => Some("joined")
      }

  }

  object Repos {

    sealed trait Sort extends SearchResult.Sort

  }

  object Users {

    sealed trait Sort extends SearchResult.Sort

  }

}
