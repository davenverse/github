package io.chrisdavenport.github.endpoints

import cats.data._
import cats.effect._
import cats.implicits._

import fs2.Stream

import io.chrisdavenport.github.Auth
import io.chrisdavenport.github.data.Gists._
import io.chrisdavenport.github.internals.GithubMedia._
import io.chrisdavenport.github.internals.RequestConstructor
import io.chrisdavenport.github.internals.QueryParamEncoders._

import org.http4s._
import org.http4s.client.Client
import org.http4s.implicits._

import java.time.ZonedDateTime

object Gists {

  def list[F[_]: Sync](
      username: String,
      since: Option[ZonedDateTime],
      auth: Option[Auth]
  ): Kleisli[Stream[F, *], Client[F], List[Gist]] =
    RequestConstructor.runPaginatedRequest[F, List[Gist]](
      auth,
      (uri"users" / username / "gists")
        .withOptionQueryParam("since", since)
    )

  /**
   * List all public gists sorted by most recently updated to least recently updated.
   * https://developer.github.com/v3/gists/#list-all-public-gists
   */
  def allPublic[F[_]: Sync](
      since: Option[ZonedDateTime],
      auth: Option[Auth]
  ): Kleisli[Stream[F, *], Client[F], List[Gist]] =
    RequestConstructor.runPaginatedRequest[F, List[Gist]](
      auth,
      (uri"gists" / "public")
        .withOptionQueryParam("since", since)
    )

  /**
   * List starred gists by the authenticated user
   * https://developer.github.com/v3/gists/#list-starred-gists
   */
  def starred[F[_]: Sync](
      since: Option[ZonedDateTime],
      auth: Auth
  ): Kleisli[Stream[F, *], Client[F], List[Gist]] =
    RequestConstructor.runPaginatedRequest[F, List[Gist]](
      auth.some,
      (uri"gists" / "starred")
        .withOptionQueryParam("since", since)
    )

  /**
   * Get a single gist by its id
   * https://developer.github.com/v3/gists/#get-a-single-gist
   */
  def get[F[_]: Sync](
      gistId: String,
      auth: Option[Auth]
  ): Kleisli[F, Client[F], Gist] =
    RequestConstructor.runRequestWithNoBody[F, Gist](
      auth,
      Method.GET,
      uri"gists" / gistId
    )

  /**
   * Get a specific revision of a gist
   * https://developer.github.com/v3/gists/#get-a-specific-revision-of-a-gist
   */
  def getRevision[F[_]: Sync](
      gistId: String,
      sha: String,
      auth: Option[Auth]
  ): Kleisli[F, Client[F], Gist] =
    RequestConstructor.runRequestWithNoBody[F, Gist](
      auth,
      Method.GET,
      uri"gists" / gistId / sha
    )

  /**
   * Creates a new gist
   * https://developer.github.com/v3/gists/#create-a-gist
   */
  def create[F[_]: Sync](
      newGist: CreateGist,
      auth: Auth
  ): Kleisli[F, Client[F], Gist] =
    RequestConstructor.runRequestWithBody[F, CreateGist, Gist](
      auth.some,
      Method.POST,
      uri"gists",
      newGist
    )

  /**
   * Edit a gist
   * https://developer.github.com/v3/gists/#edit-a-gist
   */
  // def editGist[F[_]: Sync](
  //   gistId: String,
  //   editGist: EditGist,
  //   auth: Auth
  // ): Kleisli[F, Client[F], Gist] =
  //   RequestConstructor.runRequestWithBody[F, EditGist, Gist](
  //     auth.some,
  //     Method.POST,
  //     uri"gists" / gistId,
  //     editGist
  //   )

  /**
   * List commits history for a gist
   * https://developer.github.com/v3/gists/#list-gist-commits
   */
  def listCommits[F[_]: Sync](
      gistId: String,
      auth: Auth
  ): Kleisli[F, Client[F], List[GistCommit]] =
    RequestConstructor.runRequestWithNoBody[F, List[GistCommit]](
      auth.some,
      Method.GET,
      uri"gists" / gistId / "commits"
    )

  /**
   * Star a gist
   * https://developer.github.com/v3/gists/#star-a-gist
   */
  def star[F[_]: Sync](
      gistId: String,
      auth: Auth
  ): Kleisli[F, Client[F], Unit] =
    RequestConstructor.runRequestWithNoBody[F, Unit](
      auth.some,
      Method.PUT,
      uri"gists" / gistId / "star"
    )

  //def checkStarred

  /**
   * Fork a gist
   * https://developer.github.com/v3/gists/#fork-a-gist
   */
  def fork[F[_]: Sync](
      gistId: String,
      auth: Auth
  ): Kleisli[F, Client[F], Gist] =
    RequestConstructor.runRequestWithNoBody[F, Gist](
      auth.some,
      Method.POST,
      uri"gists" / gistId / "forks"
    )

  /**
   * List all gist's forks
   * https://developer.github.com/v3/gists/#list-gist-forks
   */
  def listForks[F[_]: Sync](
      gistId: String,
      auth: Auth
  ): Kleisli[F, Client[F], List[GistFork]] =
    RequestConstructor.runRequestWithNoBody[F, List[GistFork]](
      auth.some,
      Method.GET,
      uri"gists" / gistId / "forks"
    )

  /**
   * Delete an existing gist
   * https://developer.github.com/v3/gists/#delete-a-gist
   */
  def delete[F[_]: Sync](
      gistId: String,
      auth: Auth
  ): Kleisli[F, Client[F], Unit] =
    RequestConstructor.runRequestWithNoBody[F, Unit](
      auth.some,
      Method.DELETE,
      uri"gists" / gistId
    )
}
