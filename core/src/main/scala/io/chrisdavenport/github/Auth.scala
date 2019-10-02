package io.chrisdavenport.github

import org.http4s.Uri

/**
 * The authentication method to use
 */
sealed trait Auth

/**
 * Authenticates using username and password
 * @param username The username
 * @param password The password
 */
final case class BasicAuth(username: String, password: String) extends Auth

/**
 * Authenticates using an OAuth token
 * @param token The OAuth token
 */
final case class OAuth(token: String) extends Auth
/**
 * Authenticates to a github enterprise using a custom api enpoint uri
 * @param apiEndpoint The github enterprise's endpoint uri. Must end in '/'
 * @param token The OAuth token
 */
final case class EnterpriseOAuth(apiEndpoint: Uri, token: String) extends Auth