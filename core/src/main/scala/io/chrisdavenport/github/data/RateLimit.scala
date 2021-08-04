package io.chrisdavenport.github.data

import cats.implicits._
import io.circe.Decoder
import io.circe.HCursor

object RateLimit {
  final case class Limits(
    max: Int,
    remaining: Int,
    reset: Int
  )
  object Limits {
    implicit val decoder: Decoder[Limits] = new Decoder[Limits]{
      def apply(c: HCursor): Decoder.Result[Limits] = 
        (
          c.downField("limit").as[Int],
          c.downField("remaining").as[Int],
          c.downField("reset").as[Int]
        ).mapN(Limits.apply)
    }
  }

  final case class RateLimit(
    core: Limits,
    search: Limits,
    graphQL: Limits,
    integrationManifest: Limits
  )
  object RateLimit {
    implicit val decoder: Decoder[RateLimit] = new Decoder[RateLimit]{
      def apply(c: HCursor): Decoder.Result[RateLimit] = {
        val base = c.downField("resources")
        (
          base.downField("core").as[Limits],
          base.downField("search").as[Limits],
          base.downField("graphql").as[Limits],
          base.downField("integration_manifest").as[Limits]
        ).mapN(RateLimit.apply)
      }
    }

  }
}