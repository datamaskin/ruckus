package models.wallet

import java.util.Date
import javax.persistence.{Table, Entity}

import play.api.libs.json.{Json, Writes}

/** Details of transactions with BitCoin. */
@Entity
@Table(name = "bitcoin_txn_detail")
class BitcoinTxnDetail extends TxnDetail {
}

/** Companion object for [[BitcoinTxnDetail]]. */
object BitcoinTxnDetail {
  implicit val bitcoinTxnDetailWrites = new Writes[BitcoinTxnDetail] {
    def writes(detail: BitcoinTxnDetail) = Json.obj(
      "uuid" -> detail.uuid,
      "timestamp" -> detail.timestamp,
      "description" -> detail.description
    )
  }

  def apply(id: Long, uuid: String, timestamp: Date, description: String) = {
    val result = new BitcoinTxnDetail()
    result.id = id
    result.uuid = uuid
    result.timestamp = timestamp
    result.description = description
    result
  }

  def unapply(detail: BitcoinTxnDetail) = Some((detail.id, detail.uuid, detail.timestamp, detail.description))
}

