package io.chrisdavenport.github.endpoints.utils

import org.http4s.{Header, Uri}
import cats.syntax.all._
import org.typelevel.ci._

/**
 * Can be mixed-in to simulate pagination in tests
 */
trait Paginate {

  /**
   * Creates the headers for each pages
   * @param uri The base uri
   * @param numPages The number of pages
   * @return A map, containing the "Link" header for each page point to prev, next, first and last page
   */
  def paginate(uri: Uri, numPages: Int): Map[Int, Header.Raw] = {
    (1 to numPages).map { currentPage =>
      currentPage -> Header.Raw(
        CIString("Link"),
        List(
          prevPage(uri, currentPage),
          nextPage(uri, numPages, currentPage),
          lastPage(uri, numPages, currentPage),
          firstPage(uri, currentPage)
        )
          .flatten
          .map { case (uri, rel) =>
            s""" <${uri.toString}>; rel="$rel""""
          }.mkString(",")
      )
    }.toMap
  }

  private def firstPage(uri: Uri, page: Int): Option[(Uri, String)] =
    Option(page)
      .filterNot(_ == 1)
      .as((uri.withQueryParam[String, String]("page", "1"), "first"))

  private def prevPage(uri: Uri, page: Int): Option[(Uri, String)] =
    Option(page)
      .filterNot(_ == 1)
      .map { p =>
        (uri.withQueryParam[String, String]("page", (p - 1).toString), "prev")
      }

  private def nextPage(uri: Uri, numPages: Int, page: Int): Option[(Uri, String)] =
    Option(page)
      .filterNot(_ == numPages)
      .map { p =>
        (uri.withQueryParam[String, String]("page", (p + 1).toString), "next")
      }

  private def lastPage(uri: Uri, numPages: Int, page: Int): Option[(Uri, String)] =
    Option(page)
      .filterNot(_ == numPages)
      .as((uri.withQueryParam[String, String]("page", numPages.toString), "last"))

}
