package auth

import play.api.data.Form
import play.api.i18n.Lang
import play.api.mvc.RequestHeader
import play.core.j.JavaHelpers
import play.mvc.Http
import play.twirl.api.Html
import securesocial.controllers.{ChangeInfo, RegistrationInfo, ViewTemplates}
import securesocial.core.RuntimeEnvironment

/**
 * Created by mwalsh on 7/21/14.
 */
class CustomViewTemplates(env: RuntimeEnvironment[_]) extends ViewTemplates {
  implicit val implicitEnv = env

  override def getLoginPage(form: Form[(String, String)],
                            msg: Option[String] = None)(implicit request: RequestHeader, lang: Lang): Html = {
    val oldContext = Http.Context.current.get();
    try {
      Http.Context.current.set(JavaHelpers.createJavaContext(request));
      views.html.auth.login(form, msg, request, lang, env)
    } finally {
      Http.Context.current.set(oldContext);
    }
  }

  override def getSignUpPage(form: Form[RegistrationInfo], token: String)(implicit request: RequestHeader, lang: Lang): Html = {
    val oldContext = Http.Context.current.get();
    try {
      Http.Context.current.set(JavaHelpers.createJavaContext(request));
      views.html.auth.signUp(form, token, request, lang, env)
    } finally {
      Http.Context.current.set(oldContext);
    }
  }

  override def getStartSignUpPage(form: Form[String])(implicit request: RequestHeader, lang: Lang): Html = {
    val oldContext = Http.Context.current.get();
    try {
      Http.Context.current.set(JavaHelpers.createJavaContext(request));
      views.html.auth.startSignUp(form, request, lang, env)
    } finally {
      Http.Context.current.set(oldContext);
    }
  }

  override def getStartResetPasswordPage(form: Form[String])(implicit request: RequestHeader, lang: Lang): Html = {
    val oldContext = Http.Context.current.get();
    try {
      Http.Context.current.set(JavaHelpers.createJavaContext(request));
      views.html.auth.startResetPassword(form, request, lang, env)
    } finally {
      Http.Context.current.set(oldContext);
    }
  }

  override def getResetPasswordPage(form: Form[(String, String)], token: String)(implicit request: RequestHeader, lang: Lang): Html = {
    val oldContext = Http.Context.current.get();
    try {
      Http.Context.current.set(JavaHelpers.createJavaContext(request));
      views.html.auth.resetPasswordPage(form, token, request, lang, env)
    } finally {
      Http.Context.current.set(oldContext);
    }
  }

  override def getPasswordChangePage(form: Form[ChangeInfo])(implicit request: RequestHeader, lang: Lang): Html = {
    val oldContext = Http.Context.current.get();
    try {
      Http.Context.current.set(JavaHelpers.createJavaContext(request));
      views.html.auth.passwordChange(form, request, lang, env)
    } finally {
      Http.Context.current.set(oldContext);
    }
  }

  def getNotAuthorizedPage(implicit request: RequestHeader, lang: Lang): Html = {
    val oldContext = Http.Context.current.get();
    try {
      Http.Context.current.set(JavaHelpers.createJavaContext(request));
      views.html.auth.notAuthorized(request, lang, env)
    } finally {
      Http.Context.current.set(oldContext);
    }
  }
}