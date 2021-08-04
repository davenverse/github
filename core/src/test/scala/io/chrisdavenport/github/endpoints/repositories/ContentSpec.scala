package io.chrisdavenport.github.endpoints.repositories

import cats.effect._
import cats.effect.testing.specs2.CatsEffect

import io.circe.parser._

import org.http4s._
import org.http4s.client._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.implicits._

import org.specs2.mutable.Specification

class ContentSpec extends Specification with CatsEffect {
  "Content endpoints" should {
    val getContent = HttpRoutes.of[IO]{
      case GET -> Root / "repos" / _ / _ / "contents" / "file" =>
        val json = parse("""
{
  "type": "file",
  "encoding": "base64",
  "size": 5362,
  "name": "README.md",
  "path": "README.md",
  "content": "encoded content ...",
  "sha": "3d21ec53a331a6f037a91c368710b99387d012c1",
  "url": "https://api.github.com/repos/octokit/octokit.rb/contents/README.md",
  "git_url": "https://api.github.com/repos/octokit/octokit.rb/git/blobs/3d21ec53a331a6f037a91c368710b99387d012c1",
  "html_url": "https://github.com/octokit/octokit.rb/blob/master/README.md",
  "download_url": "https://raw.githubusercontent.com/octokit/octokit.rb/master/README.md",
  "_links": {
    "git": "https://api.github.com/repos/octokit/octokit.rb/git/blobs/3d21ec53a331a6f037a91c368710b99387d012c1",
    "self": "https://api.github.com/repos/octokit/octokit.rb/contents/README.md",
    "html": "https://github.com/octokit/octokit.rb/blob/master/README.md"
  }
}
        """).toOption.get
        Ok(json)
      case GET -> Root / "repos" / _ / _ / "contents" / "directory" =>
        val json = parse("""
        [
  {
    "type": "file",
    "size": 625,
    "name": "octokit.rb",
    "path": "lib/octokit.rb",
    "sha": "fff6fe3a23bf1c8ea0692b4a883af99bee26fd3b",
    "url": "https://api.github.com/repos/octokit/octokit.rb/contents/lib/octokit.rb",
    "git_url": "https://api.github.com/repos/octokit/octokit.rb/git/blobs/fff6fe3a23bf1c8ea0692b4a883af99bee26fd3b",
    "html_url": "https://github.com/octokit/octokit.rb/blob/master/lib/octokit.rb",
    "download_url": "https://raw.githubusercontent.com/octokit/octokit.rb/master/lib/octokit.rb",
    "_links": {
      "self": "https://api.github.com/repos/octokit/octokit.rb/contents/lib/octokit.rb",
      "git": "https://api.github.com/repos/octokit/octokit.rb/git/blobs/fff6fe3a23bf1c8ea0692b4a883af99bee26fd3b",
      "html": "https://github.com/octokit/octokit.rb/blob/master/lib/octokit.rb"
    }
  },
  {
    "type": "dir",
    "size": 0,
    "name": "octokit",
    "path": "lib/octokit",
    "sha": "a84d88e7554fc1fa21bcbc4efae3c782a70d2b9d",
    "url": "https://api.github.com/repos/octokit/octokit.rb/contents/lib/octokit",
    "git_url": "https://api.github.com/repos/octokit/octokit.rb/git/trees/a84d88e7554fc1fa21bcbc4efae3c782a70d2b9d",
    "html_url": "https://github.com/octokit/octokit.rb/tree/master/lib/octokit",
    "download_url": null,
    "_links": {
      "self": "https://api.github.com/repos/octokit/octokit.rb/contents/lib/octokit",
      "git": "https://api.github.com/repos/octokit/octokit.rb/git/trees/a84d88e7554fc1fa21bcbc4efae3c782a70d2b9d",
      "html": "https://github.com/octokit/octokit.rb/tree/master/lib/octokit"
    }
  }
]
        """).toOption.get
        Ok(json)
    }
    "get content correct if given a file" in {
      Content.contentsFor[IO]("foo", "bar", "file", None, None)
        .run(Client.fromHttpApp(getContent.orNotFound))
        .attempt
        .map{_ must beRight}
    }
    "get contents correct if given a directory" in {
      Content.contentsFor[IO]("foo", "bar", "directory", None, None)
        .run(Client.fromHttpApp(getContent.orNotFound))
        .attempt
        .map{_ must beRight}
    }
  }
}
