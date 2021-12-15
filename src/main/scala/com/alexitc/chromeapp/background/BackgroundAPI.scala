package com.alexitc.chromeapp.background

import com.alexitc.chromeapp.background.models.{Command, Event}
import io.circe.generic.auto._
import io.circe.syntax._

import scala.concurrent.{Future, Promise}
import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits._
import scala.scalajs.js
import scala.util.{Failure, Success, Try}

/** There are some APIs that can be accessed only from the background runner, like http/storage/notifications/etc.
  *
  * A way to call these ones from other contexts is to send a message to the background.
  *
  * A request/response mechanism can be simulated by using promises/futures.
  *
  * Any operation is encoded as JSON and parsed on the background context, which runs the actual operation and returns
  * the result as another message.
  *
  * The BackgroundAPI abstracts all that complex logic from the caller and gives a simple API based on futures.
  */
class BackgroundAPI {

  import BackgroundAPI._

  def sendBrowserNotification(title: String, message: String): Future[Unit] = {
    val command: Command = Command.SendBrowserNotification(title, message)
    process(command).collect { case _: Event.BrowserNotificationSent =>
      ()
    }
  }

  /** Processes a command sending a message to the background context, when the background isn't ready, the command is
    * retried up to 3 times, delaying 1 second each time, this retry strategy should be enough for most cases.
    */
  private def process(command: Command): Future[Event] = {
    val timeoutMs = 1000
    def processWithRetries(retriesLeft: Int, lastError: String): Future[Event] = {
      if (retriesLeft <= 0) {
        Future.successful(Event.CommandRejected(lastError))
      } else {
        val promise = Promise[Event]()
        val _ = org.scalajs.dom.window.setTimeout(() => promise.completeWith(processInternal(command)), timeoutMs)

        promise.future
          .recoverWith { case TransientError(e) =>
            log(s"Trying to recover from transient error, retry = $retriesLeft, command = $command, error = $e")
            processWithRetries(retriesLeft - 1, e)
          }
      }
    }

    processInternal(command).recoverWith { case TransientError(e) =>
      log(s"Trying to recover from transient error, command = $command, error = $e")
      processWithRetries(3, e)
    }
  }

  private def processInternal(command: Command): Future[Event] = {
    val promise = Promise[Event]()
    val callback: js.Function1[js.Object, Unit] = (x: js.Object) => {
      // On exceptional cases, the receiver isn't ready, leading to undefined object on the callback
      // One way this happens is when the extension browser-action is opened in a tab when the browser
      // starts, it tried to contact the background which isn't ready.
      //
      // On such cases, the lastError is supposed to include the failure reason but a user claims that
      // sometimes lastError is empty but the message is still undefined.
      //
      // We are handling both cases as a TransientError.
      //
      // This seems to occur only in Firefox, See:
      // - https://bugzilla.mozilla.org/show_bug.cgi?id=1435597
      // - https://discourse.mozilla.org/t/reply-to-chrome-runtime-sendmessage-is-undefined/25021/3
      chrome.runtime.Runtime.lastError
        .flatMap(Option.apply) // Apparently, chrome.runtime.Runtime.lastError could be Option(null)
        .flatMap(_.message.toOption)
        .orElse(
          if (scalajs.js.isUndefined(x)) Some("Got undefined message, receiver likely not ready")
          else None
        )
        .map { errorReason =>
          promise.failure(TransientError(errorReason))
        }
        .getOrElse {
          Try(x.asInstanceOf[String]).flatMap(Event.decode) match {
            case Success(Event.CommandRejected(reason)) =>
              sendBrowserNotification("ERROR", reason) // TODO: Remove hack
              promise.failure(new RuntimeException(reason))

            case Success(e: Event) =>
              promise.success(e)

            // Unable to parse the incoming message, it's likely the message wasn't sent by our app, no need to process it.
            case Failure(exception) => promise.failure(exception)
          }
        }
    }

    val message = command.asJson.noSpaces
    chrome.runtime.Runtime
      .sendMessage(message = message, responseCallback = callback)

    promise.future
  }

  private def log(msg: String): Unit = {
    println(s"BackgroundAPI: $msg")
  }
}

object BackgroundAPI {
  case class TransientError(message: String) extends RuntimeException
}
