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

object Trees {

  /**
   * Get a tree - https://developer.github.com/v3/git/trees/#get-a-tree
   * 
   * GET /repos/:owner/:repo/git/trees/:tree_sha
   * 
   * If truncated is true in the response then the number of items in the tree
   * array exceeded our maximum limit. If you need to fetch more items, you can
   * clone the repository and iterate over the Git data locally.
   **/
  def getTree[F[_]: Sync](
    owner: String,
    repo: String,
    treeSha: String,
    auth: Option[Auth]
  ): Kleisli[F, Client[F], Tree] = 
    RequestConstructor.runRequestWithNoBody[F, Tree](
      auth,
      Method.GET,
      uri"repos" / owner / repo / "git" / "trees" / treeSha
    )

  /**
   * Same as [[getTree]] but retrieves the full tree, meaning also the files in subdirectories.
   **/
  def getTreeRecursive[F[_]: Sync](
    owner: String,
    repo: String,
    treeSha: String,
    auth: Option[Auth]
  ): Kleisli[F, Client[F], Tree] = 
  RequestConstructor.runRequestWithNoBody[F, Tree](
    auth,
    Method.GET,
    (uri"repos" / owner / repo / "git" / "trees" / treeSha).withQueryParam("recursive", "1")
  )

  /**
   * Create a tree https://developer.github.com/v3/git/trees/#create-a-tree
   * 
   * The tree creation API accepts nested entries. 
   * If you specify both a tree and a nested path modifying that tree, 
   * this endpoint will overwrite the contents of the tree with the new path contents,
   * and create a new tree structure.
   *
   * If you use this endpoint to add, delete, or modify the file contents in a tree,
   * you will need to commit the tree and then update a branch to point to the commit.
   * For more information see "Create a commit" and "Update a reference."
   * 
   * POST /repos/:owner/:repo/git/trees
   * 
   **/
  def createTree[F[_]: Sync](
    owner: String,
    repo: String,
    createTree: CreateTree,
    auth: Auth
  ): Kleisli[F, Client[F], Tree] = 
    RequestConstructor.runRequestWithBody[F, CreateTree, Tree](
      auth.some,
      Method.POST,
      uri"repos" / owner / repo / "git" / "trees",
      createTree
    )


}