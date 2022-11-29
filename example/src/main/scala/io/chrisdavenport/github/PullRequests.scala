package io.chrisdavenport.github

import cats.effect._
import org.http4s.ember.client.EmberClientBuilder
import io.chrisdavenport.github.endpoints.PullRequests


object PullRequestsExample extends IOApp {

  def run(args: List[String]): IO[ExitCode] = {
    for {
      c <- EmberClientBuilder.default[IO].build
      out <-
          PullRequests.pullRequestsFor[IO](
            "ChristopherDavenport",
            "github",
            None
          )
          .run(c)
          .take(2)
          .compile
          .resource
          .toList
    } yield out
    
  }.use(requests => 
    IO(println(requests)).as(ExitCode.Success)
  )

}