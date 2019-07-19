package models.business

import java.util.UUID

import models.user.User
import javax.persistence._
import org.apache.commons.lang3.builder.{EqualsBuilder, ToStringBuilder, HashCodeBuilder}
import play.api.libs.json.{JsNull, Json, Writes}
import scala.beans.BeanProperty

/** This entity contains links that map from a specific URL to an [[AffiliatePlan]] for a specific [[User]].  */
@Entity
@Table(name = "affiliate_code")
class AffiliateCode {
  /** Database primary key id. */
  @BeanProperty
  @Id
  var id: Long = 0

  /** The link code a user must pass to associate themselves to the Affiliate plan. */
  @BeanProperty
  @Column(unique = true, name = "uuid")
  var uuid: String = null

  /** The user associated with the link which becomes the affiliateRef in the user object when they register. */
  @BeanProperty
  @ManyToOne(fetch = FetchType.LAZY)
  @Column(name = "user_id")
  var user: User = null

  /** The plan associated with this affiliate code. */
  @BeanProperty
  @ManyToOne(fetch = FetchType.LAZY)
  @Column(name = "plan_id")
  var plan: AffiliatePlan = null

  override def hashCode(): Int = new HashCodeBuilder().append(id).append(uuid).toHashCode

  override def equals(obj: scala.Any): Boolean = obj match {
    case that: AffiliateCode => new EqualsBuilder().append(id, that.id).append(uuid, that.uuid).isEquals
    case _ => false
  }

  override def toString: String =
    new ToStringBuilder(this).append("id", id).append("uuid", uuid).toString
}

object AffiliateCode {
  /** JSON Renderer. */
  implicit val affiliateCodeWrites = new Writes[AffiliateCode] {
    def writes(code: AffiliateCode) = Json.obj(
      "uuid" -> code.uuid,
      "user_id" -> (if (code.plan == null) JsNull else code.user.getId.longValue),
      "plan_id" -> (if (code.plan == null) JsNull else code.plan.getId.longValue)
    )
  }

  def apply(id: Long, uuid: String, user: User, plan: AffiliatePlan) = {
    val code = new AffiliateCode()
    code.id = id
    code.uuid = uuid
    code.user = user
    code.plan = plan
    code
  }

  def unapply(code: AffiliateCode) = Some((code.id, code.uuid, code.user, code.plan))

  /** Creates a new affiliate code with the id set to 0 and uuid generated. */
  def apply(user: User, plan: AffiliatePlan) = {
    val code = new AffiliateCode()
    code.id = 0
    code.uuid = UUID.randomUUID.toString
    code.user = user
    code.plan = plan
    code
  }
}
