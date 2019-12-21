package com.alexitc.background.services.http

import com.softwaremill.sttp._

import scala.concurrent.{ExecutionContext, Future}

/**
 * Internal service available to the background context, which allows to call an http service.
 */
private[background] class HttpService(config: HttpService.Config)(
    implicit backend: SttpBackend[Future, Nothing],
    ec: ExecutionContext
) {

  private val ServerAPI: Uri = Uri
    .parse(config.serverUrl)
    .getOrElse(throw new RuntimeException("Invalid server url"))

}

object HttpService {

  case class Config(serverUrl: String)

  def apply(config: Config)(implicit ec: ExecutionContext): HttpService = {
    val backend = FetchBackend()
    new HttpService(config)(backend, ec)
  }
}
