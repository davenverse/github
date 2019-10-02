package io.chrisdavenport.github.endpoints

import org.specs2.mutable.Specification

import cats.effect._
import cats.effect.specs2.CatsEffect


import io.circe.literal._
import org.http4s._
import org.http4s.implicits._
import org.http4s.client._
import org.http4s.circe._
import org.http4s.dsl.io._

class SearchSpec extends Specification with CatsEffect {

  "Search" should {

    "return a valid search result" in {
      Search.repository[IO]("anything", None, None, None)
        .run(Client.fromHttpApp(searchRepositories.orNotFound))
        .attempt
        .map{_ must beRight}
    }

  }

  val searchRepositories : HttpRoutes[IO] = HttpRoutes.of {
    case GET -> Root / "search" / "repositories" =>
      Ok(
        json"""
{
  "total_count" : 1,
  "incomplete_results" : false,
  "items" : [
    {
      "id" : 56091117,
      "node_id" : "MDEwOlJlcG9zaXRvcnk1NjA5MTExNw==",
      "name" : "svalidate",
      "full_name" : "timo-schmid/svalidate",
      "private" : false,
      "owner" : {
        "login" : "timo-schmid",
        "id" : 1415715,
        "node_id" : "MDQ6VXNlcjE0MTU3MTU=",
        "avatar_url" : "https://avatars1.githubusercontent.com/u/1415715?v=4",
        "gravatar_id" : "",
        "url" : "https://api.github.com/users/timo-schmid",
        "html_url" : "https://github.com/timo-schmid",
        "followers_url" : "https://api.github.com/users/timo-schmid/followers",
        "following_url" : "https://api.github.com/users/timo-schmid/following{/other_user}",
        "gists_url" : "https://api.github.com/users/timo-schmid/gists{/gist_id}",
        "starred_url" : "https://api.github.com/users/timo-schmid/starred{/owner}{/repo}",
        "subscriptions_url" : "https://api.github.com/users/timo-schmid/subscriptions",
        "organizations_url" : "https://api.github.com/users/timo-schmid/orgs",
        "repos_url" : "https://api.github.com/users/timo-schmid/repos",
        "events_url" : "https://api.github.com/users/timo-schmid/events{/privacy}",
        "received_events_url" : "https://api.github.com/users/timo-schmid/received_events",
        "type" : "User",
        "site_admin" : false
      },
      "html_url" : "https://github.com/timo-schmid/svalidate",
      "description" : "lightweight validation for scala",
      "fork" : false,
      "url" : "https://api.github.com/repos/timo-schmid/svalidate",
      "forks_url" : "https://api.github.com/repos/timo-schmid/svalidate/forks",
      "keys_url" : "https://api.github.com/repos/timo-schmid/svalidate/keys{/key_id}",
      "collaborators_url" : "https://api.github.com/repos/timo-schmid/svalidate/collaborators{/collaborator}",
      "teams_url" : "https://api.github.com/repos/timo-schmid/svalidate/teams",
      "hooks_url" : "https://api.github.com/repos/timo-schmid/svalidate/hooks",
      "issue_events_url" : "https://api.github.com/repos/timo-schmid/svalidate/issues/events{/number}",
      "events_url" : "https://api.github.com/repos/timo-schmid/svalidate/events",
      "assignees_url" : "https://api.github.com/repos/timo-schmid/svalidate/assignees{/user}",
      "branches_url" : "https://api.github.com/repos/timo-schmid/svalidate/branches{/branch}",
      "tags_url" : "https://api.github.com/repos/timo-schmid/svalidate/tags",
      "blobs_url" : "https://api.github.com/repos/timo-schmid/svalidate/git/blobs{/sha}",
      "git_tags_url" : "https://api.github.com/repos/timo-schmid/svalidate/git/tags{/sha}",
      "git_refs_url" : "https://api.github.com/repos/timo-schmid/svalidate/git/refs{/sha}",
      "trees_url" : "https://api.github.com/repos/timo-schmid/svalidate/git/trees{/sha}",
      "statuses_url" : "https://api.github.com/repos/timo-schmid/svalidate/statuses/{sha}",
      "languages_url" : "https://api.github.com/repos/timo-schmid/svalidate/languages",
      "stargazers_url" : "https://api.github.com/repos/timo-schmid/svalidate/stargazers",
      "contributors_url" : "https://api.github.com/repos/timo-schmid/svalidate/contributors",
      "subscribers_url" : "https://api.github.com/repos/timo-schmid/svalidate/subscribers",
      "subscription_url" : "https://api.github.com/repos/timo-schmid/svalidate/subscription",
      "commits_url" : "https://api.github.com/repos/timo-schmid/svalidate/commits{/sha}",
      "git_commits_url" : "https://api.github.com/repos/timo-schmid/svalidate/git/commits{/sha}",
      "comments_url" : "https://api.github.com/repos/timo-schmid/svalidate/comments{/number}",
      "issue_comment_url" : "https://api.github.com/repos/timo-schmid/svalidate/issues/comments{/number}",
      "contents_url" : "https://api.github.com/repos/timo-schmid/svalidate/contents/{+path}",
      "compare_url" : "https://api.github.com/repos/timo-schmid/svalidate/compare/{base}...{head}",
      "merges_url" : "https://api.github.com/repos/timo-schmid/svalidate/merges",
      "archive_url" : "https://api.github.com/repos/timo-schmid/svalidate/{archive_format}{/ref}",
      "downloads_url" : "https://api.github.com/repos/timo-schmid/svalidate/downloads",
      "issues_url" : "https://api.github.com/repos/timo-schmid/svalidate/issues{/number}",
      "pulls_url" : "https://api.github.com/repos/timo-schmid/svalidate/pulls{/number}",
      "milestones_url" : "https://api.github.com/repos/timo-schmid/svalidate/milestones{/number}",
      "notifications_url" : "https://api.github.com/repos/timo-schmid/svalidate/notifications{?since,all,participating}",
      "labels_url" : "https://api.github.com/repos/timo-schmid/svalidate/labels{/name}",
      "releases_url" : "https://api.github.com/repos/timo-schmid/svalidate/releases{/id}",
      "deployments_url" : "https://api.github.com/repos/timo-schmid/svalidate/deployments",
      "created_at" : "2016-04-12T19:17:42Z",
      "updated_at" : "2016-11-03T17:31:23Z",
      "pushed_at" : "2017-03-14T16:46:34Z",
      "git_url" : "git://github.com/timo-schmid/svalidate.git",
      "ssh_url" : "git@github.com:timo-schmid/svalidate.git",
      "clone_url" : "https://github.com/timo-schmid/svalidate.git",
      "svn_url" : "https://github.com/timo-schmid/svalidate",
      "homepage" : "http://svalidate.readthedocs.org/en/latest/",
      "size" : 45,
      "stargazers_count" : 1,
      "watchers_count" : 1,
      "language" : "Scala",
      "has_issues" : true,
      "has_projects" : true,
      "has_downloads" : true,
      "has_wiki" : true,
      "has_pages" : false,
      "forks_count" : 0,
      "mirror_url" : null,
      "archived" : false,
      "disabled" : false,
      "open_issues_count" : 0,
      "license" : null,
      "forks" : 0,
      "open_issues" : 0,
      "watchers" : 1,
      "default_branch" : "master",
      "permissions" : {
        "admin" : true,
        "push" : true,
        "pull" : true
      },
      "score" : 21.219961
    }
  ]
}
      """
      )
  }

}
