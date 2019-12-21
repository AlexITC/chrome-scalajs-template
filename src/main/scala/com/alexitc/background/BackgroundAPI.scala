package com.alexitc.background

import com.alexitc.background.models.{Command, Event}
import io.circe.syntax._

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.scalajs.js
import scala.util.{Failure, Success, Try}

/**
 * There are some APIs that can be accessed only from the background runner, like http/storage/notifications/etc.
 *
 * A way to call these ones from other contexts is to send a message to the background.
 *
 * A request/response mechanism can be simulated by using promises/futures.
 *
 * Any operation is encoded as JSON and parsed on the background context, which runs the actual operation
 * and returns the result as another message.
 *
 * The BackgroundAPI abstracts all that complex logic from the caller and gives a simple API based on futures.
 */
class BackgroundAPI(implicit ec: ExecutionContext) {

  def sendBrowserNotification(title: String, message: String): Future[Unit] = {
    val command: Command = Command.SendBrowserNotification(title, message)
    process(command).collect {
      case _: Event.BrowserNotificationSent => ()
    }
  }

  private def process(command: Command): Future[Event] = {
    val promise = Promise[Event]
    val callback: js.Function1[js.Object, Unit] = (x: js.Object) => {
      Try(x.asInstanceOf[String]).flatMap(Event.decode) match {
        case Success(Event.CommandRejected(reason)) =>
          sendBrowserNotification("ERROR", reason) // TODO: Remove hack and let the caller handle errors
          promise.failure(new RuntimeException(reason))

        case Success(e: Event) =>
          promise.success(e)

        // Unable to parse the incoming message, it's likely the message wasn't sent by our app, no need to process it.
        case Failure(exception) => promise.failure(exception)
      }
    }

    val message = command.asJson.noSpaces
    chrome.runtime.Runtime
      .sendMessage(message = message, responseCallback = callback)

    promise.future
  }
}
