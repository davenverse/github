package io.chrisdavenport.github.endpoints

import cats.effect._
import cats.effect.testing.specs2.CatsEffect

import io.chrisdavenport.github.OAuth
import io.chrisdavenport.github.data.Teams._
import io.chrisdavenport.github.endpoints.organizations.Teams.{addOrUpdateTeamRepo}

import org.http4s._
import org.http4s.client._
import org.http4s.dsl.io._
import org.http4s.implicits._

import org.specs2.mutable.Specification

class TeamsSpec extends Specification with CatsEffect {

  "Teams.addOrUpdateRepo" should {
    "return right on the expected json" in {
      val client = Client.fromHttpApp(
        HttpRoutes.of[IO]{
          case PUT -> Root / "teams" / _ / "repos" / _ / _ =>
            NoContent()
        }.orNotFound
      )
      addOrUpdateTeamRepo[IO](3, "foo", "bar", Permission.Admin, OAuth("asdfa"))
        .run(client)
        .attempt
        .map(_ must beRight)
    }
  }
}
