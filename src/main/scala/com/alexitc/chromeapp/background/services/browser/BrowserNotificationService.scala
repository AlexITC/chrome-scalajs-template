package com.alexitc.chromeapp.background.services.browser

import com.alexitc.chromeapp.common.{I18NMessages, ResourceProvider}
import com.alexitc.chromeapp.facades.CommonsFacade

/** Internal service available to the background context, which allows sending notifications to the browser.
  */
private[background] class BrowserNotificationService(messages: I18NMessages) {

  def notify(message: String): Unit = {
    notify(messages.appName, message)
  }

  def notify(title: String, message: String): Unit = {

    /** Sadly, scala-js-chrome fails when creating notifications on firefox, to overcome that issue, we expect a simple
      * JavaScript function that creates notifications which works on Firefox and Chrome, the facade just invoke that
      * function (see common.js)
      *
      * If you don't need to support firefox, replacing this with chrome.notifications.Notifications.create() is
      * simpler.
      */
    CommonsFacade.notify(
      title,
      message,
      ResourceProvider.appIcon96
    )
  }
}
