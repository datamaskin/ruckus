package controllers

import models.user.{User, UserRole}
import play.api.mvc.RequestHeader
import securesocial.core.Authorization
import scala.collection.JavaConversions._

case class WithUserRoleAuthorization(requiredRoleNames: String*) extends Authorization[User] {
  def isAuthorized(user: User, request: RequestHeader) = {
    user.getUserRoles.toList.exists(role => requiredRoleNames.contains(role.getName))
  }
}