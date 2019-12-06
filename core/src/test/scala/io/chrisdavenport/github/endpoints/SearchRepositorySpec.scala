package io.chrisdavenport.github.endpoints

import org.specs2.mutable.Specification
import cats.effect._
import cats.effect.specs2.CatsEffect
import io.chrisdavenport.github.data.Sort.Stars
import io.chrisdavenport.github.endpoints.utils.PaginatedJsonFiles
import io.chrisdavenport.github.internals.RequestConstructor.GithubError
import org.http4s._
import org.http4s.client._
import org.http4s.dsl.io._
import org.http4s.implicits._

class SearchRepositorySpec extends Specification with CatsEffect with PaginatedJsonFiles {

  override def baseUri: Uri = uri"https://api.github.com/search/repositories?q=scala&sort=stars"

  override def pageFileName: Int => String = page => s"search/repositories/q_scala_sort_stars_page_$page.json"

  override def extractRequest: PartialFunction[Request[IO], Request[IO]] = {
    case request @ GET -> Root / "search" / "repositories" => request
  }

  "Search.repository" should {

    "be able to fetch multiple pages" in {
      Search.repository[IO]("scala", Some(Stars), None, None)
        .run(Client.fromHttpApp(paginatedEndpoint(numPages = 3)))
        .compile
        .toList
        .map { searchResults =>
          searchResults.size mustEqual (3)
          searchResults.head.totalCount mustEqual (82020)
          searchResults.head.incompleteResults mustEqual (false)
          searchResults.head.items.size mustEqual (30)
          searchResults.head.items.head.name mustEqual ("spark")
          searchResults.head.items.last.name mustEqual ("scala_school")
          searchResults(1).items.size mustEqual (30)
          searchResults(1).items.head.name mustEqual ("TensorFlowOnSpark")
          searchResults(1).items.last.name mustEqual ("spark-cassandra-connector")
          searchResults(2).items.size mustEqual (30)
          searchResults(2).items.head.name mustEqual ("sangria")
          searchResults(2).items.last.name mustEqual ("json4s")
        }
    }

    "fail when not being able to fetch a page" in {
      Search.repository[IO]("scala", Some(Stars), None, None)
        .run(Client.fromHttpApp(paginatedEndpoint(numPages = 4)))
        .compile
        .toList
        .attempt
        .map {
          _ must beLeft(new GithubError(NotFound, "Page does not exist: 4"))
        }
    }

  }


}
