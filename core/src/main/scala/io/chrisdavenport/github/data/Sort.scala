package io.chrisdavenport.github.data

sealed trait Sort

object Sort {

  case object BestMatch extends Repository with User
  case object Stars extends Repository
  case object Forks extends Repository
  case object HelpWantedIssues extends Repository
  case object Updated extends Repository
  case object Followers extends User
  case object Repositories extends User
  case object Joined extends User
  case object Newest extends Fork
  case object Oldest extends Fork
  case object Stargazers extends Fork

  sealed trait Repository extends io.chrisdavenport.github.data.Sort
  sealed trait User extends io.chrisdavenport.github.data.Sort
  sealed trait Fork extends io.chrisdavenport.github.data.Sort

  def toOptionalParam(sort: Sort): Option[String] =
    sort match {
      case BestMatch => None
      case Stars => Some("stars")
      case Forks => Some("forks")
      case HelpWantedIssues => Some("help-wanted-issues")
      case Updated => Some("updated")
      case Followers => Some("followers")
      case Repositories => Some("repositories")
      case Joined => Some("joined")
      case Newest => Some("newest")
      case Oldest => Some("oldest")
      case Stargazers => Some("stargazers")
    }

}
