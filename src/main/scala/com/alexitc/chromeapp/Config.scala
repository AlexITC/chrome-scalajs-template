package com.alexitc.chromeapp

import com.alexitc.chromeapp.activetab.ActiveTabConfig
import com.alexitc.chromeapp.background.alarms.AlarmRunner

/** This is the global config, which includes any configurable details.
  *
  * For convenience, there are two configs, the Default one and the one for Development.
  */
case class Config(
    alarmRunnerConfig: AlarmRunner.Config,
    activeTabConfig: activetab.ActiveTabConfig
)

// TODO: REPLACE ME
object Config {

  val Default: Config = {
    Config(
      AlarmRunner.Config(periodInMinutes = 60 * 3),
      ActiveTabConfig(
        com.alexitc.BuildInfo.activeTabWebsiteScripts.toList
      )
    )
  }

  val Dev: Config = Default.copy(alarmRunnerConfig = AlarmRunner.Config(periodInMinutes = 2))
}
