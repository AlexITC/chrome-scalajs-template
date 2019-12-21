package com.alexitc.background

import com.alexitc.background.models.{Command, Event}
import com.alexitc.background.services.browser.BrowserNotificationService
import com.alexitc.background.services.storage.StorageService

import scala.concurrent.{ExecutionContext, Future}

/**
 * Any command supported by the BackgroundAPI is handled here.
 */
private[background] class CommandProcessor(
    productStorage: StorageService,
    browserNotificationService: BrowserNotificationService
)(implicit ec: ExecutionContext) {

  def process(command: Command): Future[Event] = command match {
    case Command.SendBrowserNotification(title, message) =>
      browserNotificationService.notify(title, message)
      Future.successful(Event.BrowserNotificationSent())
  }
}
