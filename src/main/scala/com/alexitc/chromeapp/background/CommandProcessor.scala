package com.alexitc.chromeapp.background

import com.alexitc.chromeapp.background.models.{Command, Event}
import com.alexitc.chromeapp.background.services.browser.BrowserNotificationService
import com.alexitc.chromeapp.background.services.storage.StorageService

import scala.concurrent.Future

/** Any command supported by the BackgroundAPI is handled here.
  */
private[background] class CommandProcessor(
    productStorage: StorageService,
    browserNotificationService: BrowserNotificationService
) {

  def process(command: Command): Future[Event] = command match {
    case Command.SendBrowserNotification(title, message) =>
      browserNotificationService.notify(title, message)
      Future.successful(Event.BrowserNotificationSent())
  }
}
