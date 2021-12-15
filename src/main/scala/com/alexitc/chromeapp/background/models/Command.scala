package com.alexitc.chromeapp.background.models

import io.circe.generic.auto._
import io.circe.parser.parse

import scala.util.Try

/** Internal typed-message to request the background context to perform an operation.
  */
private[background] sealed trait Command extends Product with Serializable

private[background] object Command {

  final case class SendBrowserNotification(title: String, message: String) extends Command

  def decode(string: String): Try[Command] = {
    parse(string).toTry
      .flatMap(_.as[Command].toTry)
  }
}
