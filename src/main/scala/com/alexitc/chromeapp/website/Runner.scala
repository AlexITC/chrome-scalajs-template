package com.alexitc.chromeapp.website

import com.alexitc.chromeapp.activetab.ActiveTabPublicAPI
import org.scalajs.dom

import scala.concurrent.ExecutionContext

/** NOTE: This runs on the current website context, which means we are in a risky environment as the website isn't
  * controlled by us and it can re-define any js function to be different to what we expect.
  */
class Runner(scriptInjection: ObjectInjector) {

  def run(): Unit = {
    println("This was run by the active tab on the website context")
    scriptInjection.inject(dom.window)
  }
}

object Runner {

  def apply()(implicit ec: ExecutionContext): Runner = {
    val activeTabPublicAPI = new ActiveTabPublicAPI()
    val scriptInjection = new ObjectInjector(activeTab = activeTabPublicAPI)
    new Runner(scriptInjection)
  }
}
