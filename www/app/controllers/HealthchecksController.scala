package controllers

import io.flow.dependency.www.lib.UiData
import io.flow.play.controllers.{FlowController, FlowControllerComponents}
import io.flow.play.util.Config
import play.api.i18n._
import play.api.mvc._

class HealthchecksController @javax.inject.Inject()(
  val config: Config,
  val controllerComponents: ControllerComponents,
  val flowControllerComponents: FlowControllerComponents
) extends FlowController with I18nSupport {

  def index() = Action { implicit request =>
    Ok(
      views.html.healthchecks.index(
        UiData(requestPath = request.path),
        "healthy"
      )
    )

  }

}
