package com.alexitc.chromeapp.activetab

import com.alexitc.chromeapp.Config
import com.alexitc.chromeapp.background.BackgroundAPI
import com.alexitc.chromeapp.common.I18NMessages

import scala.concurrent.ExecutionContext

class Runner(config: ActiveTabConfig, backgroundAPI: BackgroundAPI, messages: I18NMessages)(
    implicit ec: ExecutionContext
) {

  def run(): Unit = {
    log("This was run by the active tab")
    backgroundAPI.sendBrowserNotification(messages.appName, "I'm on the tab!!")
  }

  private def log(msg: String): Unit = {
    println(s"activeTab: $msg")
  }
}

object Runner {

  def apply(config: Config)(implicit ec: ExecutionContext): Runner = {
    val backgroundAPI = new BackgroundAPI
    val messages = new I18NMessages
    new Runner(config.activeTabConfig, backgroundAPI, messages)
  }
}
