package io.chrisdavenport.github.data

import cats.implicits._

import io.circe._
import io.circe.syntax._

import org.http4s._
import org.http4s.circe._

import java.time.ZonedDateTime

object Gists {

  final case class CreateGistFile(
      name: String,
      content: String
  )
  object CreateGistFile {
    implicit val encoder: Encoder[CreateGistFile] = new Encoder[CreateGistFile] {
      def apply(a: CreateGistFile): Json = Json.obj(
        "content" -> a.content.asJson
      )
    }

    private[Gists] def encodeList: List[CreateGistFile] => Json =
      _.foldLeft(JsonObject.empty)((json, file) => json.add(file.name, file.asJson)).asJson
  }

  final case class CreateGist(
      files: List[CreateGistFile],
      description: String,
      public: Boolean
  )
  object CreateGist {
    implicit val encoder: Encoder[CreateGist] = new Encoder[CreateGist] {
      def apply(a: CreateGist): Json = Json.obj(
        "files" -> a.files
          .foldLeft(JsonObject.empty)((json, file) => json.add(file.name, file.asJson))
          .asJson,
        "description" -> a.description.asJson,
        "public" -> a.public.asJson
      )
    }
  }

  final case class GistFile(
      filename: String,
      filetype: String,
      language: Option[String],
      rawUri: Uri,
      size: Int,
      truncated: Option[Boolean],
      content: Option[String]
  )
  object GistFile {
    implicit val decoder: Decoder[GistFile] = new Decoder[GistFile] {
      def apply(c: HCursor): Decoder.Result[GistFile] =
        (
          c.downField("filename").as[String],
          c.downField("type").as[String],
          c.downField("language").as[Option[String]],
          c.downField("raw_url").as[Uri],
          c.downField("size").as[Int],
          c.downField("truncated").as[Option[Boolean]],
          c.downField("content").as[Option[String]]
        ).mapN(GistFile.apply)
    }
  }

  final case class GistFork(
      user: Users.Owner,
      uri: Uri,
      id: String,
      createdAt: ZonedDateTime,
      updatedAt: ZonedDateTime
  )
  object GistFork {
    implicit val decoder: Decoder[GistFork] = new Decoder[GistFork] {
      def apply(c: HCursor): Decoder.Result[GistFork] =
        (
          c.downField("user").as[Users.Owner],
          c.downField("url").as[Uri],
          c.downField("id").as[String],
          c.downField("created_at").as[ZonedDateTime],
          c.downField("updated_at").as[ZonedDateTime]
        ).mapN(GistFork.apply)
    }
  }

  final case class ChangeStatus(
      deletions: Int,
      additions: Int,
      total: Int
  )
  object ChangeStatus {
    implicit val decoder: Decoder[ChangeStatus] = new Decoder[ChangeStatus] {
      def apply(c: HCursor): Decoder.Result[ChangeStatus] =
        (
          c.downField("deletions").as[Int],
          c.downField("additions").as[Int],
          c.downField("total").as[Int]
        ).mapN(ChangeStatus.apply)
    }
  }

  final case class GistCommit(
      uri: Uri,
      version: String,
      user: Users.Owner,
      changeStatus: ChangeStatus,
      committedAt: ZonedDateTime
  )
  object GistCommit {
    implicit val decoder: Decoder[GistCommit] = new Decoder[GistCommit] {
      def apply(c: HCursor): Decoder.Result[GistCommit] =
        (
          c.downField("url").as[Uri],
          c.downField("version").as[String],
          c.downField("user").as[Users.Owner],
          c.downField("change_status").as[ChangeStatus],
          c.downField("committed_at").as[ZonedDateTime]
        ).mapN(GistCommit.apply)
    }
  }

  final case class Gist(
      uri: Uri,
      forksUri: Uri,
      commitsUri: Uri,
      id: String,
      nodeId: String,
      gitPullUri: Uri,
      gitPushUri: Uri,
      htmlUri: Uri,
      files: List[GistFile],
      public: Boolean,
      createdAt: ZonedDateTime,
      updatedAt: ZonedDateTime,
      comments: Int,
      user: Option[Users.Owner],
      commentsUri: Uri,
      owner: Users.Owner,
      truncated: Boolean,
      forks: List[GistFork],
      history: List[GistCommit]
  )
  object Gist {
    implicit val decoder: Decoder[Gist] = new Decoder[Gist] {

      private def toGistFile(cursor: ACursor): Decoder.Result[List[GistFile]] = {
        cursor.keys
          .map(_.toList)
          .getOrElse(Nil)
          .traverse(cursor.downField(_).as[GistFile])
      }

      def apply(c: HCursor): Decoder.Result[Gist] = {
        (
          c.downField("url").as[Uri],
          c.downField("forks_url").as[Uri],
          c.downField("commits_url").as[Uri],
          c.downField("id").as[String],
          c.downField("node_id").as[String],
          c.downField("git_pull_url").as[Uri],
          c.downField("git_push_url").as[Uri],
          c.downField("html_url").as[Uri],
          toGistFile(c.downField("files")),
          c.downField("public").as[Boolean],
          c.downField("created_at").as[ZonedDateTime],
          c.downField("updated_at").as[ZonedDateTime],
          c.downField("comments").as[Int],
          c.downField("user").as[Option[Users.Owner]],
          c.downField("comments_url").as[Uri],
          c.downField("owner").as[Users.Owner],
          c.downField("truncated").as[Boolean],
          c.downField("forks").as[Option[List[GistFork]]].getOrElse(Nil),
          c.downField("history").as[Option[List[GistCommit]]].getOrElse(Nil)
        ).mapN(Gist.apply)
      }
    }
  }

  final case class EditGist(
      description: String,
      files: List[CreateGistFile],
      filesToDelte: List[String]
  )
  object EditGist {
    implicit val encoder: Encoder[EditGist] = new Encoder[EditGist] {
      def apply(a: EditGist): Json = Json.obj(
        "files" -> CreateGistFile.encodeList(a.files),
        "description" -> a.description.asJson
      )
    }
  }
}
