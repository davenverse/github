package io.chrisdavenport.github

import cats.implicits._
import cats.effect._
import org.http4s.client.blaze.BlazeClientBuilder
import java.{util => ju}

object GetFileContent extends IOApp {

  def run(args: List[String]): IO[ExitCode] = {
    for {
      c <- BlazeClientBuilder[IO](scala.concurrent.ExecutionContext.global).resource
      out <- Resource.liftF(
          endpoints.repositories.Content.contentsFor[IO]("ChristopherDavenport", "github", "README.md", None, None)
            run(c)
        )
      _ <- Resource.liftF(out match {
        case data.Content.Content.File(data) => 
          val out = decodeBase64(data.content)
          IO(println(out))
        case _ => IO.unit
      })
    } yield ()
    
  }.use(_ => 
    IO.unit.as(ExitCode.Success)
  )

  private val base64 = ju.Base64.getMimeDecoder()
  def decodeBase64(s: String): String = {
    val bytes = base64.decode(s.getBytes(java.nio.charset.StandardCharsets.UTF_8))
    new String(bytes, java.nio.charset.StandardCharsets.UTF_8)
  } 


}