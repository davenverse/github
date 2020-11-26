package io.chrisdavenport.github.endpoints.gitdata

import cats.syntax.all._
import cats.data.Kleisli
import cats.effect._
import org.http4s._
import org.http4s.client._
import org.http4s.implicits._
import fs2.Stream

import io.chrisdavenport.github.Auth
import io.chrisdavenport.github.internals.GithubMedia._
import io.chrisdavenport.github.internals.RequestConstructor

import io.chrisdavenport.github.data.GitData._

/**
 * A Git reference (git ref) is just a file that contains a Git commit SHA-1 hash.
 * 
 * When referring to a Git commit, you can use the Git reference,
 * which is an easy-to-remember name, rather than the hash. 
 * 
 * The Git reference can be rewritten to point to a new commit.
 * 
 * A branch is just a Git reference that stores the new Git commit hash.
 * 
 * These endpoints allow you to read and write references to your Git database on GitHub.
 * 
 * See the Git Database API for more details.
 **/
object References {
  
  /**
   * Get a single reference - https://developer.github.com/v3/git/refs/#get-a-single-reference
   * Returns a single reference from your Git database. 
   * The `:ref` in the URL must be formatted as `heads/<branch name>` for branches
   * and `tags/<tag name>` for tags.
   * 
   * If the :ref doesn't match an existing ref, a 404 is returned.
   * 
   * GET /repos/:owner/:repo/git/ref/:ref
   **/
  def getReference[F[_]: Sync](
    owner: String,
    repo: String,
    ref: String,
    auth: Option[Auth]
  ): Kleisli[F, Client[F], GitReference] = 
    RequestConstructor.runRequestWithNoBody[F, GitReference](
      auth,
      Method.GET,
      uri"repos" / owner / repo / "git" / "ref" / ref
    )

  /**
   * Returns an array of references from your Git database that match the supplied name. 
   * The :`ref` in the URL must be formatted as `heads/<branch name>` for branches
   * and `tags/<tag name>` for tags. If the :ref doesn't exist in the repository,
   * but existing refs start with `:ref`, they will be returned as an array.
   *
   * When you use this endpoint without providing a `:ref`, 
   * it will return an array of all the references from your Git database,
   * including notes and stashes if they exist on the server.
   * 
   * Anything in the namespace is returned, not just heads and tags. 
   **/
  def matchingReferences[F[_]: Sync](
    owner: String,
    repo: String,
    refLike: String,
    auth: Option[Auth]
  ): Kleisli[Stream[F, ?], Client[F], List[GitReference]] = 
  RequestConstructor.runPaginatedRequest[F, List[GitReference]](
    auth,
    uri"repos" / owner / repo / "git" / "matching-refs" / refLike
  )

  /**
   * Create a reference - https://developer.github.com/v3/git/refs/#create-a-reference
   * 
   * Creates a reference for your repository. 
   * You are unable to create new references for empty repositories,
   * even if the commit SHA-1 hash used exists.
   * Empty repositories are repositories without branches.
   * 
   * POST /repos/:owner/:repo/git/refs
   **/
  def createReference[F[_]: Sync](
    owner: String,
    repo: String,
    createReference: CreateReference,
    auth: Auth
  ): Kleisli[F, Client[F], GitReference] = 
    RequestConstructor.runRequestWithBody[F, CreateReference, GitReference](
      auth.some,
      Method.POST,
      uri"repos" / owner / repo / "git" / "refs" ,
      createReference
    )

  /**
   * Update a reference - https://developer.github.com/v3/git/refs/#update-a-reference
   * 
   * PATCH /repos/:owner/:repo/git/refs/:ref
   **/
  def updateReference[F[_]: Sync](
    owner: String,
    repo: String,
    ref: String,
    updateReference: UpdateReference,
    auth: Auth
  ): Kleisli[F, Client[F], GitReference] = 
    RequestConstructor.runRequestWithBody[F, UpdateReference, GitReference](
      auth.some,
      Method.PATCH,
      uri"repos" / owner / repo / "git" / "refs"/ ref,
      updateReference
    )

  /**
   * Delete a reference - https://developer.github.com/v3/git/refs/#delete-a-reference
   * 
   * DELETE /repos/:owner/:repo/git/refs/:ref
   **/
  def deleteReference[F[_]: Sync](
    owner: String,
    repo: String,
    ref: String,
    auth: Auth
  ): Kleisli[F, Client[F], Unit] = 
    RequestConstructor.runRequestWithNoBody[F, Unit](
      auth.some,
      Method.DELETE,
      uri"repos" / owner / repo / "git" / "refs" / ref
    )

}