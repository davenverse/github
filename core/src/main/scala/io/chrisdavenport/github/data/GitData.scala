package io.chrisdavenport.github.data

import cats.implicits._
import org.http4s._
import org.http4s.circe._
import io.circe._
import io.circe.syntax._
import java.time.Instant

object GitData {
  sealed trait Encoding extends Product with Serializable
  object Encoding {
    case object Base64 extends Encoding
    case object Utf8 extends Encoding

    implicit val encoder = new Encoder[Encoding]{
      def apply(a: Encoding): Json = a match {
        case Base64 => "base64".asJson
        case Utf8 => "utf-8".asJson
      }
    }
  }

  final case class Blob(
    content: String, // Base64 encoded, Caution Supports up to 100megabytes in size
    uri: Uri,
    sha: String,
    size: Int
  )
  object Blob {
    implicit val decoder = new Decoder[Blob]{
      def apply(c: HCursor): Decoder.Result[Blob] = 
        (
          c.downField("content").as[String],
          c.downField("url").as[Uri],
          c.downField("sha").as[String],
          c.downField("size").as[Int]
        ).mapN(Blob.apply)
    }
  }

  final case class CreateBlob(
    content: String,
    encoding: Encoding
  )

  object CreateBlob {
    implicit val encoder = new Encoder[CreateBlob]{
      def apply(a: CreateBlob): Json = Json.obj(
        "content" -> a.content.asJson,
        "encoding" -> a.encoding.asJson
      )
    }
  }

  final case class NewBlob(
    uri: Uri,
    sha: String
  )
  object NewBlob {
    implicit val decoder = new Decoder[NewBlob]{
      def apply(c: HCursor): Decoder.Result[NewBlob] = 
        (
          c.downField("url").as[Uri],
          c.downField("sha").as[String]
        ).mapN(NewBlob.apply)
    }
  }


  sealed trait GitObjectType extends Product with Serializable
  object GitObjectType {
    case object Blob extends GitObjectType
    case object Commit extends GitObjectType
    case object Tree extends GitObjectType

    implicit val decoder = new Decoder[GitObjectType]{
      def apply(c: HCursor): Decoder.Result[GitObjectType] =
        c.as[String].flatMap{
          case "blob" => Right(Blob)
          case "commit" => Right(Commit)
          case "tree" => Right(Tree)
          case other => DecodingFailure(s"GitTreeType got: $other", c.history).asLeft
        }
    }

    implicit val encoder = new Encoder[GitObjectType]{
      def apply(a: GitObjectType): Json = a match {
        case Blob => Json.fromString("blob")
        case Commit => Json.fromString("commit")
        case Tree => Json.fromString("tree")
      }
    }

  }

  sealed trait GitMode extends Product with Serializable
  object GitMode {
    case object Executable extends GitMode
    case object File extends GitMode    
    case object Subdirectory extends GitMode
    case object Submodule extends GitMode
    case object Symlink extends GitMode

    implicit val decoder = new Decoder[GitMode]{
      def apply(c: HCursor): Decoder.Result[GitMode] = c.as[String].flatMap{
        case "100755" => Right(Executable)
        case "100644" => Right(File)
        case "040000" => Right(Subdirectory)
        case "160000" => Right(Submodule)
        case "120000" => Right(Symlink)
        case other =>  DecodingFailure(s"GitMode got: $other", c.history).asLeft
      }
    }

    implicit val encoder = new Encoder[GitMode]{
      def apply(a: GitMode): Json = a match {
        case Executable => Json.fromString("100755")
        case File => Json.fromString("100644")
        case Subdirectory => Json.fromString("040000")
        case Submodule => Json.fromString("160000")
        case Symlink => Json.fromString("120000")
      }
    }

  }

  final case class GitTree(
    path: String,
    sha: String,
    `type`: GitObjectType,
    mode: GitMode,
    uri: Option[Uri],
    size: Option[Int],
  )
  object GitTree {
    implicit val decoder = new Decoder[GitTree]{
      def apply(c: HCursor): Decoder.Result[GitTree] = 
        (
          c.downField("path").as[String],
          c.downField("sha").as[String],
          c.downField("type").as[GitObjectType],
          c.downField("mode").as[GitMode],
          c.downField("url").as[Option[Uri]],
          c.downField("size").as[Option[Int]]
        ).mapN(GitTree.apply)
    }
  }

