package io.chrisdavenport.github.endpoints.repositories

import cats.implicits._
import cats.data._
import cats.effect._

import fs2.Stream

import io.chrisdavenport.github.Auth
import io.chrisdavenport.github.data.Repositories.Repo
import io.chrisdavenport.github.data.Sort
import io.chrisdavenport.github.internals.GithubMedia._
import io.chrisdavenport.github.internals.RequestConstructor

import org.http4s._
import org.http4s.implicits._
import org.http4s.client.Client

object Forks {
  /**
   * Create a fork for the authenticated user.
   **/
  def create[F[_]: Concurrent](
    owner: String, 
    repo: String,
    auth: Auth
  ): Kleisli[F, Client[F], Repo] = 
    RequestConstructor.runRequestWithNoBody[F, Repo](
      auth.some,
      Method.POST,
      uri"repos" / owner / repo / "forks"
    )

  /**
   * List all forks
   **/
  def list[F[_]: Concurrent](
    owner: String,
    repo: String,
    sort: Option[Sort.Fork],
    auth: Auth
  ): Kleisli[Stream[F, *], Client[F], List[Repo]] =
  RequestConstructor.runPaginatedRequest[F, List[Repo]](
    auth.some,
    (uri"repos" / owner / repo / "forks")
      .withOptionQueryParam("sort", sort.flatMap(Sort.toOptionalParam))
  )
}