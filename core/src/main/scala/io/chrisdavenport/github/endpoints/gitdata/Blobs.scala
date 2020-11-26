package io.chrisdavenport.github.endpoints.gitdata

import cats.syntax.all._
import cats.data.Kleisli
import cats.effect._
import org.http4s._
import org.http4s.client._
import org.http4s.implicits._

import io.chrisdavenport.github.Auth
import io.chrisdavenport.github.internals.GithubMedia._
import io.chrisdavenport.github.internals.RequestConstructor

import io.chrisdavenport.github.data.GitData._

object Blobs {

  /**
   * Get a blob - https://developer.github.com/v3/git/blobs/#get-a-blob
   * GET /repos/:owner/:repo/git/blobs/:file_sha
   * 
   * The content in the response will always be Base64 encoded.
   *
   * Note: This API supports blobs up to 100 megabytes in size.
   **/
  def getBlob[F[_]: Sync](
    owner: String,
    repo: String,
    sha: String,
    auth: Option[Auth]
  ): Kleisli[F, Client[F], Blob] = 
    RequestConstructor.runRequestWithNoBody[F, Blob](
      auth,
      Method.GET,
      uri"repos" / owner / repo / "git" / "blobs" / sha
    )

  /**
   * Create a blob
   * POST /repos/:owner/:repo/git/blobs
   * Parameters
   * Name 	Type 	Description
   * content 	string 	Required. The new blob's content.
   * encoding 	string 	The encoding used for content. Currently, "utf-8" and "base64" are supported. Default: "utf-8".
   */
  def createBlob[F[_]: Sync](
    owner: String,
    repo: String,
    createBlob: CreateBlob,
    auth: Auth
  ): Kleisli[F, Client[F], NewBlob] = 
    RequestConstructor.runRequestWithBody[F, CreateBlob, NewBlob](
      auth.some,
      Method.POST,
      uri"repos" / owner / repo / "git" / "blobs",
      createBlob
    )


}