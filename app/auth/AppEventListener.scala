package auth

import dao.IUserDao
import models.user.{User, UserAction}
import play.api.mvc.{Session, RequestHeader}
import securesocial.core._

/**
 * Created by mwalsh on 8/21/14.
 */
class AppEventListener(userDao: IUserDao) extends EventListener[User] {
  override def onEvent(event: Event[User], request: RequestHeader, session: Session): Option[Session] = {
    val eventName = event match {
      case e: LoginEvent[User] => UserAction.Type.LOGIN
      case e: LogoutEvent[User] => UserAction.Type.LOGOUT
      case e: PasswordChangeEvent[User] => UserAction.Type.PASSWORD_CHANGE
      case e: PasswordResetEvent[User] => UserAction.Type.PASSWORD_RESET
      case e: SignUpEvent[User] => UserAction.Type.SIGN_UP
    }

    userDao.saveUserAction(new UserAction(event.user, eventName, request.remoteAddress))
    None
  }
}
