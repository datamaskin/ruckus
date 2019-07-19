package dao

import java.lang.Long
import java.util

import com.avaje.ebean.Ebean
import models.business.{AffiliatePlan, AffiliateCode}

/** An implementation of the affiliate Dao in scala */
class AffiliateDao extends AbstractDao with IAffiliateDao {

  override def affiliatePlanForId(affiliatePlanId: Long): AffiliatePlan = {
    Ebean.find(classOf[AffiliatePlan]).where.eq("affiliatePlanId", affiliatePlanId).findUnique()
  }

  override def allAffiliatePlans(): util.List[AffiliatePlan] = Ebean.find(classOf[AffiliatePlan]).findList()

  override def affiliateCodeForId(affiliateCodeId: Long): AffiliateCode = {
    Ebean.find(classOf[AffiliateCode]).where.eq("affiliateCodeId", affiliateCodeId).findUnique()
  }

  override def allAffiliateCodes(): util.List[AffiliateCode] = Ebean.find(classOf[AffiliateCode])
    .orderBy("user.id, plan.id").findList()

  override def affiliateCodesForUser(userId: Long): util.List[AffiliateCode] = {
    Ebean.find(classOf[AffiliateCode]).where.eq("user.id", userId).findList()
  }

  override def affiliateCodeForUUID(uuid: String): AffiliateCode = {
    Ebean.find(classOf[AffiliateCode]).where.eq("uuid", uuid).findUnique()
  }
}
