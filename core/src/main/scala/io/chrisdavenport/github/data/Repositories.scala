package io.chrisdavenport.github.data

import cats.implicits._

import io.circe._
import io.circe.syntax._

import org.http4s.Uri
import org.http4s.circe._

import java.time.ZonedDateTime

object Repositories {

  final case class RepoRef(
    owner: Users.SimpleOwner,
    repo: String
  )
  object RepoRef {
    implicit val repoRefDecoder: Decoder[RepoRef] =  new Decoder[RepoRef]{
      def apply(c: HCursor): Decoder.Result[RepoRef] =
        (
          c.downField("owner").as[Users.SimpleOwner],
          c.downField("name").as[String]
        ).mapN(RepoRef.apply)
    }
  }

  final case class Repo(
    name: String,
    id: Int,
    uri: Uri,
    htmlUri: Uri,
    isPrivate: Boolean,
    isArchived: Boolean,
    owner: Users.SimpleOwner,
    hooksUri: Uri,
    stargazersCount: Int,
    description: Option[String],
    sshUri: Option[String],
    gitUri: Option[String],
    cloneUri: Option[Uri],
    svnUri: Option[Uri],
    forks: Option[Int],
    homepage: Option[String],
    canFork: Option[Boolean],
    size: Option[Int],
    updatedAt: Option[ZonedDateTime],
    watchers: Option[Int],
    language: Option[String],
    defaultBranch: Option[String],
    pushedAt: Option[ZonedDateTime], // None for new Repos
    openIssues: Option[Int],
    hasWiki: Option[Boolean],
    hasIssues: Option[Boolean],
    hasDownloads: Option[Boolean],
    parent: Option[RepoRef],
    source: Option[RepoRef],
    visibility: Option[String] // "public", "private" or "internal" (internal is only available if using GitHub Enterprise)
  )
  object Repo {
    implicit val repoDecoder: Decoder[Repo] =new Decoder[Repo]{
      def apply(c: HCursor): Decoder.Result[Repo] =
        ((
          c.downField("name").as[String],
          c.downField("id").as[Int],
          c.downField("url").as[Uri],
          c.downField("html_url").as[Uri],
          c.downField("private").as[Boolean],
          c.downField("archived").as[Option[Boolean]]
            .map(_.getOrElse(false)),
          c.downField("owner").as[Users.SimpleUser],
          c.downField("hooks_url").as[Uri],
          c.downField("stargazers_count").as[Int],
          c.downField("description").as[Option[String]],
          c.downField("ssh_url").as[Option[String]],
          c.downField("git_url").as[Option[String]],
          c.downField("clone_url").as[Option[Uri]],
          c.downField("svn_url").as[Option[Uri]],
          c.downField("forks").as[Option[Int]],
          c.downField("homepage").as[Option[String]],
          c.downField("fork").as[Option[Boolean]],
          c.downField("size").as[Option[Int]],
          c.downField("updated_at").as[Option[ZonedDateTime]],
          c.downField("watchers").as[Option[Int]],
          c.downField("language").as[Option[String]],
          c.downField("default_branch").as[Option[String]]
        ).tupled,
          c.downField("pushed_at").as[Option[ZonedDateTime]],
          c.downField("open_issues").as[Option[Int]],
          c.downField("has_wiki").as[Option[Boolean]],
          c.downField("has_issues").as[Option[Boolean]],
          c.downField("has_downloads").as[Option[Boolean]],
          c.downField("parent").as[Option[RepoRef]],
          c.downField("source").as[Option[RepoRef]],
          c.downField("visibility").as[Option[String]]
        ).mapN{
          case (
            (
            name,
            id,
            uri,
            htmlUri,
            isPrivate,
            isArchived,
            owner,
            hooksUri,
            stargazersCount,
            description,
            sshUri,
            gitUri,
            cloneUri,
            svnUri,
            forks,
            homepage,
            canFork,
            size,
            updatedAt,
            watchers,
            language,
            defaultBranch
            ),
            pushedAt,
            openIssues,
            hasWiki,
            hasIssues,
            hasDownloads,
            parent,
            origin,
            visibility
            ) => Repo(name, id, uri, htmlUri, isPrivate, isArchived, owner, hooksUri,
            stargazersCount, description, sshUri, gitUri, cloneUri, svnUri, forks, homepage,
            canFork, size, updatedAt, watchers, language, defaultBranch, pushedAt,
            openIssues, hasWiki, hasIssues, hasDownloads, parent, origin, visibility)
        }
    }
  }

