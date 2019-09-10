package io.chrisdavenport.github.endpoints

import cats.implicits._
import cats.data._
import cats.effect._
import io.chrisdavenport.github.data.Deployments._
import org.http4s._
import org.http4s.implicits._
import org.http4s.client.Client

import io.chrisdavenport.github.Auth
import io.chrisdavenport.github.internals.GithubMedia._
import io.chrisdavenport.github.internals.RequestConstructor

object Deployments {

  def deployment[F[_]: Sync](
    owner: String,
    repo: String,
    deployment: Int,
    auth: Auth
  ): Kleisli[F, Client[F], Deployment] = 
  RequestConstructor.runRequestWithNoBody[F, Deployment](
      auth.some,
      Method.GET,
      uri"/repos" / owner / repo / "deployments" / deployment.toString()
    )

  def createDeployment[F[_]: Sync](
    owner: String,
    repo: String,
    newDeployment: NewDeployment,
    auth: Auth
  ): Kleisli[F, Client[F], Deployment] =
  RequestConstructor.runRequestWithBody[F, NewDeployment, Deployment](
    auth.some,
    Method.POST,
    uri"/repos" / owner / repo / "deployments",
    newDeployment
  )

  def listDeployments[F[_]: Sync](
    owner: String,
    repo: String,
    auth: Auth
  ): Kleisli[F, Client[F], List[Deployment]] = 
  RequestConstructor.runRequestWithNoBody[F, List[Deployment]](
      auth.some,
      Method.GET,
      uri"/repos" / owner / repo / "deployments"
    )

  def deploymentStatus[F[_]: Sync](
    owner: String,
    repo: String,
    deployment: Int,
    status: Int,
    auth: Auth
  ): Kleisli[F, Client[F], DeploymentStatus] = 
  RequestConstructor.runRequestWithNoBody[F, DeploymentStatus](
      auth.some,
      Method.GET,
      uri"/repos" / owner / repo / "deployments" / deployment.toString() / "statuses" / status.toString()
    )

  def createDeploymentStatuses[F[_]: Sync](
    owner: String,
    repo: String,
    deployment: Int,
    status: NewDeploymentStatus,
    auth: Auth
  ): Kleisli[F, Client[F], DeploymentStatus] = 
  RequestConstructor.runRequestWithBody[F, NewDeploymentStatus, DeploymentStatus](
      auth.some,
      Method.POST,
      uri"/repos" / owner / repo / "deployments" / deployment.toString() / "statuses",
      status
    )

  def listDeploymentStatuses[F[_]: Sync](
    owner: String,
    repo: String,
    deployment: Int,
    auth: Auth
  ): Kleisli[F, Client[F], List[DeploymentStatus]] = 
  RequestConstructor.runRequestWithNoBody[F, List[DeploymentStatus]](
      auth.some,
      Method.GET,
      uri"/repos" / owner / repo / "deployments" / deployment.toString() / "statuses"
    )
}