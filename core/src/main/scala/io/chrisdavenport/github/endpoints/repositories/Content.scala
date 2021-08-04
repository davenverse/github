package io.chrisdavenport.github.endpoints.repositories

import cats.implicits._
import cats.data._
import cats.effect._
import io.chrisdavenport.github.data.Content._
import org.http4s._
import org.http4s.implicits._
import org.http4s.client.Client

import io.chrisdavenport.github.Auth
import io.chrisdavenport.github.internals.GithubMedia._
import io.chrisdavenport.github.internals.RequestConstructor

object Content {
  def contentsFor[F[_]: Concurrent](
    owner: String,
    repo: String,
    path: String,
    ref: Option[String],
    auth: Option[Auth]
  ): Kleisli[F, Client[F], Content] =
    RequestConstructor.runRequestWithNoBody[F, Content](
      auth,
      Method.GET,
      (uri"repos" / owner / repo / "contents" / path)
        .withOptionQueryParam("ref", ref)
    )

  def readmeFor[F[_]: Concurrent](
    owner: String,
    repo: String,
    auth: Option[Auth]
  ) = 
    RequestConstructor.runRequestWithNoBody[F, Content](
      auth,
      Method.GET,
      uri"repos" / owner / repo / "readme"
    )

  def createFile[F[_]: Concurrent](
    owner: String,
    repo: String,
    createFile: CreateFile,
    auth: Auth
  ) = RequestConstructor.runRequestWithBody[F, CreateFile, ContentResult](
    auth.some,
    Method.PUT,
    uri"repos" / owner /  repo / "contents" / createFile.path,
    createFile
  )

  def updateFile[F[_]: Concurrent](
    owner: String,
    repo: String,
    updateFile: UpdateFile,
    auth: Auth
  ) = RequestConstructor.runRequestWithBody[F, UpdateFile, ContentResult](
    auth.some,
    Method.PUT,
    uri"repos" / owner /  repo / "contents" / updateFile.path,
    updateFile
  )

  def deleteFile[F[_]: Concurrent](
    owner: String,
    repo: String,
    deleteFile: DeleteFile,
    auth: Auth
  ) = RequestConstructor.runRequestWithBody[F, DeleteFile, Unit](
    auth.some,
    Method.DELETE,
    uri"repos" / owner /  repo / "contents" / deleteFile.path,
    deleteFile
  )
  
  
}