  final case class NewRepo(
    name: String,
    description: Option[String],
    homepage: Option[String],
    isPublic: Option[Boolean],
    hasIssues: Option[Boolean],
    hasWiki: Option[Boolean],
    autoInit: Option[Boolean],
    visibility: Option[String] // "public", "private" or "internal" (internal is only available if using GitHub Enterprise)
  )
  object NewRepo {
    def create(name: String): NewRepo = NewRepo(name, None, None, None, None, None, None, None)

    implicit val newRepoEncoder: Encoder[NewRepo] = new Encoder[NewRepo]{
      def apply(a: NewRepo): Json = Json.obj(
        "name" -> a.name.asJson,
        "description" -> a.description.asJson,
        "homepage" -> a.homepage.asJson,
        "public" -> a.isPublic.asJson,
        "has_issues" -> a.hasIssues.asJson,
        "has_wiki" -> a.hasWiki.asJson,
        "auto_init" -> a.autoInit.asJson,
        "visibility" -> a.visibility.asJson
      ).dropNullValues
    }
  }

  final case class EditRepo(
    name: Option[String],
    description: Option[String],
    homepage: Option[String],
    isPublic: Option[Boolean],
    hasIssues: Option[Boolean],
    hasWiki: Option[Boolean],
    hasDownloads: Option[Boolean],
    visibility: Option[String] // "public", "private" or "internal" (internal is only available if using GitHub Enterprise)
  )
  object EditRepo {

    implicit val editRepoEncoder: Encoder[EditRepo] = new Encoder[EditRepo]{
      def apply(a: EditRepo): Json = Json.obj(
        "name" -> a.name.asJson,
        "description" -> a.description.asJson,
        "homepage" -> a.homepage.asJson,
        "public" -> a.isPublic.asJson,
        "has_issues" -> a.hasIssues.asJson,
        "has_wiki" -> a.hasWiki.asJson,
        "has_downloads" -> a.hasDownloads.asJson
      ).dropNullValues
    }
  }

  sealed trait RepoPublicity
  object RepoPublicity {
    case object All extends RepoPublicity
    case object Owner extends RepoPublicity
    case object Public extends RepoPublicity
    case object Private extends RepoPublicity
    case object Member extends RepoPublicity
  }

  sealed trait Contributor
  final case class KnownContributor(
    contributionsCount: Int,
    avatarUri: Uri,
    login: String,
    uri: Uri,
    id: Int,
    gravatarId: String
  ) extends Contributor
  final case class AnonymousContributor(
    numberOfContributions: Int,
    recordedName: String
  ) extends Contributor

  final case class MergeRequest(
      base: String,
      head: String,
      commitMessage: String
  )
  object MergeRequest {
    implicit val mergeRequestEncoder: Encoder[MergeRequest] = new Encoder[MergeRequest] {
      def apply(a: MergeRequest): Json = Json.obj(
        "base" -> a.base.asJson,
        "head" -> a.head.asJson,
        "commit_message" -> a.commitMessage.asJson
      )
    }
  }

  final case class MergeCommit(
      author: GitData.GitUser,
      committer: GitData.GitUser,
      message: String,
      tree: GitData.CommitTree,
      uri: Uri,
      commentCount: Int
  )
  object MergeCommit {
    implicit val mergeCommitDecoder: Decoder[MergeCommit] = new Decoder[MergeCommit] {
      def apply(c: HCursor): Decoder.Result[MergeCommit] =
        (
          c.downField("author").as[GitData.GitUser],
          c.downField("committer").as[GitData.GitUser],
          c.downField("message").as[String],
          c.downField("tree").as[GitData.CommitTree],
          c.downField("url").as[Uri],
          c.downField("comment_count").as[Int]
        ).mapN(MergeCommit.apply)
    }
  }

  final case class MergeResult(
      sha: String,
      nodeId: String,
      commit: MergeCommit,
      uri: Uri,
      htmlUri: Uri,
      commentsUri: Uri,
      author: Users.Owner,
      committer: Users.Owner,
      parents: List[GitData.CommitTree]
  )
  object MergeResult {
    implicit val mergeResultDecoder: Decoder[MergeResult] = new Decoder[MergeResult] {
      def apply(c: HCursor): Decoder.Result[MergeResult] =
        (
          c.downField("sha").as[String],
          c.downField("node_id").as[String],
          c.downField("commit").as[MergeCommit],
          c.downField("url").as[Uri],
          c.downField("html_url").as[Uri],
          c.downField("comments_url").as[Uri],
          c.downField("author").as[Users.Owner],
          c.downField("committer").as[Users.Owner],
          c.downField("parents").as[List[GitData.CommitTree]]
        ).mapN(MergeResult.apply)
    }
  }

}
