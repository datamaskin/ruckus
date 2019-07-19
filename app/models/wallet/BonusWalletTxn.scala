package models.wallet

import java.util.Date
import javax.persistence._
import org.apache.commons.lang3.builder.{ToStringBuilder, EqualsBuilder, HashCodeBuilder}
import play.api.libs.json._
import play.api.libs.json.Json._
import scala.beans.BeanProperty

/** The transactions on a bonus wallet. */
@Entity
@Table(name = "bonus_wallet_txn")
class BonusWalletTxn {
  /** The database primary key ID.  */
  @BeanProperty
  @Id
  @Column(name = "id", nullable = false)
  var id: Long = 0

  /** A unique code that identifies this transaction. */
  @BeanProperty
  @Column(name = "uuid", nullable = false, unique = true)
  var uuid: String = null

  /** The wallet associated with this transaction. */
  @BeanProperty
  @ManyToOne
  @Column(name = "bonus_wallet_id", nullable = false)
  var bonusWallet: BonusWallet = null

  /** The date that this transaction cleared. */
  @BeanProperty
  @Column(name = "clear_date", nullable = false)
  var clearDate: Date = null

  /** * The amount of the cleared transaction in US Cents. */
  @BeanProperty
  @Column(name = "amount")
  var amount: Long = 0

  override def hashCode(): Int = new HashCodeBuilder().append(id).append(uuid).toHashCode

  override def equals(obj: scala.Any): Boolean = obj match {
    case that: BonusWalletTxn => new EqualsBuilder().append(id, that.id).append(uuid, that.uuid).isEquals
    case _ => false
  }

  override def toString: String =
    new ToStringBuilder(this)
      .append("id", id)
      .append("uuid", uuid)
      .append("bonusWallet.id", if (bonusWallet == null) null else bonusWallet.id)
      .append("clearDate", clearDate)
      .append("amount", amount).toString
}

/** Companion object for [[BonusWalletTxn]]. */
object BonusWalletTxn {
  /** Implicit object for JSON generation. */
  implicit val bonusWalletTxnWrites = new Writes[BonusWalletTxn] {
    def writes(txn: BonusWalletTxn) = Json.obj(
      "uuid" -> txn.uuid,
      "bonus_wallet_id" -> (if (txn.bonusWallet == null) JsNull else txn.bonusWallet.id),
      "clearDate" -> txn.clearDate,
      "amount" -> txn.amount
    )
  }

  def apply(id: Long, uuid: String, bonusWallet: BonusWallet, clearDate: Date, amount: Long) = {
    val result = new BonusWalletTxn()
    result.id = id
    result.uuid = uuid
    result.bonusWallet = bonusWallet
    result.clearDate = clearDate
    result.amount = amount
    result
  }

  def unapply(wallet: BonusWalletTxn) = Some((wallet.id, wallet.uuid, wallet.bonusWallet, wallet.clearDate,
    wallet.amount))
}