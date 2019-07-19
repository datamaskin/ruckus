package controllers

import play.api.Play
import play.api.Play.current
import play.api.mvc.{Action, AnyContent}

object ConfigurableAssets extends AssetsBuilder {
  private val assetsPath = Play.configuration.getString("assets.path").getOrElse("/public")

  def at(file: String): Action[AnyContent] = {
    at(assetsPath, file)
  }
}