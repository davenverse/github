package io.chrisdavenport.github.endpoints

import cats.effect._
import cats.effect.testing.specs2.CatsEffect

import io.chrisdavenport.github._
import io.chrisdavenport.github.endpoints.Gists
import io.chrisdavenport.github.endpoints.utils.JsonFiles

import org.http4s._
import org.http4s.client._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.circe.CirceEntityEncoder._

import org.specs2.mutable.Specification
import io.chrisdavenport.github.data.Gists.CreateGist

class GistsSpec extends Specification with CatsEffect with JsonFiles {

  private val fileResponse: String => IO[Response[IO]] = fileName =>
    getFileContent(s"gists/$fileName").map(Ok(_)).getOrElse(NotFound())

  val service = HttpRoutes.of[IO] {
    case GET -> Root / "users" / _ / "gists" =>
      fileResponse("list_user_gists.json")
    case GET -> Root / "gists" / "public" =>
      fileResponse("list_all_public_gists.json")
    case GET -> Root / "gists" / "starred" =>
      fileResponse("list_starred_gists.json")
    case GET -> Root / "gists" / _ / "commits" =>
      fileResponse("list_gist_commits.json")
    case GET -> Root / "gists" / _ / "forks" =>
      fileResponse("list_gist_forks.json")
    case GET -> Root / "gists" / _ / "stars" =>
      NoContent()
    case GET -> Root / "gists" / _ / _ =>
      fileResponse("get_a_specific_revision.json")
    case GET -> Root / "gists" / _ =>
      fileResponse("get_a_single_gist.json")
    case POST -> Root / "gists" =>
      fileResponse("create_a_gist.json")
    case PUT -> Root / "gists" / _ / "star" =>
      Ok()
    case POST -> Root / "gists" / _ / "forks" =>
      fileResponse("fork_a_gist.json")
    case DELETE -> Root / "gists" / _ =>
      Ok()
  }

  val client = Client.fromHttpApp(service.orNotFound)

  "Gists" should {
    "list a user's gists" in {
      Gists
        .list[IO]("JohnDoe", None, None)
        .run(client)
        .take(1)
        .compile
        .toList
        .map(_ must not beEmpty)
    }

    "list all public gists" in {
      Gists.allPublic[IO](None, None).run(client).take(1).compile.toList.map(_ must not beEmpty)
    }

    "list starred gists" in {
      Gists.starred[IO](None, OAuth("")).run(client).take(1).compile.toList.map(_ must not beEmpty)
    }

    "get a single gist" in {
      Gists.get[IO]("foo", None).run(client).attempt.map(_ must beRight)
    }

    "get a specific revision" in {
      Gists.getRevision[IO]("foo", "sha", None).run(client).attempt.map(_ must beRight)
    }

    "create a gist" in {
      Gists
        .create[IO](CreateGist(Nil, "description", true), OAuth(""))
        .run(client)
        .attempt
        .map(_ must beRight)
    }

    "edit a gist" in {
      failure
    }.pendingUntilFixed

    "list commits" in {
      Gists.listCommits[IO]("foo", OAuth("")).run(client).attempt.map(_ must beRight)
    }

    "star a gist" in {
      Gists.star[IO]("foo", OAuth("")).run(client).attempt.map(_ must beRight)
    }

    "check starred" in {
      Gists.checkStarred[IO]("foo", OAuth("")).run(client).attempt.map(_ must beRight)
    }

    "fork a gist" in {
      Gists.fork[IO]("foo", OAuth("")).run(client).attempt.map(_ must beRight)
    }

    "list gist forks" in {
      Gists.listForks[IO]("foo", OAuth("")).run(client).attempt.map(_ must beRight)
    }

    "delete a gist" in {
      Gists.delete[IO]("foo", OAuth("")).run(client).attempt.map(_ must beRight)
    }
  }
}
