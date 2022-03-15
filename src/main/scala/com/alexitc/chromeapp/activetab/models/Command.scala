package com.alexitc.chromeapp.activetab.models

import io.circe.generic.auto._
import io.circe.parser.parse

import scala.util.Try

/** This is the internal protocol to allow communicating different contexts to the extension active tba context.
  *
  * This is the request side.
  */
private[activetab] sealed trait Command extends Product with Serializable

private[activetab] object Command {

  final case object GetInfo extends Command

  def decode(string: String): Try[Command] = {
    parse(string).toTry
      .flatMap(_.as[Command].toTry)
  }
}
