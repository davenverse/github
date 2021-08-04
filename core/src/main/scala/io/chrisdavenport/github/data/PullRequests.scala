package io.chrisdavenport.github.data

import cats._
import cats.implicits._

import io.circe._
import io.circe.syntax._

import org.http4s._
import org.http4s.circe._

import java.time.ZonedDateTime

object PullRequests {

  final case class PullRequestLinks(
    reviewComments: Uri,
    comments: Uri,
    html: Uri,
    current: Uri
  )
  object PullRequestLinks {
    implicit val decoder: Decoder[PullRequestLinks] = new Decoder[PullRequestLinks]{
      def apply(c: HCursor): Decoder.Result[PullRequestLinks] =
        (
          c.downField("review_comments").downField("href").as[Uri],
          c.downField("comments").downField("href").as[Uri],
          c.downField("html").downField("href").as[Uri],
          c.downField("self").downField("href").as[Uri]
        ).mapN(PullRequestLinks.apply)
    }
  }

  sealed trait MergeableState
  object MergeableState {
    case object Unknown extends MergeableState
    case object Clean extends MergeableState
    case object Dirty extends MergeableState
    case object Unstable extends MergeableState
    case object Blocked extends MergeableState
    case object Behind extends MergeableState

    implicit val decoder: Decoder[MergeableState] = new Decoder[MergeableState]{
      def apply(c: HCursor): Decoder.Result[MergeableState] =
        c.as[String].flatMap{
          case "unknown" => Unknown.asRight
          case "clean" => Clean.asRight
          case "dirty" => Dirty.asRight
          case "unstable" => Unstable.asRight
          case "blocked" => Blocked.asRight
          case "behind" => Behind.asRight
          case other => DecodingFailure(s"MergeableState got: $other", c.history).asLeft
        }
    }
    implicit val encoder:  Encoder[MergeableState] = new Encoder[MergeableState]{
      def apply(a: MergeableState): Json = a match {
        case Unknown =>  "unknown".asJson
        case Clean => "clean".asJson
        case Dirty => "dirty".asJson
        case Unstable => "unstable".asJson
        case Blocked => "blocked".asJson
        case Behind => "behind".asJson
      }
    }
  }

  final case class SimplePullRequest(
    state: Issues.IssueState,
    number: Issues.IssueNumber,
    title: String,
    id: Int,
    body: Option[String],
    user: Users.SimpleUser,
    createdAt: ZonedDateTime,
    updatedAt: ZonedDateTime,
    closedAt: Option[ZonedDateTime],
    mergedAt: Option[ZonedDateTime],
    assignees: List[Users.SimpleUser],
    requestedReviewers: List[Users.SimpleUser],
    uri: Uri,
    issueUri: Uri,
    diffUri: Uri,
    patchUri: Uri,
    htmlUri: Uri,
    requestLinks: PullRequestLinks
  )
  object SimplePullRequest {
    implicit val decoder: Decoder[SimplePullRequest] = new Decoder[SimplePullRequest]{
      def apply(c: HCursor): Decoder.Result[SimplePullRequest] =
        (
          c.downField("state").as[Issues.IssueState],
          c.downField("number").as[Issues.IssueNumber],
          c.downField("title").as[String],
          c.downField("id").as[Int],
          c.downField("body").as[Option[String]],
          c.downField("user").as[Users.SimpleUser],
          c.downField("created_at").as[ZonedDateTime],
          c.downField("updated_at").as[ZonedDateTime],
          c.downField("closed_at").as[Option[ZonedDateTime]],
          c.downField("merged_at").as[Option[ZonedDateTime]],
          c.downField("assignees").as[List[Users.SimpleUser]],
          c.downField("requested_reviewers").as[Option[List[Users.SimpleUser]]]
            .map(_.getOrElse(MonoidK[List].empty)),
          c.downField("url").as[Uri],
          c.downField("issue_url").as[Uri],
          c.downField("diff_url").as[Uri],
          c.downField("patch_url").as[Uri],
          c.downField("html_url").as[Uri],
          c.downField("_links").as[PullRequestLinks]
        ).mapN(SimplePullRequest.apply)
    }
  }

