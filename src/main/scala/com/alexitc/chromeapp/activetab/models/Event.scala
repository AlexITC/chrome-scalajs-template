package com.alexitc.chromeapp.activetab.models

import io.circe.generic.auto._
import io.circe.parser.parse

import scala.util.Try

/** This is the internal protocol to allow communicating different contexts to the extension active tba context.
  *
  * This is the response side.
  */
private[activetab] sealed trait Event extends Product with Serializable

private[activetab] object Event {

  final case class GotInfo(details: String) extends Event
  // TODO: There is a likely a better way to do this
  final case class CommandRejected(reason: String) extends Event

  def decode(string: String): Try[Event] = {
    parse(string).toTry
      .flatMap(_.as[Event].toTry)
  }
}
