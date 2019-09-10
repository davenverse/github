package io.chrisdavenport.github.endpoints

import cats.data._
import cats.effect._
import cats.implicits._
import io.chrisdavenport.github.data.Deployments._
import org.http4s._
import org.http4s.implicits._
import org.http4s.client.Client

import io.chrisdavenport.github.Auth
import io.chrisdavenport.github.internals.GithubMedia._
import io.chrisdavenport.github.internals.RequestConstructor
import io.chrisdavenport.github.internals.GithubMediaType

object Deployments {

  def deployment[F[_]: Sync](
    owner: String,
    repo: String,
    deployment: Int,
    auth: Auth,
    accept: GithubMediaType
  ): Kleisli[F, Client[F], Deployment] = 
  RequestConstructor.runRequestWithNoBodyAccept[F, Deployment](
      auth.some,
      Method.GET,
      uri"/repos" / owner / repo / "deployments" / deployment.toString,
      accept
    )

  def createDeployment[F[_]: Sync](
    owner: String,
    repo: String,
    newDeployment: NewDeployment,
    auth: Auth,
    accept: GithubMediaType
  ): Kleisli[F, Client[F], Deployment] =
  RequestConstructor.runRequestWithBodyAccept[F, NewDeployment, Deployment](
    auth.some,
    Method.POST,
    uri"/repos" / owner / repo / "deployments",
    newDeployment,
    accept
  )

  def listDeployments[F[_]: Sync](
    owner: String,
    repo: String,
    auth: Auth,
    accept: GithubMediaType
  ): Kleisli[F, Client[F], List[Deployment]] = 
  RequestConstructor.runRequestWithNoBodyAccept[F, List[Deployment]](
      auth.some,
      Method.GET,
      uri"/repos" / owner / repo / "deployments",
      accept
    )

  def deploymentStatus[F[_]: Sync](
    owner: String,
    repo: String,
    deployment: Int,
    status: Int,
    auth: Auth,
    accept: GithubMediaType
  ): Kleisli[F, Client[F], DeploymentStatus] = 
  RequestConstructor.runRequestWithNoBodyAccept[F, DeploymentStatus](
      auth.some,
      Method.GET,
      uri"/repos" / owner / repo / "deployments" / deployment.toString / "statuses" / status.toString,
      accept
    )

  def createDeploymentStatuses[F[_]: Sync](
    owner: String,
    repo: String,
    deployment: Int,
    status: NewDeploymentStatus,
    auth: Auth,
    accept: GithubMediaType
  ): Kleisli[F, Client[F], DeploymentStatus] = 
  RequestConstructor.runRequestWithBodyAccept[F, NewDeploymentStatus, DeploymentStatus](
      auth.some,
      Method.POST,
      uri"/repos" / owner / repo / "deployments" / deployment.toString / "statuses",
      status,
      accept
    )

  def listDeploymentStatuses[F[_]: Sync](
    owner: String,
    repo: String,
    deployment: Int,
    auth: Auth,
    accept: GithubMediaType
  ): Kleisli[F, Client[F], List[DeploymentStatus]] = 
  RequestConstructor.runRequestWithNoBodyAccept[F, List[DeploymentStatus]](
      auth.some,
      Method.GET,
      uri"/repos" / owner / repo / "deployments" / deployment.toString / "statuses",
      accept
    )
}