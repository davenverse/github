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

}
