package com.alexitc.chromeapp.common

object ResourceProvider {

  def appIcon96: String = {
    chrome.runtime.Runtime.getURL("icons/96/app.png")
  }
}
