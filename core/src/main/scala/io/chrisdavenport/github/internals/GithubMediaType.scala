package io.chrisdavenport.github.internals

sealed trait GithubMediaType
object GithubMediaType {
  final case object `application/vnd.github.v3+json` extends GithubMediaType
  final case object `application/vnd.github.ant-man-preview+json` extends GithubMediaType
  final case object `application/vnd.github.flash-preview+json` extends GithubMediaType
  final case object `application/vnd.github.machine-man-preview` extends GithubMediaType

  def render(t: GithubMediaType): String = t match {
    case `application/vnd.github.v3+json` => "application/vnd.github.v3+json"
    case `application/vnd.github.ant-man-preview+json` => "application/vnd.github.ant-man-preview+json"
    case `application/vnd.github.flash-preview+json` => "application/vnd.github.flash-preview+json"
    case `application/vnd.github.machine-man-preview` => "application/vnd.github.machine-man-preview"
  }
}