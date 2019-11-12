package io.chrisdavenport.github

import cats.implicits._
import cats.effect._
import cats.data.Kleisli

import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] = {
    for {
      c <- BlazeClientBuilder[IO](scala.concurrent.ExecutionContext.global).resource
      home <- Resource.liftF(IO(sys.env("HOME")))
        .map(_.trim())
      authLine <- Resource.liftF(IO(scala.io.Source.fromFile(home |+| "/Documents/.token_test").getLines.toList.head))

      auth = OAuth(authLine)
      
      // _ <- Resource.liftF(
      //   endpoints.gitdata.
      //   endpoints.gitdata.Trees.getTree[IO](
      //     "ChristopherDavenport",
      //     "github",
      //     "17dafee2df113441127dad4fba9e0be65e82c4b3",
      //     auth.some
      //   ).run(c)
      //   .flatTap(a => IO(println(a)))
      // )
      _ <- Resource.liftF{
        import data.Repositories.NewRepo
        import data.GitData._
        import endpoints.Repositories.createRepo
        import endpoints.gitdata.Blobs._
        import endpoints.gitdata.Trees._
        import endpoints.gitdata.Commits._
        import endpoints.gitdata.References._
        val app = for {
          _ <- Kleisli.liftF[IO, Client[IO], Unit](IO(println("Starting App")))

          owner = "ChristopherDavenport"
          repo = "test-repo2"

          _ <- createRepo[IO](
            ???,
            // NewRepo(
            //   repo,
            //   "Description".some,
            //   None,
              
            // ),
            auth
          )
          blob <- createBlob[IO](
            owner,
            repo,
            CreateBlob(
              "# Test Readme asdfa",
              Encoding.Utf8
            ),
            auth
          )
          _ <- Kleisli.liftF(IO(println(blob)))
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
          _ <- Kleisli.liftF(IO(println(tree)))
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
          _ <- Kleisli.liftF(IO(println(commit)))
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
          _ <- Kleisli.liftF(IO(println(ref)))
        } yield ()

        app.run(c).flatTap(a => IO(println(a)))
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
    Resource.liftF(io).evalTap(a => IO(println(a)))
}