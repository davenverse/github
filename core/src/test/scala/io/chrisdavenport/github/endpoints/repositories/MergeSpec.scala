package io.chrisdavenport.github.endpoints.repositories

import cats.effect._
import cats.effect.testing.specs2.CatsEffect

import io.chrisdavenport.github.data.Repositories._
import io.chrisdavenport.github.OAuth

import org.http4s._
import org.http4s.implicits._
import org.http4s.client._
import org.http4s.circe._
import org.http4s.dsl.io._

import org.specs2.mutable.Specification

class MergeSpec extends Specification with CatsEffect {
  "Repository > Merge endpoints" should {
    val routes = HttpRoutes.of[IO] {
      case POST -> Root / "repos" / _ / _ / "merges" =>
        val json = _root_.io.circe.parser.parse("""
          {
            "sha": "7fd1a60b01f91b314f59955a4e4d4e80d8edf11d",
            "node_id": "MDY6Q29tbWl0N2ZkMWE2MGIwMWY5MWIzMTRmNTk5NTVhNGU0ZDRlODBkOGVkZjExZA==",
            "commit": {
              "author": {
                "name": "The Octocat",
                "date": "2012-03-06T15:06:50-08:00",
                "email": "octocat@nowhere.com"
              },
              "committer": {
                "name": "The Octocat",
                "date": "2012-03-06T15:06:50-08:00",
                "email": "octocat@nowhere.com"
              },
              "message": "Shipped cool_feature!",
              "tree": {
                "sha": "b4eecafa9be2f2006ce1b709d6857b07069b4608",
                "url": "https://api.github.com/repos/octocat/Hello-World/git/trees/b4eecafa9be2f2006ce1b709d6857b07069b4608"
              },
              "url": "https://api.github.com/repos/octocat/Hello-World/git/commits/7fd1a60b01f91b314f59955a4e4d4e80d8edf11d",
              "comment_count": 0,
              "verification": {
                "verified": false,
                "reason": "unsigned",
                "signature": null,
                "payload": null
              }
            },
            "url": "https://api.github.com/repos/octocat/Hello-World/commits/7fd1a60b01f91b314f59955a4e4d4e80d8edf11d",
            "html_url": "https://github.com/octocat/Hello-World/commit/7fd1a60b01f91b314f59955a4e4d4e80d8edf11d",
            "comments_url": "https://api.github.com/repos/octocat/Hello-World/commits/7fd1a60b01f91b314f59955a4e4d4e80d8edf11d/comments",
            "author": {
              "login": "octocat",
              "id": 1,
              "node_id": "MDQ6VXNlcjE=",
              "avatar_url": "https://github.com/images/error/octocat_happy.gif",
              "gravatar_id": "",
              "url": "https://api.github.com/users/octocat",
              "html_url": "https://github.com/octocat",
              "followers_url": "https://api.github.com/users/octocat/followers",
              "following_url": "https://api.github.com/users/octocat/following{/other_user}",
              "gists_url": "https://api.github.com/users/octocat/gists{/gist_id}",
              "starred_url": "https://api.github.com/users/octocat/starred{/owner}{/repo}",
              "subscriptions_url": "https://api.github.com/users/octocat/subscriptions",
              "organizations_url": "https://api.github.com/users/octocat/orgs",
              "repos_url": "https://api.github.com/users/octocat/repos",
              "events_url": "https://api.github.com/users/octocat/events{/privacy}",
              "received_events_url": "https://api.github.com/users/octocat/received_events",
              "type": "User",
              "site_admin": false
            },
            "committer": {
              "login": "octocat",
              "id": 1,
              "node_id": "MDQ6VXNlcjE=",
              "avatar_url": "https://github.com/images/error/octocat_happy.gif",
              "gravatar_id": "",
              "url": "https://api.github.com/users/octocat",
              "html_url": "https://github.com/octocat",
              "followers_url": "https://api.github.com/users/octocat/followers",
              "following_url": "https://api.github.com/users/octocat/following{/other_user}",
              "gists_url": "https://api.github.com/users/octocat/gists{/gist_id}",
              "starred_url": "https://api.github.com/users/octocat/starred{/owner}{/repo}",
              "subscriptions_url": "https://api.github.com/users/octocat/subscriptions",
              "organizations_url": "https://api.github.com/users/octocat/orgs",
              "repos_url": "https://api.github.com/users/octocat/repos",
              "events_url": "https://api.github.com/users/octocat/events{/privacy}",
              "received_events_url": "https://api.github.com/users/octocat/received_events",
              "type": "User",
              "site_admin": false
            },
            "parents": [
              {
                "sha": "553c2077f0edc3d5dc5d17262f6aa498e69d6f8e",
                "url": "https://api.github.com/repos/octocat/Hello-World/commits/553c2077f0edc3d5dc5d17262f6aa498e69d6f8e"
              },
              {
                "sha": "762941318ee16e59dabbacb1b4049eec22f0d303",
                "url": "https://api.github.com/repos/octocat/Hello-World/commits/762941318ee16e59dabbacb1b4049eec22f0d303"
              }
            ]
          }
          """).toOption.get
          Created(json)
      }
      "perform a merge" in {
        Merge.merge[IO]("foo", "bar", MergeRequest("base", "head", "message"), OAuth(""))
          .run(Client.fromHttpApp(routes.orNotFound))
          .attempt
          .map { _ must beRight }
      }
  }
}
