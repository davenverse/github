package io.chrisdavenport.github.endpoints.miscellaneous

import cats.effect._
import cats.effect.testing.specs2.CatsEffect

import io.circe.parser._

import org.http4s._
import org.http4s.client._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.implicits._

import org.specs2.mutable.Specification

class RateLimitSpec extends Specification with CatsEffect {

  "RateLimit" should {

    "return a valid rate-limit response" in {
      RateLimit.rateLimit[IO](None)
        .run(Client.fromHttpApp(rateLimit.orNotFound))
        .attempt
        .map(_ must beRight)
    }

  }

  val rateLimit : HttpRoutes[IO] = HttpRoutes.of {
    case GET -> Root / "rate_limit" =>
      Ok(
        parse("""
        {
          "resources": {
            "core": {
              "limit": 5000,
              "remaining": 4999,
              "reset": 1372700873
            },
            "search": {
              "limit": 30,
              "remaining": 18,
              "reset": 1372697452
            },
            "graphql": {
              "limit": 5000,
              "remaining": 4993,
              "reset": 1372700389
            },
            "integration_manifest": {
              "limit": 5000,
              "remaining": 4999,
              "reset": 1551806725
            }
          },
          "rate": {
            "limit": 5000,
            "remaining": 4999,
            "reset": 1372700873
          }
        }
      """).toOption.get
      )
  }

}
