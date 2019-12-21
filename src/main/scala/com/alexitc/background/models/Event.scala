package com.alexitc.background.models

import io.circe.generic.auto._
import io.circe.parser.parse
import io.circe.syntax._
import io.circe.{Decoder, DecodingFailure, Encoder, Json}

import scala.util.Try

/**
 * Internal typed-message used by the background context to reply to an operation
 */
private[background] sealed trait Event extends Product with Serializable

private[background] object Event {

  final case class BrowserNotificationSent() extends Event
  // TODO: Find a better way, possible returning something like Either[CommandRejected, Event] on the BackgroundAPI
  final case class CommandRejected(reason: String) extends Event

  implicit val encoder: Encoder[Event] = Encoder.instance { event =>
    val (tpe, json) = event match {
      case e: BrowserNotificationSent =>
        "BrowserNotificationSent" -> e.asJson
      case e: CommandRejected => "CommandRejected" -> e.asJson
    }

    Json.obj("type" -> tpe.asJson, "value" -> json)
  }

  implicit val decoder: Decoder[Event] = Decoder.instance { json =>
    for {
      tpe <- json.get[String]("type")
      value <- json.get[Json]("value")
      cmd <- tpe match {
        case "BrowserNotificationSent" => value.as[BrowserNotificationSent]
        case "CommandRejected" => value.as[CommandRejected]
        case _ =>
          Left(
            DecodingFailure
              .fromThrowable(new RuntimeException("Unknown Event"), List.empty)
          )
      }
    } yield cmd
  }

  def decode(string: String): Try[Event] = {
    parse(string).toTry
      .flatMap(_.as[Event].toTry)
  }
}
