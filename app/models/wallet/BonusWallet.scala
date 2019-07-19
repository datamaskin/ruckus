package models.wallet

import java.util.Date
import javax.persistence._
import org.apache.commons.lang3.builder.{ToStringBuilder, EqualsBuilder, HashCodeBuilder}
import play.api.libs.json.{JsNumber, JsNull, Json, Writes}
import models.user.User
import scala.beans.BeanProperty

/** A wallet that tracks bonuses to users. */
@Entity
@Table(name = "bonus_wallet")
class BonusWallet {
  /** The database primary key ID.  */
  @BeanProperty
  @Id
  @Column(name = "id")
  var id: Long = 0

  /** A unique code that identifies this bonus wallet and is safe to publish. */
  @BeanProperty
  @Column(name = "uuid", nullable = false, unique = true)
  var uuid: String = null

  /** The user that this wallet is allocated to. */
  @BeanProperty
  @ManyToOne
  @Column(name = "user_id")
  var user: User = null

  /** The original amount of the bonus in US Cents. */
  @BeanProperty
  @Column(name = "original_amount")
  var originalAmount: Long = 0

  /** The timestamp when the bonus was granted on the user's account in US Cents. */
  @Column(name = "granted_on")
  @BeanProperty
  var grantedOn: Date = null

  /** Amount cleared in US Cents. */
  @BeanProperty
  @Column(name = "cleared_amount")
  var clearedAmount: Long = 0

  /** Bonus plan for this wallet. */
  @BeanProperty
  @Enumerated(EnumType.STRING)
  @Column(name = "payout_plan")
  var payoutPlan: BonusWalletPayoutPlan = null

  /** The award type for this bonus wallet. */
  @BeanProperty
  @Enumerated(EnumType.STRING)
  @Column(name = "award_type")
  var awardType: BonusWalletAwardType = null

  override def hashCode(): Int = new HashCodeBuilder().append(id).append(uuid).toHashCode

  override def equals(obj: scala.Any): Boolean = obj match {
    case that: BonusWallet => new EqualsBuilder().append(id, that.id).append(uuid, that.uuid).isEquals
    case _ => false
  }

  override def toString: String =
    new ToStringBuilder(this)
      .append("id", id)
      .append("uuid", uuid)
      .append("originalAmount", originalAmount)
      .append("granted_on", grantedOn)
      .append("clearedAmount", clearedAmount).toString
}

/** Companion object for [[BonusWallet]]. */
object BonusWallet {
  /** Implicit object for JSON generation. */
  implicit val bonusWalletWrites = new Writes[BonusWallet] {
    def writes(wallet: BonusWallet) = Json.obj(
      "uuid" -> wallet.uuid,
      "userId" -> (if (wallet.user == null) JsNull else Long2long(wallet.user.getId)),
      "originalAmount" -> wallet.originalAmount,
      "grantedOn" -> wallet.grantedOn,
      "clearedAmount" -> wallet.clearedAmount,
      "plan" -> wallet.payoutPlan.toString,
      "awardType" -> wallet.awardType.toString
    )
  }

  def apply(id: Long, uuid: String, userId: User, originalAmount: Long, grantedOn: Date, clearedAmount: Long,
            payoutPlan: BonusWalletPayoutPlan, awardType: BonusWalletAwardType) = {
    val result = new BonusWallet()
    result.id = id
    result.uuid = uuid
    result.user = userId
    result.originalAmount = originalAmount
    result.grantedOn = grantedOn
    result.clearedAmount = clearedAmount
    result.awardType = awardType
    result
  }

  def unapply(wallet: BonusWallet) = Some((wallet.id, wallet.uuid, wallet.user, wallet.originalAmount, wallet.grantedOn,
    wallet.clearedAmount, wallet.payoutPlan, wallet.awardType))
}