package com.alexitc.chromeapp.website

import com.alexitc.chromeapp.activetab.ActiveTabPublicAPI

import scala.concurrent.ExecutionContext
import scala.scalajs.js
import scala.scalajs.js.JSConverters.JSRichFutureNonThenable
import scala.scalajs.js.PropertyDescriptor
import scala.scalajs.js.|._

/** This is js-object that can be injected directly into websites.
  *
  * For example, you can use this to interact with the website JavaScript, expose new JavaScript functions to the
  * website (like Metamask), etc.
  *
  * There are some important details to consider:
  *   - Exposed JavaScript function should be js-friendly instead of pure Scala, expose a js Promise instead of a Scala
  *     Future.
  *   - The exposed Scala functions should be public (required by the compiler).
  *
  * In this case, the website would get this function available: window.injected.getInfo()
  */
private[website] class ObjectInjector(name: String = "injected", activeTab: ActiveTabPublicAPI)(implicit
    ec: ExecutionContext
) {

  def inject(parent: js.Object): Unit = {
    js.Object.defineProperty(
      parent,
      name,
      new PropertyDescriptor {
        enumerable = false
        writable = false
        configurable = false
        value = js.Dictionary(
          "getInfo" -> js.Any.fromFunction0(() => getInfo())
        ): js.UndefOr[js.Any]
      }
    )
  }

  def getInfo(): js.Promise[String] = {
    activeTab
      .getInfo()
      .map(_.details)
      .toJSPromise
  }
}
