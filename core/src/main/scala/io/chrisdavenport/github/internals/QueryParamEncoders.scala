package io.chrisdavenport.github.internals

import org.http4s._

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME

object QueryParamEncoders {
  implicit val zonedDateTime: QueryParamEncoder[ZonedDateTime] =
    QueryParamEncoder[String].contramap(_.format(ISO_OFFSET_DATE_TIME))
}
