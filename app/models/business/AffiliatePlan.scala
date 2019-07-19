package models.business

import java.util.{UUID, Date}
import javax.persistence._
import org.apache.commons.lang3.builder.{EqualsBuilder, ToStringBuilder, HashCodeBuilder}
import play.api.libs.json.{Json, Writes}
import scala.beans.BeanProperty

/** Encapsulates a plan for a particular reimbursement promotion for an affiliate.  */
@Entity
@Table(name = "affiliate_plan")
class AffiliatePlan {
  /** Database primary key id. */
  @BeanProperty
  @Id
  @Column(name = "id", unique = true, nullable = false)
  var id: Long = 0

  /** A unique name for the plan. */
  @BeanProperty
  @Column(name = "uuid", unique = true, nullable = false)
  var uuid: String = null

  /** A unique name for the plan. */
  @BeanProperty
  @Column(name = "name", unique = true, nullable = false)
  var name: String = null

  /** A date for this plan to start where users are able to use the link. */
  @BeanProperty
  @Column(name = "start", nullable = false)
  var start: Date = null

  /** A date for expiration of the plan beyond which users cant sign up for it. */
  @BeanProperty
  @Column(name = "expires", nullable = false)
  var expires: Date = null

  override def hashCode(): Int = new HashCodeBuilder().append(id).append(uuid).toHashCode

  override def equals(obj: scala.Any): Boolean = obj match {
    case that: AffiliatePlan => new EqualsBuilder().append(id, that.id).append(uuid, that.uuid).isEquals
    case _ => false
  }

  override def toString: String =
    new ToStringBuilder(this).append("id", id).append("uuid", uuid).toString
}

object AffiliatePlan {
  /** JSON Serializer for an affiliate plan. */
  implicit val affiliatePlanWrites = new Writes[AffiliatePlan] {
    def writes(affiliatePlan: AffiliatePlan) = Json.obj(
      "uuid" -> affiliatePlan.uuid,
      "name" -> affiliatePlan.name,
      "start" -> affiliatePlan.start,
      "expires" -> affiliatePlan.expires
    )
  }

  def apply(id: Long, uuid: String, name: String, start: Date, expires: Date) = {
    val result = new AffiliatePlan()
    result.id = id
    result.uuid = uuid
    result.name = name
    result.start = start
    result.expires = expires
    result
  }

  def unapply(plan: AffiliatePlan) = Some((plan.id, plan.uuid, plan.name, plan.start, plan.expires))

  /** Creates a new affiliate plan with the id set to 0 and uuid generated. */
  def apply(name: String, start: Date, expires: Date) = {
    val result = new AffiliatePlan()
    result.id = 0
    result.uuid = UUID.randomUUID.toString
    result.name = name
    result.start = start
    result.expires = expires
    result
  }
}

