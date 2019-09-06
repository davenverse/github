package io.chrisdavenport.github.internals



import cats.effect._
import org.http4s._
import org.http4s.implicits._
import org.http4s.client.Client
import org.http4s.headers.Authorization

import io.chrisdavenport.github._
import cats.data.Kleisli


object RequestConstructor {

  def runRequestWithNoBody[F[_]: Sync, B: EntityDecoder[F, ?]]
  (
    auth: Option[Auth],
    method: Method,
    extendedUri: Uri,
  ): Kleisli[F, Client[F], B] = Kleisli{c =>
    val uri = Uri.resolve(baseUrl(auth), extendedUri)
    val baseReq = Request[F](method = method, uri = uri).withHeaders(extraHeaders)
    val req = auth.fold(baseReq)(setAuth(_)(baseReq))
    c.expect[B](req)
  }

  val GITHUB_URI: Uri = uri"https://api.github.com"

  def baseUrl(auth: Option[Auth]): Uri = auth match {
    case Some(EnterpriseOAuth(apiEndpoint, _)) => apiEndpoint
    case _ => GITHUB_URI
  }

  def setAuth[F[_]](auth: Auth)(req: Request[F]): Request[F] = auth match{
    case BasicAuth(username, password) => 
      req.withHeaders(Authorization(BasicCredentials(username, password)))
    case EnterpriseOAuth(_, token) =>
      req.withHeaders(Authorization(Credentials.Token(AuthScheme.Basic, token)))
    case OAuth(token) =>
      req.withHeaders(Authorization(Credentials.Token(AuthScheme.Basic, token)))
  }

  val extraHeaders: Headers = Headers(
    Header("User-Agent", "github.scala/" ++ io.chrisdavenport.github.BuildInfo.version),
    Header("Accept", "application/vnd.github.v3+json")
  )
}