package controllers

import auth.AppEnvironment
import dao.DaoFactory
import models.business.{AffiliateCode, AffiliatePlan}
import models.user.User
import play.api.mvc._
import play.api.libs.json.Json._
import scala.collection.JavaConverters._
import securesocial.core.{RuntimeEnvironment, SecureSocial}

/** A controller for affiliate based activities. */
object AffiliateController extends Controller with SecureSocial[User] {
  /** Environment needed by securesocial. */
  implicit val env: RuntimeEnvironment[User] = AppEnvironment.getEnvironment

  implicit val codeWrites = AffiliateCode.affiliateCodeWrites
  implicit val planWrites = AffiliatePlan.affiliatePlanWrites

  /** Return a JSON representation of all affiliate plans. */
  def affiliatePlans = SecuredAction(WithUserRoleAuthorization("admin")) {
    val affiliatePlanList = DaoFactory.getAffiliateDao.allAffiliatePlans()
    Ok(toJson(affiliatePlanList.asScala))
  }

  /** Return a JSON representation of all affiliate codes. */
  def affiliateCodes = SecuredAction(WithUserRoleAuthorization("admin")) {
    val affiliateCodeList = DaoFactory.getAffiliateDao.allAffiliateCodes()
    Ok(toJson(affiliateCodeList.asScala))
  }

  /**
   * Return a JSON representation of a user's affiliate codes
   * @param userId The user id to get the codes for.
   * @return The resulting codes.
   */
  def userAffiliateCodes(userId: Option[Long]) = SecuredAction(WithUserRoleAuthorization("admin")) {
    userId match {
      case Some(uid) =>
        val affiliateCodeList = DaoFactory.getAffiliateDao.affiliateCodesForUser(uid)
        Ok(toJson(affiliateCodeList.asScala))
      case _ => BadRequest("Params Missing")
    }
  }

  /** Handles calls with affiliate code, setting the code in a session cookie.  */
  def fromAffiliate(code: Option[String]) = Action {
    import scala.concurrent.duration._
    code match {
      case Some(code) => Redirect("/").withCookies(Cookie(AffiliateUtils.AFFILIATE_COOKIE_NAME, code,
        Some(24.hours.toSeconds.toInt)))
      case None => Redirect("/")
    }
  }

}

object AffiliateUtils {
  /** Name used for an affiliate cookie. */
  val AFFILIATE_COOKIE_NAME = "victiv.affiliate.code"

  /**
   * Sets the affiliate code for a user if there is a cookie in the request with the name [[AFFILIATE_COOKIE_NAME]].
   * This method is designed to take in a java request and set the affiliate code.
   */
  def setUserAffiliate4Java(request: play.mvc.Http.Request, user: User): Unit = {
    request.cookie(AFFILIATE_COOKIE_NAME) match {
      case cookie: play.mvc.Http.Cookie =>
        val affiliateCode = DaoFactory.getAffiliateDao.affiliateCodeForUUID(cookie.value)
        affiliateCode match {
          case affiliateCode: AffiliateCode => user.setAffiliateCode(affiliateCode)
          case _ => Unit
        }
      case _ => Unit
    }
  }
}

