package io.chrisdavenport.github.endpoints.repositories

import cats.implicits._
import cats.data._
import cats.effect._
import io.chrisdavenport.github.data.Repositories._
import io.chrisdavenport.github.data.Content._
import org.http4s._
import org.http4s.implicits._
import org.http4s.client.Client

import io.chrisdavenport.github.Auth
import io.chrisdavenport.github.internals.GithubMedia._
import io.chrisdavenport.github.internals.RequestConstructor

object Content {
  def contentsFor[F[_]: Sync](
    owner: String,
    repo: String,
    path: String,
    ref: Option[String],
    auth: Option[Auth]
  ): Kleisli[F, Client[F], Content] =
    RequestConstructor.runRequestWithNoBody[F, Content](
      auth,
      Method.GET,
      (uri"/repo" / owner / repo / "contents" / path)
        .withOptionQueryParam("ref", ref)
    )
  
  
}