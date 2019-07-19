package models.wallet

import java.util.Date
import javax.persistence.{Table, Entity}

import play.api.libs.json.{JsNull, Json, Writes}

/** A transaction detail class that encapsulates transactions with PayPal. */
@Entity
@Table(name = "paypal_txn_detail")
class PaypalTxnDetail extends TxnDetail {
}

/** Companion object for [[PaypalTxnDetail]]. */
object PaypalTxnDetail {
  implicit val paypalTxnDetailWrites = new Writes[PaypalTxnDetail] {
    def writes(detail: PaypalTxnDetail) = Json.obj(
      "uuid" -> detail.uuid,
      "timestamp" -> detail.timestamp,
      "description" -> detail.description
    )
  }

  def apply(id: Long, uuid: String, timestamp: Date, description: String) = {
    val result = new PaypalTxnDetail()
    result.id = id
    result.uuid = uuid
    result.timestamp = timestamp
    result.description = description
    result
  }

  def unapply(detail: PaypalTxnDetail) = Some((detail.id, detail.uuid, detail.timestamp, detail.description))

}