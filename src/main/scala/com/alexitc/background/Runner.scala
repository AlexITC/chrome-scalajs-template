package com.alexitc.background

import com.alexitc.Config
import com.alexitc.background.alarms.AlarmRunner
import com.alexitc.background.models.{Command, Event}
import com.alexitc.background.services.browser.BrowserNotificationService
import com.alexitc.background.services.http.HttpService
import com.alexitc.background.services.storage.StorageService
import com.alexitc.common.I18NMessages
import io.circe.syntax._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try
import scala.util.control.NonFatal

class Runner(
    commandProcessor: CommandProcessor,
    alarmRunner: AlarmRunner
)(implicit ec: ExecutionContext) {

  def run(): Unit = {
    log("This was run by the background script")
    alarmRunner.register()
    processExternalMessages()
  }

  /**
   * Enables the future-based communication between contexts to the background contexts.
   *
   * Internally, this is done by string-based messages, which we encode as JSON.
   */
  private def processExternalMessages(): Unit = {
    chrome.runtime.Runtime.onMessage.listen { message =>
      message.value.foreach { any =>
        val response = Future
          .fromTry { Try(any.asInstanceOf[String]).flatMap(Command.decode) }
          .map { cmd =>
            log(s"Got command = $cmd")
            cmd
          }
          .flatMap(commandProcessor.process)
          .recover {
            case NonFatal(ex) =>
              log(s"Failed to process command, error = ${ex.getMessage}")
              Event.CommandRejected(ex.getMessage)
          }
          .map(_.asJson.noSpaces)

        /**
         * NOTE: When replying on futures, the method returning an async response is the only reliable one
         * otherwise, the sender is getting no response, a way to use the async method is to pass a response
         * in case of failures even if that case was already handled with the CommandRejected event.
         */
        message.response(response, "Impossible failure")
      }
    }
  }

  private def log(msg: String): Unit = {
    println(s"background: $msg")
  }
}

object Runner {

  def apply(config: Config)(implicit ec: ExecutionContext): Runner = {
    val storage = new StorageService
    val http = HttpService(config.httpConfig)
    val messages = new I18NMessages
    val browserNotificationService = new BrowserNotificationService(messages)
    val commandProcessor =
      new CommandProcessor(storage, browserNotificationService)

    val productUpdaterAlarm = new AlarmRunner(
      config.alarmRunnerConfig,
      messages,
      browserNotificationService
    )
    new Runner(commandProcessor, productUpdaterAlarm)
  }
}
