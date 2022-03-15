package com.alexitc.chromeapp.activetab

import com.alexitc.chromeapp.activetab.models.{Command, Event, TaggedModel}
import io.circe.Encoder
import io.circe.generic.auto._
import io.circe.syntax._
import org.scalajs.dom

import java.util.UUID
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}

private[activetab] class ExternalMessageProcessor(commandProcessor: CommandProcessor)(implicit ec: ExecutionContext) {

  def start(): Unit = {
    log("listening for external messages")
    dom.window.addEventListener(
      "message", // NOTE: This message type is required to get the message to the extension
      eventHandler,
      useCapture = true
    )
  }

  private val eventHandler = (event: dom.MessageEvent) => {
    // We need to make sure that the event comes from the same website where this
    // content-script is running.
    //
    // Otherwise, other websites/extensions would make our background context believe
    // that the request is from this website content-script while it's not, this
    // content-script is just a proxy to get to the background.
    dom.window.location.origin
      .filter(_ == event.origin)
      .foreach { _ =>
        TaggedModel.decode[Command](event.data.toString) match {
          case Failure(_) =>
            // There is nothing to do when we get an unknown message
            ()

          case Success(value) =>
            commandProcessor
              .process(value.model)
              .onComplete(reply(event.origin, value.tag, _))
        }
      }

  }

  private def reply[T: Encoder](origin: String, tag: UUID, result: Try[T]): Unit = {
    val model = result match {
      case Failure(exception) => Event.CommandRejected(exception.getMessage).asJson
      case Success(value) => value.asJson
    }
    val msg = TaggedModel(tag, model)

    // Ensures only the origin can see the response
    dom.window.postMessage(msg.asJson.noSpaces, origin)
  }

  private def log(msg: String): Unit = {
    println(s"ExternalMessageProcessor: $msg")
  }
}
