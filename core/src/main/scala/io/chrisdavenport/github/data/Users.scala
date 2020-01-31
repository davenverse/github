package io.chrisdavenport.github.data

import cats.implicits._
import cats.effect._
import org.http4s.Uri
import org.http4s.circe._
import java.time.ZonedDateTime

import io.circe._

object Users {

  sealed trait OwnerType
  object OwnerType {
    object User extends OwnerType
    object Organization extends OwnerType

    implicit val ownerTypeDecoder = new Decoder[OwnerType]{
      def apply(c: HCursor): Decoder.Result[OwnerType] =
        c.as[String].flatMap{
          case "User" => User.pure[Decoder.Result]
          case "Organization" => Organization.pure[Decoder.Result]
        }
    }
  }

  //
  // Simple Sections
  //
  //

  sealed trait SimpleOwner{
    val id: Int
    val login: String
    val uri: Uri
    val avatarUri: Uri
  }
  final case class SimpleUser(
    id: Int,
    login: String,
    uri: Uri,
    avatarUri: Uri
  ) extends SimpleOwner
  object SimpleUser {
    implicit val simpleUserDecoder = new Decoder[SimpleUser]{
      def apply(c: HCursor): Decoder.Result[SimpleUser] =
        (
          c.downField("id").as[Int],
          c.downField("login").as[String],
          c.downField("url").as[Uri],
          c.downField("avatar_url").as[Uri]
        ).mapN(SimpleUser.apply)
    }
  }

  final case class SimpleOrganization(
    id: Int,
    login: String,
    uri: Uri,
    avatarUri: Uri,
  ) extends SimpleOwner
  object SimpleOrganization {
    implicit val simpleOrganizationDecoder = new Decoder[SimpleOrganization]{
      def apply(c: HCursor): Decoder.Result[SimpleOrganization] =
      (
        c.downField("id").as[Int],
        c.downField("login").as[String],
        c.downField("url").as[Uri],
        c.downField("avatar_url").as[Uri]
      ).mapN(SimpleOrganization.apply)
    }
  }

  object SimpleOwner {
    implicit val simpleOwnerDecoder = new Decoder[SimpleOwner]{
      def apply(c: HCursor): Decoder.Result[SimpleOwner] =
        c.downField("type").as[OwnerType].flatMap{
          case OwnerType.Organization =>
            SimpleUser.simpleUserDecoder(c)
          case OwnerType.User =>
            SimpleOrganization.simpleOrganizationDecoder(c)
        }
    }
  }

  sealed trait Owner
  final case class User(
    id: Int,
    login: String,
    name: Option[String],
    email: Option[String],
    company: Option[String],
    createdAt: Option[ZonedDateTime],
    blog: Option[String],
    location: Option[String],
    bio: Option[String],
    hireable: Option[Boolean],
    publicRepos: Option[Int],
    publicGists: Option[Int],
    followers: Option[Int],
    following: Option[Int],
    uri: Uri,
    htmlUri: Uri,
    avatarUri: Uri,
  ) extends Owner
  object User {
    implicit val userDecoder = new Decoder[User]{
      def apply(c: HCursor): Decoder.Result[User] =
        (
          c.downField("id").as[Int],
          c.downField("login").as[String],
          c.downField("name").as[Option[String]],
          c.downField("email").as[Option[String]],
          c.downField("company").as[Option[String]],
          c.downField("created_at").as[Option[ZonedDateTime]],
          c.downField("blog").as[Option[String]],
          c.downField("location").as[Option[String]],
          c.downField("bio").as[Option[String]],
          c.downField("hireable").as[Option[Boolean]],
          c.downField("public_repos").as[Option[Int]],
          c.downField("public_gists").as[Option[Int]],
          c.downField("followers").as[Option[Int]],
          c.downField("following").as[Option[Int]],
          c.downField("url").as[Uri],
          c.downField("html_url").as[Uri],
          c.downField("avatar_url").as[Uri]
        ).mapN(User.apply)
    }
    implicit def userEntityDecoder[F[_]: Sync] = jsonDecoder[F]
  }

  final case class Organization(
    id: Int,
    login: String,
    name: Option[String],
    email: Option[String],
    company: Option[String],
    createdAt: ZonedDateTime,
    blog: Option[String],
    location: Option[String],
    publicRepos: Int,
    publicGists: Int,
    followers: Int,
    following: Int,
    uri: Uri,
    htmlUri: Uri,
    avatarUri: Uri,
  ) extends Owner
  object Organization {
    implicit val organizationDecoder = new Decoder[Organization]{
      def apply(c: HCursor): Decoder.Result[Organization] =
        (
          c.downField("id").as[Int],
          c.downField("login").as[String],
          c.downField("name").as[Option[String]],
          c.downField("email").as[Option[String]],
          c.downField("company").as[Option[String]],
          c.downField("created_at").as[ZonedDateTime],
          c.downField("blog").as[Option[String]],
          c.downField("location").as[Option[String]],
          c.downField("public_repos").as[Int],
          c.downField("public_gists").as[Int],
          c.downField("followers").as[Int],
          c.downField("following").as[Int],
          c.downField("url").as[Uri],
          c.downField("html_url").as[Uri],
          c.downField("avatar_url").as[Uri]
        ).mapN(Organization.apply)
    }
  }
  object Owner {
    implicit val ownerDecoder = new Decoder[Owner]{
      def apply(c: HCursor): Decoder.Result[Owner] =
        c.downField("type").as[OwnerType].flatMap{
          case OwnerType.User =>
            User.userDecoder(c)
          case OwnerType.Organization =>
            Organization.organizationDecoder(c)
        }
    }
  }




}
