package io.chrisdavenport.github

import cats.implicits._
import cats.effect._

import org.http4s.client.blaze.BlazeClientBuilder

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] = {
    for {
      c <- BlazeClientBuilder[IO](scala.concurrent.ExecutionContext.global).resource
      // out <- Resource.liftF(endpoints.Users.userInfoCurrent[IO](auth).run(c))
      out <- Resource.liftF(endpoints.Users.userInfoFor[IO]("ChristopherDavenport").run(c))
    } yield out
    
  }.use(out => 
    IO(println(out)).as(ExitCode.Success)
  )

  // IO(println("I am a new project!")).as(ExitCode.Success)


}