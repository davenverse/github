package io.chrisdavenport.github.endpoints.gitdata

import cats.implicits._
import cats.data.Kleisli
import cats.effect._
import org.http4s._
import org.http4s.client._
import org.http4s.implicits._

import io.chrisdavenport.github.Auth
import io.chrisdavenport.github.internals.GithubMedia._
import io.chrisdavenport.github.internals.RequestConstructor

import io.chrisdavenport.github.data.GitData._

object Tags {
  /**
   * Get a tag - https://developer.github.com/v3/git/tags/#get-a-tag
   * 
   * GET /repos/:owner/:repo/git/tags/:tag_sha
   * 
   **/
  def getTag[F[_]: Sync](
    owner: String,
    repo: String,
    tagSha: String,
    auth: Option[Auth]
  ): Kleisli[F, Client[F], GitTag] = 
    RequestConstructor.runRequestWithNoBody[F, GitTag](
      auth,
      Method.GET,
      uri"repos" / owner / repo / "git" / "tags" / tagSha
    )

  /**
   * Create a tag object - https://developer.github.com/v3/git/tags/#create-a-tag-object
   * Note that creating a tag object does not create the reference that makes a tag in Git.
   * If you want to create an annotated tag in Git, you have to do this call to create the tag object,
   * and then create the refs/tags/[tag] reference.
   * 
   * If you want to create a lightweight tag, you only have to create the tag reference - 
   * this call would be unnecessary.
   * 
   * POST /repos/:owner/:repo/git/tags
   * 
   **/
  def createTag[F[_]: Sync](
    owner: String,
    repo: String,
    createTag: CreateTag,
    auth: Auth
  ): Kleisli[F, Client[F], GitTag] = 
    RequestConstructor.runRequestWithBody[F, CreateTag, GitTag](
      auth.some,
      Method.POST,
      uri"repos" / owner / repo / "git" / "tags",
      createTag
    )

  /**
   * Like [[createTag]] except that it follows up by also creating the tag
   **/
  def createTagFull[F[_]: Sync](
    owner: String,
    repo: String,
    createTag: CreateTag,
    auth: Auth
  ): Kleisli[F, Client[F], (GitTag, GitReference)] = 
    for {
      tag <- Tags.createTag[F](
        owner,
        repo,
        createTag, 
        auth
      )
      ref <- References.createReference[F](
        owner,
        repo,
        CreateReference(
          "refs/tags/" |+| createTag.tag,
          createTag.objectSha
        ),
        auth
      )
    } yield (tag, ref)

}