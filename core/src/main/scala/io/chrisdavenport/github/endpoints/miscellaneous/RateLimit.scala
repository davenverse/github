package io.chrisdavenport.github.endpoints.miscellaneous


import cats.effect._
import org.http4s._
import org.http4s.implicits._

import io.chrisdavenport.github.data.{RateLimit => DRateLimit}
import io.chrisdavenport.github.Auth
import io.chrisdavenport.github.internals.GithubMedia._
import io.chrisdavenport.github.internals.RequestConstructor

object RateLimit {

  /**
   * Get your current rate limit status
   **/
  def rateLimit[F[_]: Concurrent](
    auth: Option[Auth]
  ) = RequestConstructor.runRequestWithNoBody[F, DRateLimit.RateLimit](
    auth,
    Method.GET,
    uri"rate_limit"
  )

}