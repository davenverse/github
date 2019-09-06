package io.chrisdavenport.github

import org.http4s.Uri

sealed trait Auth
final case class BasicAuth(username: String, password: String) extends Auth
final case class OAuth(token: String) extends Auth
final case class EnterpriseOAuth(apiEndpoint: Uri, token: String) extends Auth