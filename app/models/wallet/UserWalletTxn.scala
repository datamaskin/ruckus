package models.wallet

import java.time.{ZoneId, ZonedDateTime}
import java.util.{UUID, Date}
import javax.persistence._
import models.contest.Contest
import org.apache.commons.lang3.builder.{ToStringBuilder, EqualsBuilder, HashCodeBuilder}
import play.api.libs.json.{JsNull, Json, Writes}
import scala.beans.BeanProperty

/**
 * Encapsulates a single transaction in a user wallet. Note that at least one of the detail objects must be set
 * on the transaction object.
 */
@Entity
@Table(name = "user_wallet_txn")
class UserWalletTxn {
  /** Primary key ID of the transaction. */
  @BeanProperty
  @Id
  @Column(name = "id")
  var id: Long = 0

  /** A unique code that identifies this transaction. */
  @BeanProperty
  @Column(name = "uuid", nullable = false, unique = true)
  var uuid: String = null

  /** The timestamp of the transaction. */
  @BeanProperty
  @Column(name = "time_stamp", nullable = false)
  var timestamp: Date = null

  /** The timestamp of the transaction. */
  @BeanProperty
  @ManyToOne()
  @Column(name = "user_wallet_id", nullable = false)
  var userWallet: UserWallet = null

  /** The CAMS Transaction detail object if any. */
  @BeanProperty
  @PrimaryKeyJoinColumn
  @OneToOne(cascade = Array(CascadeType.ALL), fetch = FetchType.EAGER, optional = true, orphanRemoval = true)
  @Column(name = "cams_detail_id", nullable = true)
  var camsDetail: CAMSTxnDetail = null

  /** The Paypal transaction detail object if any. */
  @BeanProperty
  @OneToOne(cascade = Array(CascadeType.ALL), fetch = FetchType.EAGER, optional = true, orphanRemoval = true)
  @Column(name = "paypal_detail_id", nullable = true)
  var paypalDetail: PaypalTxnDetail = null

  /** The Bitcoin transaction detail object if any. */
  @BeanProperty
  @OneToOne(cascade = Array(CascadeType.ALL), fetch = FetchType.EAGER, optional = true, orphanRemoval = true)
  @Column(name = "bitcoin_detail_id", nullable = true)
  var bitcoinDetail: BitcoinTxnDetail = null

  /** The Victiv transaction detail object if any. */
  @BeanProperty
  @OneToOne(cascade = Array(CascadeType.ALL), fetch = FetchType.EAGER, optional = true, orphanRemoval = true)
  @Column(name = "victiv_detail_id", nullable = true)
  var victivDetail: VictivTxnDetail = null

  /** Wallet balance after this transaction in US Cents. */
  @BeanProperty
  @Column(name = "amount", nullable = false)
  var amount: Long = 0

  /** Wallet balance after this transaction in US Cents. */
  @BeanProperty
  @Column(name = "balance_after", nullable = false)
  var balanceAfter: Long = 0

  override def hashCode(): Int = new HashCodeBuilder().append(id).append(uuid).toHashCode

  override def equals(obj: scala.Any): Boolean = obj match {
    case that: UserWalletTxn => new EqualsBuilder().append(id, that.id).append(uuid, that.uuid).isEquals
    case _ => false
  }

  override def toString: String =
    new ToStringBuilder(this)
      .append("id", id)
      .append("uuid", uuid)
      .append("timestamp", timestamp)
      .append("wallet.id", if (userWallet == null) null else userWallet.getId)
      .append("camsDetail", camsDetail).append("paypalDetail", paypalDetail).append("bitcoinDetail", bitcoinDetail)
      .append("victivDetail", victivDetail)
      .toString

}

