package io.chrisdavenport.github.endpoints.utils

import cats.data.Kleisli
import cats.effect._
import io.circe.Json
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.circe._

trait PaginatedJsonFiles extends JsonFiles with Paginate {

  def baseUri: Uri

  def pageFileName: Int => String

  def extractRequest: PartialFunction[Request[IO], Request[IO]]

  def paginatedEndpoint(numPages: Int): Kleisli[IO, Request[IO], Response[IO]] =
    HttpRoutes.of[IO] {
      extractRequest.andThen { request =>
        val page: Int = getCurrentPage(request)
        (for {
          jsonContent <- pages(numPages).get(page)
          linkHeader <- links(numPages).get(page)
        } yield Ok(jsonContent).map(_.putHeaders(linkHeader)))
          .getOrElse {
            NotFound(s"Page does not exist: $page")
          }
      }
    }.orNotFound

  private def pages(numPages: Int): Map[Int, Json] =
    getPageContents(numPages, pageFileName)

  private def links(numPages: Int): Map[Int, Header.Raw] =
    paginate(baseUri, numPages)

}
