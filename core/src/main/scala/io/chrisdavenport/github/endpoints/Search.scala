package io.chrisdavenport.github.endpoints

import cats.data._
import cats.effect._
import io.chrisdavenport.github.data.Repositories._
import org.http4s._
import org.http4s.implicits._
import org.http4s.client.Client
import io.chrisdavenport.github.Auth
import io.chrisdavenport.github.data.SearchResult
import io.chrisdavenport.github.data.SearchResult.{Order, Sort}
import io.chrisdavenport.github.internals.GithubMedia._
import io.chrisdavenport.github.internals.RequestConstructor

object Search {

  def repository[F[_]: Sync](
    q: String,
    sort: Option[Sort],
    order: Option[Order],
    auth: Option[Auth]
  ): Kleisli[F, Client[F], SearchResult[Repo]] =
    RequestConstructor.runRequestWithNoBody[F, SearchResult[Repo]](
      auth,
      Method.GET,
      (uri"search" / "repositories")
        .withQueryParam("q", q)
        .withOptionQueryParam("sort", sort.flatMap(Sort.toOptionalParam))
        .withOptionQueryParam("order", order.flatMap(Order.toOptionalParam))
    )

}
