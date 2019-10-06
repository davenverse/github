package io.chrisdavenport.github.endpoints.utils

import io.circe.Json
import io.circe.parser.parse
import org.http4s.Request
import scala.io.Source

/**
 * Can be mixed-in to load json contents from files
 */
trait JsonFiles {

  /**
   * Returns the current page name from the request.
   * @param request The request
   * @return The current page. Defaults to 1 if not set.
   */
  def getCurrentPage[F[_]](request: Request[F]): Int =
    request
      .params
      .get("page")
      .flatMap(_.toIntOption)
      .getOrElse(1)

  /**
   * Returns the contens for multiple files as a map
   * @param numPages The amount of pages
   * @param pageFileName Produces the file name for each page
   * @return The contents for all pages in a map
   */
  def getPageContents(numPages: Int, pageFileName: Int => String): Map[Int, Json] =
    (1 to numPages).flatMap { page =>
      getFileContent(pageFileName(page)).map(page -> _)
    }.toMap

  /**
   * Returns the content of a file as Json in the test resources folder, or None
   * @param fileName The file name
   * @return The parsed file
   */
  def getFileContent(fileName: String): Option[Json] =
    for {
      inputStream <- Option(getClass.getClassLoader.getResourceAsStream(fileName))
      jsonString = Source.fromInputStream(inputStream).getLines().mkString("\n")
      json <- parse(jsonString).toOption
    } yield json

}
