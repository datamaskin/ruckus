package models.wallet

import java.util.Date
import javax.persistence.{Column, Table, Entity}
import org.apache.commons.lang3.builder.ToStringBuilder
import play.api.libs.json.{Json, Writes}
import scala.beans.BeanProperty

/** Detail for CAMS based Transactions. */
@Entity
@Table(name = "cams_txn_detail")
class CAMSTxnDetail extends TxnDetail {

  /** CAMS Transaction result ID. */
  @BeanProperty
  @Column(name = "cams_result_code", nullable = false)
  var camsResultCode: Int = 0

  /** CAMS Transaction result ID. */
  @BeanProperty
  @Column(name = "cams_authcode", nullable = true)
  var camsAuthcode: String = null

  override def toString: String =
    new ToStringBuilder(this)
      .append("id", id)
      .append("uuid", uuid)
      .append("timestamp", timestamp)
      .append("description", description)
      .append("camsResultCode", camsResultCode).toString
}

/** Companion object for [[CAMSTxnDetail]]. */
object CAMSTxnDetail {
  implicit val camsTxnDetailWrites = new Writes[CAMSTxnDetail] {
    def writes(detail: CAMSTxnDetail) = Json.obj(
      "uuid" -> detail.uuid,
      "timestamp" -> detail.timestamp,
      "camsResultCode" -> detail.camsResultCode,
      "camsAuthCode" -> detail.camsAuthcode
    )
  }

  def apply(id: Long, uuid: String, timestamp: Date, description: String, camsResultCode: Int, camsAuthcode: String) = {
    val result = new CAMSTxnDetail()
    result.id = id
    result.uuid = uuid
    result.timestamp = timestamp
    result.description = description
    result.camsResultCode = camsResultCode
    result.camsAuthcode = camsAuthcode
    result
  }

  def unapply(detail: CAMSTxnDetail) = Some((detail.id, detail.uuid, detail.timestamp, detail.description,
    detail.camsResultCode, detail.camsAuthcode))

}