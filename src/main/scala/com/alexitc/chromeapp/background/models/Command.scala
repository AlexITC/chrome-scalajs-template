package com.alexitc.chromeapp.background.models

import io.circe.generic.auto._
import io.circe.parser.parse
import io.circe.syntax._
import io.circe.{Decoder, DecodingFailure, Encoder, Json}

import scala.util.Try

/**
 * Internal typed-message to request the background context to perform an operation.
 */
private[background] sealed trait Command extends Product with Serializable

private[background] object Command {

  final case class SendBrowserNotification(title: String, message: String) extends Command

  implicit val encoder: Encoder[Command] = Encoder.instance { cmd =>
    val (tpe, json) = cmd match {
      case cmd: SendBrowserNotification =>
        "SendBrowserNotification" -> cmd.asJson
    }

    Json.obj("type" -> tpe.asJson, "value" -> json)
  }

  implicit val decoder: Decoder[Command] = Decoder.instance { json =>
    for {
      tpe <- json.get[String]("type")
      value <- json.get[Json]("value")
      cmd <- tpe match {
        case "SendBrowserNotification" => value.as[SendBrowserNotification]
        case _ =>
          Left(
            DecodingFailure.fromThrowable(
              new RuntimeException("Unknown Command"),
              List.empty
            )
          )
      }
    } yield cmd
  }

  def decode(string: String): Try[Command] = {
    parse(string).toTry
      .flatMap(_.as[Command].toTry)
  }
}
