package io.chrisdavenport.github.endpoints.repositories

import cats.data._
import cats.effect._
import cats.syntax.all._

import io.chrisdavenport.github.Auth
import io.chrisdavenport.github.data.Repositories._
import io.chrisdavenport.github.internals.GithubMedia._
import io.chrisdavenport.github.internals.RequestConstructor

import org.http4s._
import org.http4s.client._
import org.http4s.implicits._

/**
 * The Repo Merging API supports merging branches in a repository. This accomplishes essentially
 * the same thing as merging one branch into another in a local repository and then pushing to GitHub.
 *
 * See the Repository Merging API for more details.
 **/
object Merge {

  /**
   * Perform a merge in a repository - https://developer.github.com/v3/repos/merging/#perform-a-merge
   * POST /repos/:owner/:repo/merges
   **/
  def merge[F[_]: Sync](
    owner: String,
    repo: String,
    mergeRequest: MergeRequest,
    auth: Auth
  ): Kleisli[F, Client[F], MergeResult] =
    RequestConstructor.runRequestWithBody[F, MergeRequest, MergeResult](
      auth.some,
      Method.POST,
      uri"repos" / owner / repo / "merges",
      mergeRequest
    )
}