  final case class Tree(
    sha: String,
    uri: Uri,
    gitTrees: List[GitTree],
    truncated: Option[Boolean]
  )
  object Tree {
    implicit val decoder = new Decoder[Tree]{
      def apply(c: HCursor): Decoder.Result[Tree] = 
        (
          c.downField("sha").as[String],
          c.downField("url").as[Uri],
          c.downField("tree").as[List[GitTree]],
          c.downField("truncated").as[Option[Boolean]]
        ).mapN(Tree.apply)
    }
  }



  sealed trait CreateGitTree extends Product with Serializable
  object CreateGitTree {

    def fromGitTree(g: GitTree): CreateGitTree = 
      CreateGitTreeSha(
        g.path,
        g.sha.some,
        g.`type`,
        g.mode
      )

    final case class CreateGitTreeSha(
      path: String,
      sha: Option[String],
      `type`: GitObjectType,
      mode: GitMode
    ) extends CreateGitTree
    
    final case class CreateGitTreeBlob(
      path: String,
      content: String,
      mode: Either[GitMode.Executable.type, GitMode.File.type],
    ) extends CreateGitTree

    implicit val encoder = new Encoder[CreateGitTree]{
      def apply(a: CreateGitTree): Json = a match {
        case CreateGitTreeSha(path, sha, typ, mode) => 
          Json.obj(
            "path" -> path.asJson,
            "sha" -> sha.asJson,
            "type" -> typ.asJson,
            "mode" -> mode.asJson
          )
        case CreateGitTreeBlob(path, content, mode) => 
          Json.obj(
            "path" -> path.asJson,
            "type" -> (GitObjectType.Blob : GitObjectType).asJson,
            "mode" -> mode.merge.asJson,
            "content" -> content.asJson
          )
        
      }
    }
  }

  /**
   * The tree creation API accepts nested entries. 
   * If you specify both a tree and a nested path modifying that tree,
   * this endpoint will overwrite the contents of the tree with the new path contents,
   * and create a new tree structure.
   * 
   * If you use this endpoint to add, delete, or modify the file contents in a tree,
   *  you will need to commit the tree and then update a branch to point to the commit.
   *  For more information see "Create a commit" and "Update a reference."
   * 
   * POST /repos/:owner/:repo/git/trees
   * 
   * @param tree Objects specifying the tree structure
   * @param baseTreeSha The SHA1 of the tree you want to update with new data. 
   *   If you don't set this, the commit will be created on top of everything;
   *   however, it will only contain your change, the rest of your files will show up as deleted.
   **/
  final case class CreateTree(
    tree: List[CreateGitTree],
    baseTreeSha: Option[String],
  )
  object CreateTree {
    implicit val encoder = new Encoder[CreateTree]{
      def apply(a: CreateTree): Json = 
        Json.obj(
          "tree" -> a.tree.asJson,
          "base_tree" -> a.baseTreeSha.asJson
        )
    }
  }


  final case class GitUser(
    name: String,
    email: String,
    date: Instant
  )
  object GitUser {
    implicit val decoder = new Decoder[GitUser]{
      def apply(c: HCursor): Decoder.Result[GitUser] = 
        (
          c.downField("name").as[String],
          c.downField("email").as[String],
          c.downField("date").as[Instant]
        ).mapN(GitUser.apply)
    }
    implicit val encoder = new Encoder[GitUser]{
      def apply(a: GitUser): Json = Json.obj(
        "name" -> a.name.asJson,
        "email" -> a.email.asJson,
        "date" -> a.date.asJson
      )
    }
  }

  final case class CommitTree(
    sha: String,
    uri: Uri
  )

  object CommitTree {
    implicit val decoder = new Decoder[CommitTree]{
      def apply(c: HCursor): Decoder.Result[CommitTree] = 
        (
          c.downField("sha").as[String],
          c.downField("url").as[Uri]
        ).mapN(CommitTree.apply)
    }
  }

