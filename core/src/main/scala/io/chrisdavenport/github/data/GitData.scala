package io.chrisdavenport.github.data

import cats.implicits._
import org.http4s._
import org.http4s.circe._
import io.circe._
import io.circe.syntax._

object GitData {
  sealed trait Encoding
  object Encoding {
    case object Base64 extends Encoding
    case object Utf8 extends Encoding

    implicit val encoder = new Encoder[Encoding]{
      def apply(a: Encoding): Json = a match {
        case Base64 => "base64".asJson
        case Utf8 => "utf-8".asJson
      }
    }
  }

  final case class Blob(
    content: String, // Base64 encoded, Caution Supports up to 100megabytes in size
    uri: Uri,
    sha: String,
    size: Int
  )
  object Blob {
    implicit val decoder = new Decoder[Blob]{
      def apply(c: HCursor): Decoder.Result[Blob] = 
        (
          c.downField("content").as[String],
          c.downField("url").as[Uri],
          c.downField("sha").as[String],
          c.downField("size").as[Int]
        ).mapN(Blob.apply)
    }
  }

  final case class CreateBlob(
    content: String,
    encoding: Encoding
  )

  object CreateBlob {
    implicit val encoder = new Encoder[CreateBlob]{
      def apply(a: CreateBlob): Json = Json.obj(
        "content" -> a.content.asJson,
        "encoding" -> a.encoding.asJson
      )
    }
  }

  final case class NewBlob(
    uri: Uri,
    sha: String
  )
  object NewBlob {
    implicit val decoder = new Decoder[NewBlob]{
      def apply(c: HCursor): Decoder.Result[NewBlob] = 
        (
          c.downField("url").as[Uri],
          c.downField("sha").as[String]
        ).mapN(NewBlob.apply)
    }
  }


  final case class Tree(
    sha: String,
    uri: Uri,
    gitTrees: Vector[GitTree],
    truncated: Boolean
  )

  final case class GitTree(
    `type`: String,
    sha: String,
    uri: Option[Uri],
    size: Option[Int],
    path: String,
    mode: String
  )





}