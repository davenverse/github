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

/**
 * A Git commit is a snapshot of the hierarchy (Git tree) and the contents of the files (Git blob)
 * in a Git repository. 
 * 
 * These endpoints allow you to read and write commit objects to your Git database on GitHub. 
 * 
 * See the Git Database API for more details.
 **/
object Commits {

  /**
   * Gets a Git commit object - https://developer.github.com/v3/git/commits/#get-a-commit
   * 
   * GET /repos/:owner/:repo/git/commits/:commit_sha
   * 
   **/
  def getCommit[F[_]: Sync](
    owner: String,
    repo: String,
    commitSha: String,
    auth: Option[Auth]
  ): Kleisli[F, Client[F], GitCommit] = 
    RequestConstructor.runRequestWithNoBody[F, GitCommit](
      auth,
      Method.GET,
      uri"repos" / owner / repo / "git" / "commits" / commitSha
    )

  /**
   * Create a commit - https://developer.github.com/v3/git/commits/#create-a-commit
   * 
   * Creates a new Git commit object - https://git-scm.com/book/en/v1/Git-Internals-Git-Objects#Commit-Objects
   * 
   * POST /repos/:owner/:repo/git/commits
   **/
  def createCommit[F[_]: Sync](
    owner: String,
    repo: String,
    createCommit: CreateCommit,
    auth: Auth
  ): Kleisli[F, Client[F], GitCommit] = 
  RequestConstructor.runRequestWithBody[F, CreateCommit, GitCommit](
    auth.some,
    Method.POST,
    uri"repos" / owner / repo / "git" / "commits",
    createCommit
  )




}