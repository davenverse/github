package io.chrisdavenport.github.endpoints

import cats.syntax.all._
import cats.data._
import cats.effect._
import io.chrisdavenport.github.data.Users._
import org.http4s._
import org.http4s.implicits._
import org.http4s.client.Client
import fs2.Stream

import io.chrisdavenport.github.Auth
import io.chrisdavenport.github.internals.GithubMedia._
import io.chrisdavenport.github.internals.RequestConstructor

object Users {

  def userInfoFor[F[_]: Sync](username: String, auth: Option[Auth]): Kleisli[F, Client[F], User] = 
    RequestConstructor.runRequestWithNoBody[F, User](
      auth,
      Method.GET,
      uri"users" / username
    )

  def ownerInfoFor[F[_]: Sync](owner: String, auth: Option[Auth]): Kleisli[F, Client[F], Owner] =
    RequestConstructor.runRequestWithNoBody[F, Owner](
      auth,
      Method.GET,
      uri"users" / owner
    )

  def userInfoAuthenticatedUser[F[_]: Sync](auth: Auth): Kleisli[F, Client[F], User] = 
    RequestConstructor.runRequestWithNoBody[F, User](
      auth.some,
      Method.GET,
      uri"user"
    )

  // We expose this as the returned list for each request
  // that way users can monitor how many requests they make
  // and can know where they stand in regards to their cap.
  def getAllUsers[F[_]: Sync](
    since: Option[String],
    auth: Option[Auth]
  ): Kleisli[Stream[F, ?], Client[F], List[SimpleOwner]] = 
    RequestConstructor.runPaginatedRequest[F, List[SimpleOwner]](
      auth,
      uri"users".withOptionQueryParam("since", since)
    )

  // Patch so presently only updates. Unsure 
  // if null values are removed entirely, so
  // for the first draft dropNull values
  // possibly in the future rework
  def updateAuthenticatedUser[F[_]: Sync](
    auth: Auth,
    name: Option[String],
    email: Option[String],
    blog: Option[String],
    company: Option[String],
    location: Option[String],
    hireable: Option[Boolean],
    bio: Option[String]
  ): Kleisli[F, Client[F], User] = {
    import io.circe._
    def fromOptionJson[A](fa: Option[A])(f: A => Json): Json =
      fa.fold(Json.Null)(f)
    def fromOptionString(fa: Option[String]): Json =
      fromOptionJson(fa)(Json.fromString)
    val json = Json.obj(
      "name" -> fromOptionString(name),
      "email" -> fromOptionString(email),
      "blog" -> fromOptionString(blog),
      "company" -> fromOptionString(company),
      "location" -> fromOptionString(location),
      "hireable" -> fromOptionJson(hireable)(Json.fromBoolean),
      "bio" -> fromOptionString(bio)
    ).dropNullValues
    RequestConstructor.runRequestWithBody[F, Json, User](
      auth.some,
      Method.PATCH,
      uri"user",
      json
    )
  }
}