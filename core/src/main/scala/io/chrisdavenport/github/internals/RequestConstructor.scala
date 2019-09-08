package io.chrisdavenport.github.internals

import cats._
import cats.effect._
import cats.implicits._
import fs2._
import org.http4s._
import org.http4s.implicits._
import org.http4s.client.Client
import org.http4s.headers.Authorization

import _root_.io.chrisdavenport.github._
import cats.data.Kleisli
import org.http4s.headers.Link


object RequestConstructor {

  def runRequestWithNoBody[F[_]: Sync, B: EntityDecoder[F, ?]]
  (
    auth: Option[Auth],
    method: Method,
    extendedUri: Uri,
  ): Kleisli[F, Client[F], B] =
    runRequest[F, Unit, B](auth, method, extendedUri, None)

  def runRequestWithBody[F[_]: Sync, A: EntityEncoder[F, ?], B: EntityDecoder[F, ?]](
    auth: Option[Auth],
    method: Method,
    extendedUri: Uri,
    body: A
  ): Kleisli[F, Client[F], B] =
    runRequest[F, A, B](auth, method, extendedUri, body.some)

  def runRequest[F[_]: Sync, A: EntityEncoder[F, ?], B: EntityDecoder[F, ?]](
    auth: Option[Auth],
    method: Method,
    extendedUri: Uri,
    body: Option[A]
  ):  Kleisli[F, Client[F], B] = Kleisli{ c =>
    val uri = Uri.resolve(baseUrl(auth), extendedUri)
    val baseReq = Request[F](method = method, uri = uri)
      .withHeaders(extraHeaders)
    val req2 = auth.fold(baseReq)(setAuth(_)(baseReq))
    val req = body.fold(req2)(a => req2.withEntity(a))
    c.expect[B](req)
  }

  private def getNextUri[F[_]: MonadError[*[_], Throwable]](r: Response[F]): Option[Uri] = {
    for {
      next <- r.headers.toList.map(Link.matchHeader).collect{
        case Some(Link(uri, Some("next"), _, _, _)) => uri
      }.headOption
    } yield next
  } 

  def runPaginatedRequest[F[_]: Sync, B: EntityDecoder[F, ?]](
    auth: Option[Auth],
    extendedUri: Uri,
  ):  Kleisli[Stream[F, ?], Client[F], B] = Kleisli{ c =>
    val uri = Uri.resolve(baseUrl(auth), extendedUri)

    unfoldLoopEval(uri){uri => 
      val baseReq = Request[F](method = Method.GET, uri = uri)
        .withHeaders(extraHeaders)
      val req = auth.fold(baseReq)(setAuth(_)(baseReq))
      c.run(req).use{resp => 
          resp.as[B].map{
            (_, getNextUri(resp))
          }
        }
    }
  }


  //These two will be in the next version of fs2
  

  /**
    * Creates a stream by successively applying `f` to a `S`, emitting
    * each output `O` and using each output `S` as input to the next invocation of `f`
    * if it is Some, or terminating on Noine
    *
    * {{{
    * scala> unfoldLoop(0)(i => (i, if (i < 5) Some((i+1)) else None)).toList
    * res0: List[Int] = List(0, 1, 2, 3, 4, 5)
    * }}}
    */
  private def unfoldLoop[F[x] <: Pure[x], S, O](s: S)(f: S => (O, Option[S])): Stream[F, O] = 
    Pull.loop[F, O, S]{
      s => 
          val (o, sOpt) = f(s)
          Pull.output1(o) >> Pull.pure(sOpt)
    }(s)
    .void
    .stream
  
  /** Like [[unfoldLoop]], but takes an effectful function. */
  private def unfoldLoopEval[F[_], S, O](s: S)(f: S => F[(O, Option[S])]): Stream[F, O] =
    Pull
      .loop[F, O, S](
        s =>
          Pull.eval(f(s)).flatMap {
            case (o, sOpt) => Pull.output1(o) >> Pull.pure(sOpt)
          }
      )(s)
      .void
      .stream

  private val GITHUB_URI: Uri = uri"https://api.github.com"

  private def baseUrl(auth: Option[Auth]): Uri = auth match {
    case Some(EnterpriseOAuth(apiEndpoint, _)) => apiEndpoint
    case _ => GITHUB_URI
  }

  private def setAuth[F[_]](auth: Auth)(req: Request[F]): Request[F] = auth match{
    case BasicAuth(username, password) => 
      req.withHeaders(Authorization(BasicCredentials(username, password)))
    case EnterpriseOAuth(_, token) =>
      req.withHeaders(Authorization(Credentials.Token(AuthScheme.Bearer, token)))
    case OAuth(token) =>
      req.withHeaders(Authorization(Credentials.Token(AuthScheme.Bearer, token)))
  }

  private val extraHeaders: Headers = Headers(
    Header("User-Agent", "github.scala/" ++ _root_.io.chrisdavenport.github.BuildInfo.version),
    Header("Accept", "application/vnd.github.v3+json")
  )
}