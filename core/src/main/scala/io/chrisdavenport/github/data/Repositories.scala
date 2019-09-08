package io.chrisdavenport.github.data

import cats.implicits._
import cats.effect._
import org.http4s.Uri
import org.http4s.circe._
import java.time.Instant
import io.circe._
import io.circe.syntax._

object Repositories {

  final case class RepoRef(
    owner: Users.SimpleOwner,
    repo: String
  )
  object RepoRef {
    implicit val repoRefDecoder=  new Decoder[RepoRef]{
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
    updatedAt: Option[Instant],
    watchers: Option[Int],
    language: Option[String],
    defaultBranch: Option[String],
    pushedAt: Option[Instant], // None for new Repos
    openIssues: Option[Int],
    hasWiki: Option[Boolean],
    hasIssues: Option[Boolean],
    hasDownloads: Option[Boolean],
    parent: Option[RepoRef],
    source: Option[RepoRef]
  )
  object Repo {
    implicit val repoDecoder =new Decoder[Repo]{
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
          c.downField("updated_at").as[Option[Instant]],
          c.downField("watchers").as[Option[Int]],
          c.downField("language").as[Option[String]],
          c.downField("default_branch").as[Option[String]]
        ).tupled,
          c.downField("pushed_at").as[Option[Instant]],
          c.downField("open_issues").as[Option[Int]],
          c.downField("has_wiki").as[Option[Boolean]],
          c.downField("has_issues").as[Option[Boolean]],
          c.downField("has_downloads").as[Option[Boolean]],
          c.downField("parent").as[Option[RepoRef]],
          c.downField("source").as[Option[RepoRef]]
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
            origin
            ) => Repo(name, id, uri, htmlUri, isPrivate, isArchived, owner, hooksUri,
            stargazersCount, description, sshUri, gitUri, cloneUri, svnUri, forks, homepage,
            canFork, size, updatedAt, watchers, language, defaultBranch, pushedAt,
            openIssues, hasWiki, hasIssues, hasDownloads, parent, origin)
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
    autoInit: Option[Boolean]
  )
  object NewRepo {
    def create(name: String): NewRepo = NewRepo(name, None, None, None, None, None, None)
    
    implicit val newRepoEncoder = new Encoder[NewRepo]{
      def apply(a: NewRepo): Json = Json.obj(
        "name" -> a.name.asJson,
        "description" -> a.description.asJson,
        "homepage" -> a.homepage.asJson,
        "public" -> a.isPublic.asJson,
        "has_issues" -> a.hasIssues.asJson,
        "has_wiki" -> a.hasWiki.asJson,
        "auto_init" -> a.autoInit.asJson
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
    hasDownloads: Option[Boolean]
  )
  object EditRepo {
    
    implicit val editRepoEncoder = new Encoder[EditRepo]{
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
    final case object All extends RepoPublicity
    final case object Owner extends RepoPublicity
    final case object Public extends RepoPublicity
    final case object Private extends RepoPublicity
    final case object Member extends RepoPublicity
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
}