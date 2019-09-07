package io.chrisdavenport.github

import cats.implicits._
import cats.effect._

import org.http4s.client.blaze.BlazeClientBuilder

import endpoints._
object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] = {
    for {
      c <- BlazeClientBuilder[IO](scala.concurrent.ExecutionContext.global).resource
      home <- Resource.liftF(IO(sys.env("HOME")))
        .map(_.trim())
      authLine <- Resource.liftF(IO(scala.io.Source.fromFile(home |+| "/Documents/.token_test").getLines.toList.head))

      auth = OAuth(authLine)
      
      // out <- Resource.liftF(
      //   Users.getAllUsers[IO](None, None)
      //   .run(c)
      //   .evalTap(s => IO(println(s)))
      //   .compile
      //   .drain
      // )
      out <- liftPrint(endpoints.Users.userInfoAuthenticatedUser[IO](auth).run(c))
      out <- liftPrint(endpoints.Users.ownerInfoFor[IO]("http4s", auth.some).run(c))
    } yield out
    
  }.use(out => 
    // IO(println(out)).as(ExitCode.Success)
    IO.unit.as(ExitCode.Success)
  )

  def liftPrint[A](io: IO[A]): Resource[IO, A] = 
    Resource.liftF(io).evalTap(a => IO(println(a)))

  // IO(println("I am a new project!")).as(ExitCode.Success)


}