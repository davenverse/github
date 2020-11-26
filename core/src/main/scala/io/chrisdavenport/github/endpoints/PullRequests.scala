package io.chrisdavenport.github.endpoints

import cats.syntax.all._
import cats.effect._
import io.chrisdavenport.github.data.Issues
import io.chrisdavenport.github.data.PullRequests._
import org.http4s._
import org.http4s.implicits._

import io.chrisdavenport.github.Auth
import io.chrisdavenport.github.internals.GithubMedia._
import io.chrisdavenport.github.internals.RequestConstructor

object PullRequests {

  def pullRequestsFor[F[_]: Sync](
    owner: String,
    name: String,
    auth: Option[Auth]
  ) = RequestConstructor.runPaginatedRequest[F, List[SimplePullRequest]](
    auth,
    uri"repos" / owner / name / "pulls"
  )

  def pullRequest[F[_]: Sync](
    owner: String,
    name: String,
    issueNumber: Issues.IssueNumber,
    auth: Option[Auth]
  ) = RequestConstructor.runRequestWithNoBody[F, PullRequest](
    auth,
    Method.GET,
    uri"repos" / owner / name / "pulls" / issueNumber.toInt.toString
  )

  def createPullRequest[F[_]: Sync](
    owner: String,
    name: String,
    createPullRequest: CreatePullRequest,
    auth: Auth
  ) = RequestConstructor.runRequestWithBody[F, CreatePullRequest, PullRequest](
    auth.some,
    Method.POST,
    uri"repos" / owner / name / "pulls",
    createPullRequest
  )

  def updatePullRequest[F[_]: Sync](
    owner: String,
    name: String,
    issueNumber: Issues.IssueNumber,
    updatePullRequest: EditPullRequest,
    auth: Auth
  ) = RequestConstructor.runRequestWithBody[F, EditPullRequest, PullRequest](
    auth.some,
    Method.PATCH,
    uri"repos" / owner / name / "pulls" / issueNumber.toInt.toString,
    updatePullRequest
  )



}