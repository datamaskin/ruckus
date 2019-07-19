package controllers

import auth.AppEnvironment
import play.api.mvc._
import securesocial.core.RuntimeEnvironment

/**
 * Created by mgiles on 8/21/14.
 */

object JavaContext {

  import play.core.j.JavaHelpers
  import play.mvc.Http

  def withContext[Status](block: => Status)(implicit header: RequestHeader): Status = {
    try {
      Http.Context.current.set(JavaHelpers.createJavaContext(header))
      block
    }
    finally {
      Http.Context.current.remove()
    }
  }
}

object LoginController extends Controller {
  def newSignUp = Action { implicit request => {
    val env: RuntimeEnvironment[_] = AppEnvironment.getEnvironment
    JavaContext.withContext {
      Ok(views.html.auth.newSignUp.render(request, play.api.i18n.Lang.defaultLang, env))
    }
  }
  }
}
