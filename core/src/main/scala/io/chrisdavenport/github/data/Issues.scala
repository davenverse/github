package io.chrisdavenport.github.data

import cats.implicits._
import io.circe._
import io.circe.syntax._
import org.http4s._
import org.http4s.implicits._
import org.http4s.circe._

object Issues {
  sealed trait IssueState
  object IssueState {
    case object Open extends IssueState
    case object Closed extends IssueState

    implicit val encoder = new Encoder[IssueState]{
      def apply(a: IssueState): Json = a match {
        case Open => "open".asJson
        case Closed => "closed".asJson
      }
    }

    implicit val decoder = new Decoder[IssueState]{
      def apply(c: HCursor): Decoder.Result[IssueState] = 
        c.as[String].flatMap{
          case "open" => Open.asRight
          case "closed" => Closed.asRight
          case other => DecodingFailure(s"IssueState - $other", c.history).asLeft
        }
    }
  }

  final case class IssueNumber(toInt: Int) extends AnyVal
  object IssueNumber {
    implicit val decoder = new Decoder[IssueNumber]{
      def apply(c: HCursor): Decoder.Result[IssueNumber] = c.as[Int].map(IssueNumber(_))
    }
    implicit val encoder = new Encoder[IssueNumber]{
      def apply(a: IssueNumber): Json = a.toInt.asJson
    }
  }

  final case class IssueLabel(
    name: String,
    uri: Uri,
    color: String, 
  )
  object IssueLabel {
    implicit val decoder = new Decoder[IssueLabel]{
      def apply(c: HCursor): Decoder.Result[IssueLabel] = 
        (
          c.downField("name").as[String],
          c.downField("url").as[Option[Uri]].map(_.getOrElse(uri"")),
          c.downField("color").as[String]
        ).mapN(IssueLabel.apply)
    }
  }
}