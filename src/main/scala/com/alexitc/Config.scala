package com.alexitc

import com.alexitc.activetab.ActiveTabConfig
import com.alexitc.background.alarms.AlarmRunner
import com.alexitc.background.services.http.HttpService

/**
 * This is the global config, which includes any configurable details.
 *
 * For convenience, there are two configs, the Default one and the one for Development.
 */
case class Config(
    httpConfig: HttpService.Config,
    alarmRunnerConfig: AlarmRunner.Config,
    activeTabConfig: activetab.ActiveTabConfig
)

// TODO: REPLACE ME
object Config {

  val Default: Config = {
    Config(
      HttpService.Config(serverUrl = "https://safer.chat/api"),
      AlarmRunner.Config(periodInMinutes = 60 * 3),
      ActiveTabConfig()
    )
  }

  val Dev: Config = {
    Config(
      HttpService.Config(serverUrl = "http://localhost:9000"),
      AlarmRunner.Config(periodInMinutes = 2),
      ActiveTabConfig()
    )
  }
}
