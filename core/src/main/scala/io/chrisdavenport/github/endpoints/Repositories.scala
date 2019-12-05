package io.chrisdavenport.github.endpoints

import cats.implicits._
import cats.data._
import cats.effect._
import io.chrisdavenport.github.data.Repositories._
import org.http4s._
import org.http4s.implicits._
import org.http4s.client.Client

import io.chrisdavenport.github.Auth
import io.chrisdavenport.github.internals.RequestConstructor


object Repositories {

  def repository[F[_]: Sync](
    owner: String,
    repo: String,
    auth: Option[Auth]
  ): Kleisli[F, Client[F], Repo] = {
    import io.chrisdavenport.github.internals.GithubMedia._
    RequestConstructor.runRequestWithNoBody[F, Repo](
      auth,
      Method.GET,
      uri"repos" / owner / repo
    )
  }

  def createRepo[F[_]: Sync](
    newRepo: NewRepo,
    auth: Auth
  ): Kleisli[F, Client[F], Repo] = {
    import io.chrisdavenport.github.internals.GithubMedia._
    RequestConstructor.runRequestWithBody[F, NewRepo, Repo](
      auth.some,
      Method.POST,
      uri"user/repos",
      newRepo
    )
  }

  def createOrganizationRepo[F[_]: Sync](
    org: String,
    newRepo: NewRepo,
    auth: Auth
  ): Kleisli[F, Client[F], Repo] = {
    import io.chrisdavenport.github.internals.GithubMedia._
    RequestConstructor.runRequestWithBody[F, NewRepo, Repo](
      auth.some,
      Method.POST,
      uri"orgs" / org / "repos",
      newRepo
    )
  }

  def edit[F[_]: Sync](
    owner: String,
    repo: String,
    editRepo: EditRepo,
    auth: Auth
  ): Kleisli[F, Client[F], Repo] = {
    import io.chrisdavenport.github.internals.GithubMedia._
    RequestConstructor.runRequestWithBody[F, EditRepo, Repo](
      auth.some,
      Method.PATCH,
      uri"repos" / owner / repo,
      editRepo
    )
  }

  /**
   * Deleting a repository requires admin access. If OAuth is used, the delete_repo scope is required.
   **/
  def delete[F[_]: Sync](
    owner: String,
    repo: String,
    auth: Auth
  ): Kleisli[F, Client[F], Unit] =
    RequestConstructor.runRequestWithNoBody[F, Unit](
      auth.some,
      Method.DELETE,
      uri"repos" / owner / repo
    )

}