package io.chrisdavenport.github.endpoints

import cats.data._
import cats.effect._
import fs2.Stream
import io.chrisdavenport.github.data.Repositories._
import org.http4s.implicits._
import org.http4s.client.Client
import io.chrisdavenport.github.Auth
import io.chrisdavenport.github.data.{Order, SearchResult, Sort}
import io.chrisdavenport.github.data.Users.User
import io.chrisdavenport.github.internals.GithubMedia._
import io.chrisdavenport.github.internals.RequestConstructor

object Search {

  /**
   * Repository Search Endpoint
   * https://developer.github.com/v3/search/#search-repositories
   * @param q The search query string
   * @param sort The sorting method
   * @param order  The sorting order
   * @param auth The authentication mechanism
   * @tparam F The effect type
   */
  def repository[F[_]: Concurrent](
    q: String,
    sort: Option[Sort.Repository],
    order: Option[Order],
    auth: Option[Auth]
  ): Kleisli[Stream[F, *], Client[F], SearchResult[Repo]] =
    RequestConstructor.runPaginatedRequest[F, SearchResult[Repo]](
      auth,
      (uri"search" / "repositories")
        .withQueryParam("q", q)
        .withOptionQueryParam("sort", sort.flatMap(Sort.toOptionalParam))
        .withOptionQueryParam("order", order.flatMap(Order.toOptionalParam))
    )

  /**
   * User Search Endpoint
   * https://developer.github.com/v3/search/#search-users
   * @param q The search query string
   * @param sort The sorting method
   * @param order  The sorting order
   * @param auth The authentication mechanism
   * @tparam F The effect type
   */
  def users[F[_]: Concurrent](
    q: String,
    sort: Option[Sort.User],
    order: Option[Order],
    auth: Option[Auth]
  ): Kleisli[Stream[F, *], Client[F], SearchResult[User]] =
    RequestConstructor.runPaginatedRequest[F, SearchResult[User]](
      auth,
      (uri"search" / "users")
        .withQueryParam("q", q)
        .withOptionQueryParam("sort", sort.flatMap(Sort.toOptionalParam))
        .withOptionQueryParam("order", order.flatMap(Order.toOptionalParam))
    )

}
