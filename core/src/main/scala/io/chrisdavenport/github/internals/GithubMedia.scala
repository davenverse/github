package io.chrisdavenport.github.internals

import cats._
import io.circe._
import org.http4s._
import org.http4s.circe._

object GithubMedia extends CirceEntityDecoder {
  implicit def jsonEncoder[F[_]: Applicative, A: Encoder] ={
    val json = jsonEncoderOf[F, A]

    new EntityEncoder[F, A]{
      def headers: Headers = 
        Headers.of(
          Header("Accept", "application/vnd.github.v3+json")
        ) ++ json.headers
      def toEntity(a: A): Entity[F] = json.toEntity(a)
    }
  }
}
