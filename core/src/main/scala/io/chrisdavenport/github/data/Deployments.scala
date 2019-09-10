package io.chrisdavenport.github.data

import java.time.Instant

import cats.implicits._
import org.http4s.Uri
import org.http4s.circe._
import io.circe._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import cats.Applicative

object Deployments {

  final case class DeploymentCreator(
    id: Int,
    login: String,
    `type`: String,
    siteAdmin: Boolean,
    gravatarId: Option[String],
    uri: Uri,
    htmlUri: Uri,
    organizationsUri: Uri,
    receivedEventsUri: Uri,
    reposUri: Uri,
    subscriptionsUri: Uri,
    avatarUri: Uri
  )
  object DeploymentCreator {

    implicit val deploymentCreatorDecoder = new Decoder[DeploymentCreator] {
      def apply(c: HCursor): Decoder.Result[DeploymentCreator] =
        (
          c.downField("id").as[Int],
          c.downField("login").as[String],
          c.downField("type").as[String],
          c.downField("site_admin").as[Boolean],
          c.downField("gravatar_id").as[Option[String]],
          c.downField("url").as[Uri],
          c.downField("html_url").as[Uri],
          c.downField("organizations_url").as[Uri],
          c.downField("received_events_url").as[Uri],
          c.downField("repos_url").as[Uri],
          c.downField("subscriptions_url").as[Uri],
          c.downField("avatar_url").as[Uri]
        ).mapN(DeploymentCreator.apply)
    }
  }
  
  final case class Deployment(
    id: Int,
    sha: Option[String],
    ref: Option[String],
    task: Option[String],
    payload: Option[Json],
    originalEnvironment: Option[String],
    environment: Option[String],
    description: Option[String],
    creator: Option[DeploymentCreator],
    createdAt: Option[Instant],
    updatedAt: Option[Instant],
    url: Uri,
    statusesUri: Uri,
    repositoryUri: Uri,
    transientEnvironment: Option[Boolean], // an ADT would be good here but API is in preview
    productionEnvironment: Option[Boolean] // an ADT would be good here but API is in preview
  )
  object Deployment {
    implicit val deploymentDecoder = new Decoder[Deployment] {
      def apply(c: HCursor): Decoder.Result[Deployment] =
        (
          c.downField("id").as[Int],
          c.downField("sha").as[Option[String]],
          c.downField("ref").as[Option[String]],
          c.downField("task").as[Option[String]],
          c.downField("payload").as[Option[Json]],
          c.downField("original_environment").as[Option[String]],
          c.downField("environment").as[Option[String]],
          c.downField("description").as[Option[String]],
          c.downField("creator").as[Option[DeploymentCreator]],
          c.downField("created_at").as[Option[Instant]],
          c.downField("updated_at").as[Option[Instant]],
          c.downField("url").as[Uri],
          c.downField("statuses_url").as[Uri],
          c.downField("repository_url").as[Uri],
          c.downField("transient_environment").as[Option[Boolean]],
          c.downField("production_environment").as[Option[Boolean]]
        ).mapN(Deployment.apply)
    }
  }

  sealed trait DeploymentState
  object DeploymentState {
    final case object Error extends DeploymentState
    final case object Failure extends DeploymentState
    final case object Inactive extends DeploymentState
    final case object InProgress extends DeploymentState
    final case object Queued extends DeploymentState
    final case object Pending extends DeploymentState
    final case object Success extends DeploymentState
    
    implicit val deploymentCreatorDecoder = new Decoder[DeploymentState] {
      def apply(c: HCursor): Decoder.Result[DeploymentState] =
        c.as[String].flatMap {
          case "error" => Error.asRight
          case "failure" => Failure.asRight
          case "inactive" => Inactive.asRight
          case "in_progress" => InProgress.asRight
          case "queued" => Queued.asRight
          case "pending" => Pending.asRight
          case "success" => Success.asRight
          case s => Left(DecodingFailure(s"Invalid deployment state $s", c.history))
        }
    }

    implicit val deploymentStateEncoder = new Encoder[DeploymentState] {
      def apply(a: DeploymentState): Json = a match {
        case Error => "error".asJson
        case Failure => "failure".asJson
        case Inactive => "inactive".asJson
        case InProgress => "in_progress".asJson
        case Queued => "queued".asJson
        case Pending => "pending".asJson
        case Success => "success".asJson
      }
    }
  }
  
