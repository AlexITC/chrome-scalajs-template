package com.alexitc.chromeapp.activetab.models

import io.circe.Decoder
import io.circe.generic.auto._
import io.circe.parser.parse

import java.util.UUID
import scala.util.Try

/** In our protocol (Command/Event), the messages are sent through unidirectional channels, hence, we tag the messages
  * to allow matching a command with its produced event.
  *
  * @param tag
  *   a unique tag
  * @param model
  *   the model
  */
case class TaggedModel[T: Decoder](tag: UUID, model: T)

object TaggedModel {
  def decode[T: Decoder](string: String): Try[TaggedModel[T]] = {
    parse(string).toTry
      .flatMap(_.as[TaggedModel[T]].toTry)
  }
}
