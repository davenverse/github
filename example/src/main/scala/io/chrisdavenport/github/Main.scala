package io.chrisdavenport.github

import cats.implicits._
import cats.effect._
import cats.data.Kleisli

import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import scala.concurrent.duration._
import org.typelevel.log4cats.slf4j.Slf4jLogger
import scodec.bits._

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] = {
    val logger = Slf4jLogger.getLogger[IO]
    for {
      c <- EmberClientBuilder.default[IO].build
      home <- Resource.eval(IO(sys.env("HOME")))
        .map(_.trim())
      authLine <- Resource.eval(IO(scala.io.Source.fromFile(home |+| "/Documents/.token_test").getLines().toList.head))

      auth = OAuth(authLine)

      _ <- Resource.eval{
        import data.Content.CreateFile
        import data.Repositories.NewRepo
        import data.GitData._
        import endpoints.Repositories.createRepo
        import endpoints.gitdata.Blobs._
        import endpoints.gitdata.Trees._
        import endpoints.gitdata.Commits._
        import endpoints.gitdata.References._
        import endpoints.repositories.Content.createFile
        val app = for {
          _ <- Kleisli.liftF[IO, Client[IO], Unit](logger.info("Starting App"))

          owner = "ChristopherDavenport"
          repo = "test-repo2"

          repoO <- createRepo[IO](
            NewRepo(
              repo,
              "Description".some,
              None,
              None,
              None,
              None,
              None,
              None
            ),
            auth
          )
          _ <- Kleisli.liftF(logger.debug(s"Repo Created $repoO"))
          _ <- Kleisli.liftF(Temporal[IO].sleep(10.seconds))
          file <- createFile[IO](
            owner,
            repo,
            CreateFile(
              "README.md",
              "File To Initialize Repo",
              ByteVector("# Initialized Repo File".getBytes()).toBase64,
              None,
              None,
              None
            ),
            auth
          )
          _ <- Kleisli.liftF(logger.debug(s"File Created : $file"))
          blob <- createBlob[IO](
            owner,
            repo,
            CreateBlob(
              "# Test Readme asdfa",
              Encoding.Utf8
            ),
            auth
          )
          _ <- Kleisli.liftF(logger.debug(s"Blob Created $blob"))
          tree <- createTree[IO](
            owner,
            repo,
            CreateTree(
              List(
                CreateGitTree.CreateGitTreeSha(
                  "README.md",
                  blob.sha.some,
                  GitObjectType.Blob,
                  GitMode.File
                )
              ),
              None
            ),
            auth
          )
          _ <- Kleisli.liftF(logger.debug(s"Tree Created: $tree"))
          commit <- createCommit[IO](
            owner,
            repo,
            CreateCommit.simple(
              "Initial Commit",
              tree.sha,
              List()
            ),
            auth
          )
          _ <- Kleisli.liftF(logger.debug(s"Commit Created $commit"))
          ref <- updateReference[IO](
            owner,
            repo,
            "heads/master", // Working with refs sucks presently
            UpdateReference(
              commit.sha,
              true
            ),
            auth
          )
          _ <- Kleisli.liftF(logger.debug(s"Ref Updated $ref"))
        } yield ()

        app.run(c).attempt.flatTap{
          case Right(a) => logger.debug(s"Application Completed Succesfully - $a")
          case Left(e) => logger.error(e)(s"Application Failed - ${Option(e.getStackTrace())}")
        }
      }
      //   endpoints.gitdata.Trees.createTree[IO](
      //     "ChristopherDavenport",
      //     "test-repo1",

      //     data.GitData.CreateTree(
      //       List(
      //         data.GitData.CreateGitTree.CreateGitTreeBlob(
      //           "README.md",
      //           "# Test Readme Created By App",
      //           Either.right(data.GitData.GitMode.File)
      //         ),
      //         // data.GitData.CreateGitTree.CreateGitTreeBlob(
      //         //   "subdir/README.md",
      //         //   "# Test Readme Created By App",
      //         //   Either.right(data.GitData.GitMode.File)
      //         // ),
      //       ),
      //       None
      //     ),
      //     auth

      //   ).run(c)
      //   .flatTap(a => IO(println(a)))
      // )
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
      // }
    
  }.use(_ => 
    // IO(println(out)).as(ExitCode.Success)
    IO.unit.as(ExitCode.Success)
  )

  def liftPrint[A](io: IO[A]): Resource[IO, A] = 
    Resource.eval(io).evalTap(a => IO(println(a)))
}
