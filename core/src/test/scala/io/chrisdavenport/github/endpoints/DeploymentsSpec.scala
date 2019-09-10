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

import io.chrisdavenport.github.OAuth
import io.chrisdavenport.github.data
import io.chrisdavenport.github.internals.GithubMediaType


class DeploymentsSpec extends Specification with CatsEffect {

  "Deployments" should {

    "create a deployment" in {

      val createDeploymentRoute = HttpRoutes.of[IO] {
        case POST -> Root / "repos" / _ / _ / "deployments"  => Ok(json"""
        {
          "url": "https://api.github.com/repos/octocat/example/deployments/1",
          "id": 1,
          "node_id": "MDEwOkRlcGxveW1lbnQx",
          "sha": "a84d88e7554fc1fa21bcbc4efae3c782a70d2b9d",
          "ref": "topic-branch",
          "task": "deploy",
          "payload": {
            "deploy": "migrate"
          },
          "original_environment": "staging",
          "environment": "production",
          "description": "Deploy request from hubot",
          "creator": {
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
          "created_at": "2012-07-20T01:19:13Z",
          "updated_at": "2012-07-20T01:19:13Z",
          "statuses_url": "https://api.github.com/repos/octocat/example/deployments/1/statuses",
          "repository_url": "https://api.github.com/repos/octocat/example",
          "transient_environment": false,
          "production_environment": true
        }
        """)
      }
      Deployments.createDeployment[IO](
        "anything",
        "anything",
        data.Deployments.NewDeployment.create("topic-branch"),
        OAuth("anything"),
        GithubMediaType.`application/vnd.github.ant-man-preview+json`
      )
      .run(Client.fromHttpApp(createDeploymentRoute.orNotFound))
      .attempt
      .map{_ must beRight}
    }

    "get a deployment" in {
      val getDeploymentRoute = HttpRoutes.of[IO] {
        case GET -> Root / "repos" / _ / _ / "deployments" / _  => Ok(json"""
        {
          "url": "https://api.github.com/repos/octocat/example/deployments/1",
          "id": 1,
          "node_id": "MDEwOkRlcGxveW1lbnQx",
          "sha": "a84d88e7554fc1fa21bcbc4efae3c782a70d2b9d",
          "ref": "topic-branch",
          "task": "deploy",
          "payload": {
            "deploy": "migrate"
          },
          "original_environment": "staging",
          "environment": "production",
          "description": "Deploy request from hubot",
          "creator": {
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
          "created_at": "2012-07-20T01:19:13Z",
          "updated_at": "2012-07-20T01:19:13Z",
          "statuses_url": "https://api.github.com/repos/octocat/example/deployments/1/statuses",
          "repository_url": "https://api.github.com/repos/octocat/example",
          "transient_environment": false,
          "production_environment": true
        }
        """)
      }
      Deployments.deployment[IO](
        "anything",
        "anything",
        1,
        OAuth("anything"),
        GithubMediaType.`application/vnd.github.ant-man-preview+json`
      )
      .run(Client.fromHttpApp(getDeploymentRoute.orNotFound))
      .attempt
      .map{_ must beRight}
    }

    "list deployments" in {
      val listDeploymentsRoute = HttpRoutes.of[IO] {
        case GET -> Root / "repos" / _ / _ / "deployments" => Ok(json"""
        [
          {
            "url": "https://api.github.com/repos/octocat/example/deployments/1",
            "id": 1,
            "node_id": "MDEwOkRlcGxveW1lbnQx",
            "sha": "a84d88e7554fc1fa21bcbc4efae3c782a70d2b9d",
            "ref": "topic-branch",
            "task": "deploy",
            "payload": {
              "deploy": "migrate"
            },
            "original_environment": "staging",
            "environment": "production",
            "description": "Deploy request from hubot",
            "creator": {
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
            "created_at": "2012-07-20T01:19:13Z",
            "updated_at": "2012-07-20T01:19:13Z",
            "statuses_url": "https://api.github.com/repos/octocat/example/deployments/1/statuses",
            "repository_url": "https://api.github.com/repos/octocat/example",
            "transient_environment": false,
            "production_environment": true
          }
        ]
        """)
      }
      Deployments.listDeployments[IO](
        "anything",
        "anything",
        OAuth("anything"),
        GithubMediaType.`application/vnd.github.ant-man-preview+json`
      )
      .run(Client.fromHttpApp(listDeploymentsRoute.orNotFound))
      .attempt
      .map{_ must beRight}
    }

    "create a deployment status" in {

      val createDeploymentStatusRoute = HttpRoutes.of[IO] {
        case POST -> Root / "repos" / _ / _ / "deployments" / _ / "statuses"  => Ok(json"""
        {
          "url": "https://api.github.com/repos/octocat/example/deployments/42/statuses/1",
          "id": 1,
          "node_id": "MDE2OkRlcGxveW1lbnRTdGF0dXMx",
          "state": "success",
          "creator": {
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
          "description": "Deployment finished successfully.",
          "environment": "production",
          "target_url": "https://example.com/deployment/42/output",
          "created_at": "2012-07-20T01:19:13Z",
          "updated_at": "2012-07-20T01:19:13Z",
          "deployment_url": "https://api.github.com/repos/octocat/example/deployments/42",
          "repository_url": "https://api.github.com/repos/octocat/example",
          "environment_url": "",
          "log_url": "https://example.com/deployment/42/output"
        }
        """)
      }
      Deployments.createDeploymentStatuses[IO](
        "anything",
        "anything",
        1,
        data.Deployments.NewDeploymentStatus.create(data.Deployments.DeploymentState.Success),
        OAuth("anything"),
        GithubMediaType.`application/vnd.github.ant-man-preview+json`
      )
      .run(Client.fromHttpApp(createDeploymentStatusRoute.orNotFound))
      .attempt
      .map{_ must beRight}
    }

    "get a deployment status" in {

      val getDeploymentStatusRoute = HttpRoutes.of[IO] {
        case GET -> Root / "repos" / _ / _ / "deployments" / _ / "statuses" / _  => Ok(json"""
        {
          "url": "https://api.github.com/repos/octocat/example/deployments/42/statuses/1",
          "id": 1,
          "node_id": "MDE2OkRlcGxveW1lbnRTdGF0dXMx",
          "state": "success",
          "creator": {
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
          "description": "Deployment finished successfully.",
          "environment": "production",
          "target_url": "https://example.com/deployment/42/output",
          "created_at": "2012-07-20T01:19:13Z",
          "updated_at": "2012-07-20T01:19:13Z",
          "deployment_url": "https://api.github.com/repos/octocat/example/deployments/42",
          "repository_url": "https://api.github.com/repos/octocat/example",
          "environment_url": "",
          "log_url": "https://example.com/deployment/42/output"
        }
        """)
      }
      Deployments.deploymentStatus[IO](
        "anything",
        "anything",
        1,
        1,
        OAuth("anything"),
        GithubMediaType.`application/vnd.github.ant-man-preview+json`
      )
      .run(Client.fromHttpApp(getDeploymentStatusRoute.orNotFound))
      .attempt
      .map{_ must beRight}
    }

    "list deployment statuses" in {
      val listDeploymentStatusesRoute = HttpRoutes.of[IO] {
        case GET -> Root / "repos" / _ / _ / "deployments" / _ / "statuses" => Ok(json"""
        [
          {
            "url": "https://api.github.com/repos/octocat/example/deployments/42/statuses/1",
            "id": 1,
            "node_id": "MDE2OkRlcGxveW1lbnRTdGF0dXMx",
            "state": "success",
            "creator": {
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
            "description": "Deployment finished successfully.",
            "environment": "production",
            "target_url": "https://example.com/deployment/42/output",
            "created_at": "2012-07-20T01:19:13Z",
            "updated_at": "2012-07-20T01:19:13Z",
            "deployment_url": "https://api.github.com/repos/octocat/example/deployments/42",
            "repository_url": "https://api.github.com/repos/octocat/example",
            "environment_url": "",
            "log_url": "https://example.com/deployment/42/output"
          }
        ]
        """)
      }
      Deployments.listDeploymentStatuses[IO](
        "anything",
        "anything",
        1,
        OAuth("anything"),
        GithubMediaType.`application/vnd.github.ant-man-preview+json`
      )
      .run(Client.fromHttpApp(listDeploymentStatusesRoute.orNotFound))
      .attempt
      .map{_ must beRight}
    }
  }
}