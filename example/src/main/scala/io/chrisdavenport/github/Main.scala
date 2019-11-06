package io.chrisdavenport.github

import cats.implicits._
import cats.effect._

import org.http4s.client.blaze.BlazeClientBuilder

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] = {
    for {
      c <- BlazeClientBuilder[IO](scala.concurrent.ExecutionContext.global).resource
      home <- Resource.liftF(IO(sys.env("HOME")))
        .map(_.trim())
      authLine <- Resource.liftF(IO(scala.io.Source.fromFile(home |+| "/Documents/.token_test").getLines.toList.head))

      auth = OAuth(authLine)
      
      _ <- Resource.liftF(
        endpoints.gitdata.Trees.getTree[IO](
          "ChristopherDavenport",
          "github",
          "17dafee2df113441127dad4fba9e0be65e82c4b3",
          auth.some
        ).run(c)
        .flatTap(a => IO(println(a)))
      )
      // _ <- Resource.liftF(
      //   endpoints.miscellaneous.RateLimit.rateLimit[IO](auth.some)
      //   .run(c)
      //   .flatTap(a => IO(println(a)))
      // )
      // out <- liftPrint(endpoints.Users.userInfoAuthenticatedUser[IO](auth).run(c))
      // out <- liftPrint(endpoints.Users.ownerInfoFor[IO]("http4s", auth.some).run(c))
      // _ <- liftPrint(endpoints.Repositories.repository[IO]("http4s", "http4s", auth.some).run(c))
      // _ <- liftPrint(endpoints.Repositories.create[IO](NewRepo.create("test-creation-1"), auth).run(c))
      // _ <- liftPrint(endpoints.Repositories.edit[IO](
      //   "ChristopherDavenport",
      //   "test-creation-1",
      //   data.Repositories.EditRepo(None, "foo".some, None, true.some, true.some, true.some, true.some),
      //   auth
      //   ).run(c))
    } yield ()
    
  }.use(_ => 
    // IO(println(out)).as(ExitCode.Success)
    IO.unit.as(ExitCode.Success)
  )

  def liftPrint[A](io: IO[A]): Resource[IO, A] = 
    Resource.liftF(io).evalTap(a => IO(println(a)))
}