package com.alexitc.chromeapp.background.models

import io.circe.generic.auto._
import io.circe.parser.parse

import scala.util.Try

/** Internal typed-message used by the background context to reply to an operation
  */
private[background] sealed trait Event extends Product with Serializable

private[background] object Event {

  final case class BrowserNotificationSent() extends Event
  // TODO: Find a better way, possible returning something like Either[CommandRejected, Event] on the BackgroundAPI
  final case class CommandRejected(reason: String) extends Event

  def decode(string: String): Try[Event] = {
    parse(string).toTry
      .flatMap(_.as[Event].toTry)
  }
}
