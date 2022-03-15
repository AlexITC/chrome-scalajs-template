package com.alexitc.chromeapp.activetab

import com.alexitc.chromeapp.Config
import com.alexitc.chromeapp.background.BackgroundAPI
import com.alexitc.chromeapp.common.I18NMessages
import com.alexitc.chromeapp.facades.SweetAlert
import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits._

import scala.concurrent.Future
import scala.scalajs.js.JSConverters._

class Runner(
    config: ActiveTabConfig,
    backgroundAPI: BackgroundAPI,
    messages: I18NMessages,
    scriptInjector: ScriptInjector,
    externalMessageProcessor: ExternalMessageProcessor
) {

  def run(): Unit = {
    log("This was run by the active tab")
    externalMessageProcessor.start()
    injectPrivilegedScripts(config.websiteScripts)
      .foreach { _ =>
        log("Scripts injected, the website context should start soon")
      }

    SweetAlert(new SweetAlert.Options {
      title = messages.appName
      text = "Do you like this template?"
      icon = chrome.runtime.Runtime.getURL("icons/96/app.png")
      buttons = Option(List("No", "Yes").toJSArray).orUndefined
    }).toFuture.onComplete { t =>
      log(s"SweetAlert result: $t")
    }

    backgroundAPI.sendBrowserNotification(messages.appName, "I'm on the tab!!")
  }

  private def injectPrivilegedScripts(scripts: Seq[String]): Future[Unit] = {
    // it's important to load the scripts in the right order
    scripts.foldLeft(Future.unit) { case (acc, cur) =>
      acc.flatMap(_ => scriptInjector.injectPrivilegedScript(cur))
    }
  }

  private def log(msg: String): Unit = {
    println(s"activeTab: $msg")
  }
}

object Runner {

  def apply(config: Config): Runner = {
    val backgroundAPI = new BackgroundAPI
    val messages = new I18NMessages
    val scriptInjector = new ScriptInjector
    val commandProcessor = new CommandProcessor
    val externalMessageProcessor = new ExternalMessageProcessor(commandProcessor)
    new Runner(config.activeTabConfig, backgroundAPI, messages, scriptInjector, externalMessageProcessor)
  }
}