  final case class PullRequestCommit(
    label: String,
    ref: String,
    sha: String,
    user: Users.SimpleUser,
    repo: Option[Repositories.Repo]
  )
  object PullRequestCommit {
    implicit val decoder: Decoder[PullRequestCommit] = new Decoder[PullRequestCommit]{
      def apply(c: HCursor): Decoder.Result[PullRequestCommit] =
        (
          c.downField("label").as[String],
          c.downField("ref").as[String],
          c.downField("sha").as[String],
          c.downField("user").as[Users.SimpleUser],
          c.downField("repo").as[Option[Repositories.Repo]]
        ).mapN(PullRequestCommit.apply)
    }
  }

  final case class PullRequest(
    state: Issues.IssueState,
    number: Issues.IssueNumber,
    title: String,
    id: Int,
    body: Option[String],
    user: Users.SimpleUser,
    createdAt: ZonedDateTime,
    updatedAt: ZonedDateTime,
    closedAt: Option[ZonedDateTime],
    mergedAt: Option[ZonedDateTime],
    assignees: List[Users.SimpleUser],
    requestedReviewers: List[Users.SimpleUser],
    uri: Uri,
    issueUri: Uri,
    diffUri: Uri,
    patchUri: Uri,
    htmlUri: Uri,
    requestLinks: PullRequestLinks,
    head: PullRequestCommit,
    base: PullRequestCommit,
    mergedBy: Option[Users.SimpleUser],
    changedFiles: Int,
    commentCount: Int,
    additionsCount: Int,
    deletionsCount: Int,
    reviewCommentsCount: Int,
    commitCount: Int,
    merged: Boolean,
    mergeable: Option[Boolean],
    mergeableState: MergeableState
  )

  object PullRequest {
    implicit val decoder: Decoder[PullRequest] = new Decoder[PullRequest]{
      def apply(c: HCursor): Decoder.Result[PullRequest] = for {
        state: Issues.IssueState <- c.downField("state").as[Issues.IssueState]
        number: Issues.IssueNumber <- c.downField("number").as[Issues.IssueNumber]
        title: String <- c.downField("title").as[String]
        id: Int <- c.downField("id").as[Int]
        body: Option[String] <- c.downField("body").as[Option[String]]
        user: Users.SimpleUser <- c.downField("user").as[Users.SimpleUser]
        createdAt: ZonedDateTime <- c.downField("created_at").as[ZonedDateTime]
        updatedAt: ZonedDateTime <- c.downField("updated_at").as[ZonedDateTime]
        closedAt: Option[ZonedDateTime] <- c.downField("closed_at").as[Option[ZonedDateTime]]
        mergedAt: Option[ZonedDateTime] <- c.downField("merged_at").as[Option[ZonedDateTime]]
        assignees: List[Users.SimpleUser] <- c.downField("assignees").as[List[Users.SimpleUser]]
        requestedReviewers: List[Users.SimpleUser] <- c.downField("requested_reviewers").as[Option[List[Users.SimpleUser]]]
          .map(_.getOrElse(MonoidK[List].empty))
        uri: Uri <- c.downField("url").as[Uri]
        issueUri: Uri <- c.downField("issue_url").as[Uri]
        diffUri: Uri <- c.downField("diff_url").as[Uri]
        patchUri: Uri <- c.downField("patch_url").as[Uri]
        htmlUri: Uri <- c.downField("html_url").as[Uri]
        requestLinks: PullRequestLinks <- c.downField("_links").as[PullRequestLinks]
        head: PullRequestCommit <- c.downField("head").as[PullRequestCommit]
        base: PullRequestCommit <-  c.downField("base").as[PullRequestCommit]
        mergedBy: Option[Users.SimpleUser] <-  c.downField("merged_by").as[Option[Users.SimpleUser]]
        changedFiles: Int <- c.downField("changed_files").as[Int]
        commentCount: Int <- c.downField("comments").as[Int]
        additionsCount: Int <- c.downField("additions").as[Int]
        deletionsCount: Int <- c.downField("deletions").as[Int]
        reviewCommentsCount: Int <- c.downField("review_comments").as[Int]
        commitCount: Int <- c.downField("commits").as[Int]
        merged: Boolean <- c.downField("merged").as[Boolean]
        mergeable: Option[Boolean] <- c.downField("mergeable").as[Option[Boolean]]
        mergeableState: MergeableState <- c.downField("mergeable_state").as[MergeableState]
      } yield PullRequest(
        state,
        number,
        title,
        id,
        body,
        user,
        createdAt,
        updatedAt,
        closedAt,
        mergedAt,
        assignees,
        requestedReviewers,
        uri,
        issueUri,
        diffUri,
        patchUri,
        htmlUri,
        requestLinks,
        head,
        base,
        mergedBy,
        changedFiles,
        commentCount,
        additionsCount,
        deletionsCount,
        reviewCommentsCount,
        commitCount,
        merged,
        mergeable,
        mergeableState
      )
    }
  }



