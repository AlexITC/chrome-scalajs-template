package com.alexitc.chromeapp.popup

import com.alexitc.chromeapp.background.BackgroundAPI
import com.alexitc.chromeapp.common.I18NMessages
import org.scalajs.dom._

class Runner(messages: I18NMessages, backgroundAPI: BackgroundAPI) {

  def run(): Unit = {
    log("This was run by the popup script")
    document.onreadystatechange = _ => {
      if (document.readyState == "interactive") {
        document
          .getElementById("popup-view-id")
          .innerHTML = s"<p>${messages.appName}!!!</p>"
      }
    }
    backgroundAPI.sendBrowserNotification(messages.appName, "I'm on the Pop-up")
  }

  private def log(msg: String): Unit = {
    println(s"popup: $msg")
  }
}

object Runner {

  def apply(): Runner = {
    val messages = new I18NMessages
    val backgroundAPI = new BackgroundAPI()
    new Runner(messages, backgroundAPI)
  }
}
