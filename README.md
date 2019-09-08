# github - Github Integration for Scala [![Build Status](https://travis-ci.com/ChristopherDavenport/github.svg?branch=master)](https://travis-ci.com/ChristopherDavenport/github) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.chrisdavenport/github_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.chrisdavenport/github_2.12) ![Code of Consuct](https://img.shields.io/badge/Code%20of%20Conduct-Scala-blue.svg)

## [Head on over to the microsite](https://ChristopherDavenport.github.io/github)

## Contributing

This library is just starting! [Github is a big API](https://developer.github.com/v3/), and to get it all mapped it going to take some time and energy,
however I believe we have a firm foundation on which to build and the tools set up for quick iterations.

The library has taken lessons from the haskell [github](http://hackage.haskell.org/package/github-0.21).
The design goals is that we break data into relevant objects from the Github pages, with modules breaking out subpages from the overarching layout on the [api guide](https://developer.github.com/v3/).

Then the endpoints module is where the methods that call those individual endpoints live.
That should make it easy to seperate concerns such as serialization to the data modules, and map the
functions to what behavior they want to call, which is easily isolatable by the external api.

Requests then go through the requestConstructor which has options for both paginated and non-paginated requests.
Any methods that update information require auth. We want to make sure to expose `Option[Auth]` on all endpoints,
regardless of whether they require it as rate limiting differs at 60/hr for unauthed, and 5000/hr for authed.

Tests are focused around creating HttpClients from the endpoints listed on Github via Circe literal and http4s-dsl,
this allows us to make sure our endpoints deserialization behavior works for the final output.
Github will overall be the judge for our serialization, but we can test it looks like we expect.

Please create issues to let others know what endpoints you are working on,
so we can build something that works nicely for everyone.

## Quick Start

To use github in an existing SBT project with Scala 2.11 or a later version, add the following dependencies to your
`build.sbt` depending on your needs:

```scala
libraryDependencies ++= Seq(
  "io.chrisdavenport" %% "github" % "<version>"
)
```
