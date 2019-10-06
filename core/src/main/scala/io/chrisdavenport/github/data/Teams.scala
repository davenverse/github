package io.chrisdavenport.github.data

import org.http4s.Uri
import io.circe._
import io.circe.syntax._

object Teams {
  sealed trait Privacy
  object Privacy {
    case object Closed extends Privacy
    case object Secret extends Privacy
  }

  sealed trait Permission
  object Permission {
    case object Pull extends Permission
    case object Push extends Permission
    case object Admin extends Permission

    implicit val encoder = new Encoder[Permission]{
      def apply(a: Permission): Json = a match {
        case Pull => "pull".asJson
        case Push => "push".asJson
        case Admin => "admin".asJson
      }
    }
  }

  final case class AddTeamRepoPermission(
    permission: Permission
  )
  object AddTeamRepoPermission {
    implicit val encoder = new Encoder[AddTeamRepoPermission]{
      def apply(a: AddTeamRepoPermission): Json = Json.obj(
        "permissions" -> a.permission.asJson
      )
    }
  }

  final case class SimpleTeam(
    id: Int,
    name: String,
    slug: String,
    description: Option[String],
    privacy: Option[Privacy],
    permission: Permission,
    uri: Uri,
    membersUri: Uri,
    repositoriesUri: Uri
  )

  final case class Team(
    id: Int, 
    name: String,
    slug: String,
    description: Option[String],
    privacy: Option[Privacy],
    permission: Permission,
    uri: Uri,
    memberUri: Uri,
    repositoriesUri: Uri,
    membersCount: Int,
    reposCount: Int,
    // organization: SimpleOrganization
  )

  final case class CreateTeam(
    name: String,
    description: Option[String],
    repoNames: List[String],
    permission: Permission
  )

  final case class EditTeam(
    name: String,
    description: Option[String],
    permission: Permission
  )

  sealed trait Role
  object Role {
    case object Maintainer extends Role
    case object Member extends Role
  }

  sealed trait ReqState
  object ReqState {
    case object Pending extends ReqState
    case object Active extends ReqState
  }

  final case class TeamMembership(
    uri: Uri,
    role: Role,
    state: ReqState
  )

  sealed trait TeamMemberRole
  object TeamMemberRole {
    case object All extends TeamMemberRole
    case object Maintainer extends TeamMemberRole
    case object Member extends TeamMemberRole
  }

}