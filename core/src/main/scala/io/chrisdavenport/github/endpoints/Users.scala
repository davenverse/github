package io.chrisdavenport.github.endpoints

import cats.implicits._
import cats.data._
import cats.effect._
import io.chrisdavenport.github.data.definitions._
import org.http4s._
import org.http4s.implicits._
import org.http4s.client.Client
import org.http4s.circe.CirceEntityCodec._

import io.chrisdavenport.github.Auth

import io.chrisdavenport.github.internals

object Users {

  def userInfoFor[F[_]: Sync](username: String): Kleisli[F, Client[F], User] = 
    internals.RequestConstructor.runRequestWithNoBody[F, User](
      None,
      Method.GET,
      uri"/users" / username)

  def userInfoCurrent[F[_]: Sync](auth: Auth): Kleisli[F, Client[F], User] = 
    internals.RequestConstructor.runRequestWithNoBody[F, User](
      auth.some,
      Method.GET,
      uri"/user".withQueryParam("user")
    )
}