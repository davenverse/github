package io.chrisdavenport.github.internals

import cats._
import cats.effect._
import cats.data._
import io.circe._
import org.http4s._
import org.http4s.circe._
import org.http4s.headers.Accept
import org.http4s.headers.MediaRangeAndQValue


object GithubMedia extends CirceEntityDecoder {
  implicit def jsonEncoder[F[_]: Applicative, A: Encoder] ={
    val json = jsonEncoderOf[F, A]

    new EntityEncoder[F, A]{
      def headers: Headers = 
        Headers(
          Header("Accept", "application/vnd.github.v3+json")
        ) ++ json.headers
      def toEntity(a: A): Entity[F] = json.toEntity(a)
    }
  }
}
