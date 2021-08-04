package io.chrisdavenport.github.data

import cats.implicits._
import org.http4s.Uri
import org.http4s.circe._
import io.circe._
import io.circe.syntax._

object Content {

  sealed trait ContentItemType
  object ContentItemType{
    case object File extends ContentItemType
    case object Dir extends ContentItemType

    implicit val decoder: Decoder[ContentItemType] = new Decoder[ContentItemType]{
      def apply(c: HCursor): Decoder.Result[ContentItemType] = 
        c.as[String].flatMap{
          case "file" => File.pure[Decoder.Result]
          case "dir" => Dir.pure[Decoder.Result]
          case other => DecodingFailure.apply(s"ContentItemType: got $other", c.history).asLeft
        }
    }
  }


  final case class ContentInfo(
    name: String,
    path: String,
    sha: String,
    uri: Uri,
    gitUri: Uri,
    htmlUri: Uri
  )
  object ContentInfo {
    implicit val decoder: Decoder[ContentInfo] = new Decoder[ContentInfo]{
      def apply(c: HCursor): Decoder.Result[ContentInfo] = 
        (
          c.downField("name").as[String],
          c.downField("path").as[String],
          c.downField("sha").as[String],
          c.downField("url").as[Uri],
          c.downField("git_url").as[Uri],
          c.downField("html_url").as[Uri]
        ).mapN(ContentInfo.apply)
    }
  }

  final case class ContentItem(
    itemType: ContentItemType,
    itemInfo: ContentInfo
  )

  object ContentItem {
    implicit val decoder: Decoder[ContentItem] = new Decoder[ContentItem]{
      def apply(c: HCursor): Decoder.Result[ContentItem] = 
        (
          c.downField("type").as[ContentItemType],
          ContentInfo.decoder(c)
        ).mapN(ContentItem.apply)
    }
  }

  sealed trait Content
  object Content {
    final case class File(data: ContentFileData) extends Content
    final case class Directory(data: List[ContentItem]) extends Content

    implicit val decoder: Decoder[Content] = new Decoder[Content]{
      def apply(c: HCursor): Decoder.Result[Content] = 
        ContentFileData.decoder(c)
          .map(File(_))
          .orElse(
            c.as[List[ContentItem]]
              .map(Directory(_))
          )
    }
  }
  final case class ContentFileData(
    info: ContentInfo,
    encoding: String,
    size: Int,
    content: String
  )
  object ContentFileData {
    implicit val decoder: Decoder[ContentFileData] = new Decoder[ContentFileData]{
      def apply(c: HCursor): Decoder.Result[ContentFileData] = 
        (
          ContentInfo.decoder(c),
          c.downField("encoding").as[String],
          c.downField("size").as[Int],
          c.downField("content").as[String]
        ).mapN(ContentFileData.apply)
    }
  }

  final case class ContentResultInfo(
    info: ContentInfo,
    size: Int
  )
  object ContentResultInfo{
    implicit val decoder: Decoder[ContentResultInfo] = new Decoder[ContentResultInfo]{
      def apply(c: HCursor): Decoder.Result[ContentResultInfo] = 
        (
          ContentInfo.decoder(c),
          c.downField("size").as[Int]
        ).mapN(ContentResultInfo.apply)
    }
  }

  final case class ContentResult(
    content: ContentResultInfo,
    // commit: GitCommit
  )

  object ContentResult {
    implicit val decoder: Decoder[ContentResult] = new Decoder[ContentResult]{
      def apply(c: HCursor): Decoder.Result[ContentResult] = 
        c.downField("content")
          .as[ContentResultInfo]
          .map(ContentResult.apply)

    }
  }

  final case class Author(
    name: String,
    email: String
  )

  object Author {
    implicit val encoder: Encoder[Author] = new Encoder[Author]{
      def apply(a: Author): Json = Json.obj(
        "name" -> a.name.asJson,
        "email" -> a.email.asJson
      )
    }
  }

  final case class CreateFile(
    path: String, 
    message: String,
    content: String,
    branch: Option[String],
    author: Option[Author],
    committer: Option[Author]
  )
  object CreateFile {
    implicit val encoder: Encoder[CreateFile] = new Encoder[CreateFile]{
      def apply(a: CreateFile): Json = Json.obj(
        "path" -> a.path.asJson,
        "message" -> a.message.asJson,
        "content" -> a.content.asJson,
        "branch" -> a.branch.asJson,
        "author" -> a.author.asJson,
        "committer" -> a.committer.asJson
      ).dropNullValues
    }
  }

  final case class UpdateFile(
    path: String,
    message: String,
    content: String,
    sha: String,
    branch: Option[String],
    author: Option[Author],
    committer: Option[Author]
  )


  object UpdateFile {
    implicit val encoder: Encoder[UpdateFile] = new Encoder[UpdateFile]{
      def apply(a: UpdateFile): Json = Json.obj(
        "path" -> a.path.asJson,
        "message" -> a.message.asJson,
        "content" -> a.content.asJson,
        "sha" -> a.sha.asJson,
        "branch" -> a.branch.asJson,
        "author" -> a.author.asJson,
        "committer" -> a.committer.asJson
      ).dropNullValues
    }
  }

  final case class DeleteFile(
    path: String,
    message: String,
    sha: String,
    branch:  Option[String],
    author: Option[Author],
    committer: Option[Author]
  )

  object DeleteFile {
    implicit val encoder: Encoder[DeleteFile] = new Encoder[DeleteFile]{
      def apply(a: DeleteFile): Json = Json.obj(
        "path" -> a.path.asJson,
        "message" -> a.message.asJson,
        "branch" -> a.branch.asJson,
        "author" -> a.author.asJson,
        "committer" -> a.committer.asJson
      ).dropNullValues
    }
  }
}