  final case class DeploymentStatus(
    id: Int,
    url: Uri,
    state: DeploymentState,
    creator: DeploymentCreator,
    description: Option[String],
    environment: Option[String],
    createdAt: Instant,
    updatedAt: Instant,
    deploymentUri: Uri,
    repositoryUri: Uri,
    targetUri: Option[Uri],
    environmentUri: Option[Uri],
    logUri: Option[Uri]
  )
  object DeploymentStatus {

    implicit val deploymentStatusDecoder = new Decoder[DeploymentStatus] {
      def apply(c: HCursor): Decoder.Result[DeploymentStatus] =
        (
          c.downField("id").as[Int],
          c.downField("url").as[Uri],
          c.downField("state").as[DeploymentState],
          c.downField("creator").as[DeploymentCreator],
          c.downField("description").as[Option[String]],
          c.downField("environment").as[Option[String]],
          c.downField("created_at").as[Instant],
          c.downField("updated_at").as[Instant],
          c.downField("deployment_url").as[Uri],
          c.downField("repository_url").as[Uri],
          c.downField("target_url").as[Option[Uri]],
          c.downField("environment_url").as[Option[Uri]],
          c.downField("log_url").as[Option[Uri]]
        ).mapN(DeploymentStatus.apply)
    }
  }

  final case class NewDeployment(
    ref: String,
    environment: Option[String],
    description: Option[String],
    task: Option[String],
    autoMerge: Option[Boolean],
    requiredContexts: Option[List[String]],
    payload: Option[Json],
    transientEnvironment: Option[Boolean],
    productionEnvironment: Option[Boolean]
  )
  object NewDeployment {
    def create(ref: String) = NewDeployment(ref, None, None, None, None, None, None, None, None)

    implicit val newDeploymentEncoder = new Encoder[NewDeployment] {
      def apply(a: NewDeployment): Json = Json.obj(
        "ref" -> a.ref.asJson,
        "description" -> a.description.asJson,
        "environment" -> a.environment.asJson,
        "task" -> a.task.asJson,
        "auto_merge" -> a.autoMerge.asJson,
        "required_contexts" -> a.requiredContexts.asJson,
        "payload" -> a.payload.asJson,
        "transient_environment" -> a.transientEnvironment.asJson,
        "production_environment" -> a.productionEnvironment.asJson
      ).dropNullValues
    }

    implicit def newDeploymentEntityEncoder[F[_]: Applicative] = {
      val json = jsonEncoderOf[F, NewDeployment]

      new EntityEncoder[F, NewDeployment] {
        def headers: Headers =
          Headers.of(
            Header("Accept", "application/vnd.github.ant-man-preview+json")
          ) ++ json.headers

        def toEntity(a: NewDeployment): Entity[F] = json.toEntity(a)
      }
    }
  }

  final case class NewDeploymentStatus(
    state: DeploymentState,
    environment: Option[String],
    environmentUri: Option[Uri],
    targetUri: Option[Uri],
    logUri: Option[Uri],
    description: Option[String],
    autoInactive: Option[Boolean]
  )
  object NewDeploymentStatus {
    def create(state: DeploymentState) = NewDeploymentStatus(state, None, None, None, None, None, None)

    implicit val newDeploymentStatusEncoder = new Encoder[NewDeploymentStatus] {
      def apply(a: NewDeploymentStatus): Json = Json.obj(
        "state" -> a.state.asJson,
        "environment" -> a.environment.asJson,
        "environment_url" -> a.environmentUri.asJson,
        "target_url" -> a.targetUri.asJson,
        "log_url" -> a.logUri.asJson,
        "description" -> a.description.asJson,
        "auto_inactive" -> a.autoInactive.asJson
      ).dropNullValues
    }

    implicit def newDeploymentStatusEntityEncoder[F[_]: Applicative] = {
      val json = jsonEncoderOf[F, NewDeploymentStatus]

      new EntityEncoder[F, NewDeploymentStatus] {
        def headers: Headers =
          Headers.of(
            Header("Accept", "application/vnd.github.ant-man-preview+json")
          ) ++ json.headers

        def toEntity(a: NewDeploymentStatus): Entity[F] = json.toEntity(a)
      }
    }
  }
}