package com.alexitc.chromeapp.background.alarms

import chrome.alarms.bindings.AlarmInfo
import com.alexitc.chromeapp.background.services.browser.BrowserNotificationService
import com.alexitc.chromeapp.common.I18NMessages

/** Example code to register and run a configurable alarm.
  */
private[background] class AlarmRunner(
    config: AlarmRunner.Config,
    messages: I18NMessages,
    notificationService: BrowserNotificationService
) {

  def register(): Unit = {
    val alarmName = "TO_BE_DEFINED"
    chrome.alarms.Alarms.create(alarmName, AlarmInfo(delayInMinutes = 1.0, periodInMinutes = config.periodInMinutes))
    chrome.alarms.Alarms.onAlarm.filter(_.name == alarmName).listen { alarm =>
      log(s"Got alarm: ${alarm.name}")
      run()
    }
  }

  private def run(): Unit = {
    // As this runs on the background, it can use its API directly.
    notificationService.notify("Alarm time!")
  }

  private def log(msg: String): Unit = {
    println(s"alarmRunner: $msg")
  }
}

object AlarmRunner {
  case class Config(periodInMinutes: Double)
}
