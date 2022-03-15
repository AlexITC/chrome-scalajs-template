package com.alexitc.chromeapp.activetab

import com.alexitc.chromeapp.activetab.models.{Command, Event}

import scala.concurrent.{ExecutionContext, Future}

private[activetab] class CommandProcessor(implicit ec: ExecutionContext) {

  def process(cmd: Command): Future[Event] = {
    cmd match {
      case Command.GetInfo =>
        val msg = s"Extension id = ${chrome.runtime.Runtime.id}"
        val response = Event.GotInfo(msg)
        Future.successful(response)
    }
  }.recover { case e =>
    Event.CommandRejected(e.getMessage) // Any exceptions will be resolved to CommandRejected
  }
}
