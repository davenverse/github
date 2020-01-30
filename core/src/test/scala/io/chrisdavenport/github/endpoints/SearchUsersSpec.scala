package io.chrisdavenport.github.endpoints

import cats.effect._
import cats.effect.testing.specs2.CatsEffect

import io.chrisdavenport.github.data.Sort
import io.chrisdavenport.github.endpoints.utils.PaginatedJsonFiles
import io.chrisdavenport.github.internals.RequestConstructor.GithubError

import org.http4s._
import org.http4s.client._
import org.http4s.dsl.io._
import org.http4s.implicits._

import org.specs2.mutable.Specification

class SearchUsersSpec extends Specification with CatsEffect with PaginatedJsonFiles {

  override def baseUri: Uri = uri"https://api.github.com/search/users?q=scala&sort=repositories"

  override def pageFileName: Int => String = page => s"search/users/q_scala_sort_repositories_page_$page.json"

  override def extractRequest: PartialFunction[Request[IO], Request[IO]] = {
    case request @ GET -> Root / "search" / "users" => request
  }

  "Search.users" should {

    "be able to fetch multiple pages" in {
      Search.users[IO]("scala", Some(Sort.Repositories), None, None)
        .run(Client.fromHttpApp(paginatedEndpoint(numPages = 3)))
        .take(3)
        .compile
        .toList
        .map { searchResults =>
          searchResults.size mustEqual (3)
          searchResults.head.totalCount mustEqual (3343)
          searchResults.head.incompleteResults mustEqual (false)
          searchResults.head.items.size mustEqual (30)
          searchResults.head.items.head.login mustEqual ("DefinitelyScala")
          searchResults.head.items.last.login mustEqual ("ashwanthkumar")
          searchResults(1).items.size mustEqual (30)
          searchResults(1).items.head.login mustEqual ("kitlangton")
          searchResults(1).items.last.login mustEqual ("Andrea")
          searchResults(2).items.size mustEqual (30)
          searchResults(2).items.head.login mustEqual ("ryan-williams")
          searchResults(2).items.last.login mustEqual ("heguangwu")
        }
    }

    "fail when not being able to fetch a page" in {
      Search.users[IO]("scala", Some(Sort.Repositories), None, None)
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