  final case class GitCommit(
    message: String,
    uri: Uri,
    committer: GitUser,
    author: GitUser,
    sha: String,
    tree: CommitTree,
    parents: List[CommitTree]
  )
  object GitCommit {
    implicit val decoder = new Decoder[GitCommit]{
      def apply(c: HCursor): Decoder.Result[GitCommit] = 
        (
          c.downField("message").as[String],
          c.downField("url").as[Uri],
          c.downField("committer").as[GitUser],
          c.downField("author").as[GitUser],
          c.downField("sha").as[String],
          c.downField("tree").as[CommitTree],
          c.downField("parents").as[List[CommitTree]],
        ).mapN(GitCommit.apply)
    }
  }

  final case class CreateCommit(
    message: String,
    treeSha: String,
    parents: List[String],
    author: Option[GitUser],
    committer: Option[GitUser],
    signature: Option[String]
  )
  object CreateCommit {
    def simple(message: String, treeSha: String, parents: List[String]): CreateCommit =
      CreateCommit(message, treeSha, parents, None, None, None)

    implicit val encoder = new Encoder[CreateCommit]{
      def apply(a: CreateCommit): Json = Json.obj(
        "message" -> a.message.asJson,
        "tree" -> a.treeSha.asJson,
        "parents" -> a.parents.asJson,
        "author" -> a.author.asJson,
        "committer" -> a.committer.asJson,
        "signature" -> a.signature.asJson
      ).dropNullValues
    }
  }

  final case class GitObject(
    `type`: GitObjectType, 
    sha: String,
    uri: Uri
  )
  object GitObject {
    implicit val decoder = new Decoder[GitObject]{
      def apply(c: HCursor): Decoder.Result[GitObject] = 
        (
          c.downField("type").as[GitObjectType],
          c.downField("sha").as[String],
          c.downField("url").as[Uri]
        ).mapN(GitObject.apply)
    }
  }

  final case class GitReference(
    ref: String,
    uri: Uri,
    `object`: GitObject
  )
  object GitReference{
    implicit val decoder = new Decoder[GitReference]{
      def apply(c: HCursor): Decoder.Result[GitReference] =
        (
          c.downField("ref").as[String],
          c.downField("url").as[Uri],
          c.downField("object").as[GitObject]
        ).mapN(GitReference.apply)
    }
  }

  final case class CreateReference(
    ref: String,
    sha: String
  )
  object CreateReference {
    implicit val encoder = new Encoder[CreateReference]{
      def apply(a: CreateReference): Json = Json.obj(
        "ref" -> a.ref.asJson,
        "sha" -> a.sha.asJson
      )
    }
  }

  final case class UpdateReference(
    sha: String,
    force: Boolean
  )
  object UpdateReference {
    implicit val encoder = new Encoder[UpdateReference]{
      def apply(a: UpdateReference): Json = Json.obj(
        "sha" -> a.sha.asJson,
        "force" -> a.force.asJson
      )
    }
  }

  final case class GitTag(
    tag: String,
    sha: String,
    uri: Uri,
    message: String,
    tagger: GitUser,
    `object`: GitObject
  )
  object GitTag {
    implicit val decoder = new Decoder[GitTag]{
      def apply(c: HCursor): Decoder.Result[GitTag] = 
        (
          c.downField("tag").as[String],
          c.downField("sha").as[String],
          c.downField("url").as[Uri],
          c.downField("message").as[String],
          c.downField("tagger").as[GitUser],
          c.downField("object").as[GitObject]
        ).mapN(GitTag.apply)
    }
  }

  final case class CreateTag(
    tag: String,
    message: String,
    objectSha: String,
    `type`: GitObjectType,
    tagger: Option[GitUser]
  )
  object CreateTag {
    implicit val encoder = new Encoder[CreateTag]{
      def apply(a: CreateTag): Json = Json.obj(
        "tag" -> a.tag.asJson,
        "message" -> a.message.asJson,
        "object" -> a.objectSha.asJson,
        "type" -> a.`type`.asJson,
        "tagger" -> a.tagger.asJson
      ).dropNullValues
    }
  }








}