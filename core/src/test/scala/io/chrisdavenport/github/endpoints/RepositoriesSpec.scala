package io.chrisdavenport.github
package endpoints

import org.specs2.mutable.Specification

import cats.effect._
import cats.effect.specs2.CatsEffect

import org.http4s._
import org.http4s.implicits._
import org.http4s.client._
import org.http4s.dsl.io._

class RepositoriesSpec extends Specification with CatsEffect {
  "Repositories" should {
    "delete correctly" in {
      val delete = HttpRoutes.of[IO]{
        case DELETE -> Root / "repos" / _ / _ => NoContent()
      }
      val client = Client.fromHttpApp(delete.orNotFound)
      io.chrisdavenport.github.endpoints.Repositories.delete[IO]("foo", "bar", OAuth(""))
        .run(client)
        .attempt
        .map(_ must beRight)
    }
  }

}