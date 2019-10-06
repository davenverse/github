package io.chrisdavenport.github.data

sealed trait Order

object Order {

  case object Ascending extends Order
  case object Descending extends Order

  def toOptionalParam(order: Order): Option[String] =
    order match {
      case Ascending => Some("asc")
      case Descending => Some("desc")
    }

}
