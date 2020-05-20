package com.alexitc.chromeapp.facades

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport("sweetalert", JSImport.Namespace, globalFallback = "swal")
object SweetAlert extends js.Object {
  def apply(text: String): js.Promise[js.Dynamic] = js.native
  def apply(title: String, text: String): js.Promise[js.Dynamic] = js.native

  def apply(title: String, text: String, icon: String): js.Promise[js.Dynamic] =
    js.native

  def apply(options: Options = js.native): js.Promise[Boolean] = js.native

  trait Options extends js.Object {
    var title: js.UndefOr[String] = js.undefined
    var text: js.UndefOr[String] = js.undefined
    var icon: js.UndefOr[String] = js.undefined
    var buttons: js.UndefOr[js.Array[String]] = js.undefined
  }
}