  final case class EditPullRequest(
    title: Option[String],
    body: Option[String],
    state: Option[Issues.IssueState],
    base: Option[String],
    maintainerCanModify: Option[Boolean]
  )
  object EditPullRequest {
    implicit val encoder: Encoder[EditPullRequest] = new Encoder[EditPullRequest]{
      def apply(a: EditPullRequest): Json = Json.obj(
        "title" -> a.title.asJson,
        "body" -> a.body.asJson,
        "state" -> a.state.asJson,
        "base" -> a.base.asJson,
        "maintainer_can_modify" -> a.maintainerCanModify.asJson
      ).dropNullValues
    }
  }

  sealed trait CreatePullRequest
  object CreatePullRequest {
    final case class PullRequest(
      title: String,
      body: String,
      head: String,
      base: String
    ) extends CreatePullRequest
    final case class Issue(
      issueNumber: Int,
      head: String,
      base: String
    ) extends CreatePullRequest

    implicit val encoder: Encoder[CreatePullRequest] = new Encoder[CreatePullRequest]{
      def apply(a: CreatePullRequest): Json = a match {
        case PullRequest(title, body, head, base) =>
          Json.obj(
            "title" -> title.asJson,
            "body" -> body.asJson,
            "head" -> head.asJson,
            "base" -> base.asJson
          )
        case Issue(issueNumber, head, base) =>
          Json.obj(
            "issue" -> issueNumber.asJson,
            "head" -> head.asJson,
            "base" -> base.asJson
          )
      }
    }
  }

  sealed trait PullRequestEventType
  object PullRequestEventType {
    case object Opened extends PullRequestEventType
    case object Closed extends PullRequestEventType
    case object Synchronized extends PullRequestEventType
    case object Reopened extends PullRequestEventType
    case object Assigned extends PullRequestEventType
    case object Unassigned extends PullRequestEventType
    case object Labeled extends PullRequestEventType
    case object Unlabeled extends PullRequestEventType
    case object ReviewRequested extends PullRequestEventType
    case object ReviewRequestRemoved extends PullRequestEventType
    case object Edited extends PullRequestEventType

    implicit val decoder: Decoder[PullRequestEventType] = new Decoder[PullRequestEventType]{
      def apply(c: HCursor): Decoder.Result[PullRequestEventType] =
        c.as[String].flatMap{
          case "opened" => Opened.asRight
          case "closed" => Closed.asRight
          case "synchronize" => Closed.asRight
          case "reopened" => Reopened.asRight
          case "assigned" => Assigned.asRight
          case "unassigned" => Unassigned.asRight
          case "labeled" => Labeled.asRight
          case "unlabeled" => Unlabeled.asRight
          case "review_requested" => ReviewRequested.asRight
          case "review_request_removed" => ReviewRequestRemoved.asRight
          case "edited" => Edited.asRight
          case other => DecodingFailure(s"PullRequestEventType got: $other", c.history).asLeft
        }
    }
  }

  final case class PullRequestEvent(
    action: PullRequestEventType,
    number: Int,
    pullRequest: PullRequest,
    repository: Repositories.Repo,
    sender: Users.SimpleUser
  )
  object PullRequestEvent {
    implicit val decoder: Decoder[PullRequestEvent] = new Decoder[PullRequestEvent]{
      def apply(c: HCursor): Decoder.Result[PullRequestEvent] =
        (
          c.downField("action").as[PullRequestEventType],
          c.downField("number").as[Int],
          c.downField("pull_request").as[PullRequest],
          c.downField("repository").as[Repositories.Repo],
          c.downField("sender").as[Users.SimpleUser]
        ).mapN(PullRequestEvent.apply)
    }
  }

}
