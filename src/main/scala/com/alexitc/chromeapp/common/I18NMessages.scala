package com.alexitc.chromeapp.common

import chrome.i18n.I18N

class I18NMessages {

  def appName: String = getMessage("extensionName")

  private def getMessage(id: String, substitutions: String*): String = {
    I18N
      .getMessage(id, substitutions: _*)
      .getOrElse(throw new RuntimeException(s"Message $id not available"))
  }
}