/** Companion object for [[UserWalletTxn]]. */
object UserWalletTxn {
  implicit val userWalletTxnWrites = new Writes[UserWalletTxn] {
    /** Note we don't expose PK in transaction json. */
    def writes(txn: UserWalletTxn) = Json.obj(
      "uuid" -> txn.uuid,
      "timestamp" -> txn.timestamp,
      "cams_detail" -> (if (txn.camsDetail == null) JsNull else CAMSTxnDetail.camsTxnDetailWrites.writes(txn.camsDetail)),
      "paypal_detail" -> (if (txn.paypalDetail == null) JsNull else PaypalTxnDetail.paypalTxnDetailWrites.writes(txn.paypalDetail)),
      "transaction" -> (if (txn.bitcoinDetail == null) JsNull else BitcoinTxnDetail.bitcoinTxnDetailWrites.writes(txn.bitcoinDetail)),
      "victiv_detail" -> (if (txn.victivDetail == null) JsNull else VictivTxnDetail.victivTxnDetailWrites.writes(txn.victivDetail)),
      "amount" -> txn.amount,
      "usdBalanceAfter" -> txn.balanceAfter
    )
  }

  def apply(id: Long, uuid: String, timestamp: Date, wallet: UserWallet, camsDetail: CAMSTxnDetail,
            paypalDetail: PaypalTxnDetail, bitcoinDetail: BitcoinTxnDetail, victivDetail: VictivTxnDetail,
            amount: Long, balanceAfter: Long) = {
    val result = new UserWalletTxn()
    result.id = id
    result.uuid = uuid
    result.timestamp = timestamp
    result.userWallet = wallet
    result.camsDetail = camsDetail
    result.paypalDetail = paypalDetail
    result.bitcoinDetail = bitcoinDetail
    result.victivDetail = victivDetail
    result.amount = amount
    result.balanceAfter = balanceAfter
    result
  }

  def unapply(txn: UserWalletTxn) = Some((txn.id, txn.uuid, txn.timestamp, txn.userWallet, txn.camsDetail,
    txn.paypalDetail, txn.bitcoinDetail, txn.victivDetail, txn.amount, txn.balanceAfter))

  def camsTxn(wallet: UserWallet, amount: Long, balanceAfter: Long, description: String, camsResultCode: Int, camsAuthcode: String) = {
    val result = new UserWalletTxn()
    result.id = 0
    result.uuid = UUID.randomUUID.toString
    result.timestamp = Date.from(ZonedDateTime.now(ZoneId.of("UTC")).toInstant)
    result.userWallet = wallet
    result.camsDetail = CAMSTxnDetail(0, result.uuid, result.timestamp, description, camsResultCode, camsAuthcode)
    result.paypalDetail = null
    result.bitcoinDetail = null
    result.victivDetail = null
    result.amount = amount
    result.balanceAfter = balanceAfter
    result
  }

  def victivTxn(wallet: UserWallet, amount: Long, balanceAfter: Long, description: String, txnType : VictivTxnType, contest: Contest) = {
    val result = new UserWalletTxn()
    result.id = 0
    result.uuid = UUID.randomUUID.toString
    result.timestamp = Date.from(ZonedDateTime.now(ZoneId.of("UTC")).toInstant)
    result.userWallet = wallet
    result.camsDetail = null
    result.paypalDetail = null
    result.bitcoinDetail = null
    result.victivDetail = VictivTxnDetail(0, result.uuid, result.timestamp, description, txnType, contest)
    result.amount = amount
    result.balanceAfter = balanceAfter
    result
  }

  def bitcoinTxn(wallet: UserWallet, amount: Long, balanceAfter: Long, description: String) = {
    val result = new UserWalletTxn()
    result.id = 0
    result.uuid = UUID.randomUUID.toString
    result.timestamp = Date.from(ZonedDateTime.now(ZoneId.of("UTC")).toInstant)
    result.userWallet = wallet
    result.camsDetail = null
    result.paypalDetail = null
    result.bitcoinDetail = BitcoinTxnDetail(0, result.uuid, result.timestamp, description)
    result.victivDetail = null
    result.amount = amount
    result.balanceAfter = balanceAfter
    result
  }

  def paypalTxn(wallet: UserWallet, amount: Long, balanceAfter: Long, description: String) = {
    val result = new UserWalletTxn()
    result.id = 0
    result.uuid = UUID.randomUUID.toString
    result.timestamp = Date.from(ZonedDateTime.now(ZoneId.of("UTC")).toInstant)
    result.userWallet = wallet
    result.camsDetail = null
    result.paypalDetail = PaypalTxnDetail(0, result.uuid, result.timestamp, description)
    result.bitcoinDetail = null
    result.victivDetail = null
    result.amount = amount
    result.balanceAfter = balanceAfter
    result
  }
}