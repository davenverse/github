package io.chrisdavenport.github.endpoints.repositories

import cats.implicits._
import cats.data._
import cats.effect._
import io.chrisdavenport.github.data.Repositories.Repo
import org.http4s._
import org.http4s.implicits._
import org.http4s.client.Client

import io.chrisdavenport.github.Auth
import io.chrisdavenport.github.internals.GithubMedia._
import io.chrisdavenport.github.internals.RequestConstructor

object Forks {
  /**
   * Create a fork for the authenticated user.
   **/
  def create[F[_]: Sync](
    owner: String, 
    repo: String,
    auth: Auth
  ): Kleisli[F, Client[F], Repo] = 
    RequestConstructor.runRequestWithNoBody[F, Repo](
      auth.some,
      Method.POST,
      uri"repos" / owner / repo / "forks"
    )
}