package models.wallet

import java.util.Date
import javax.persistence._
import models.contest.Contest
import org.apache.commons.lang3.builder.ToStringBuilder
import play.api.libs.json.{JsNull, Json, Writes}
import scala.beans.BeanProperty

/** Encapsulates details of bitcoin transactions. */
@Entity
@Table(name = "victiv_txn_detail")
class VictivTxnDetail extends TxnDetail {
  /** The type of transaction which is an enumeration. */
  @BeanProperty
  @Enumerated(EnumType.STRING)
  @Column(name = "victiv_txn_type", nullable = false)
  var victivTxnType: VictivTxnType = null

  /** The associated contest for the detail, if any. This may be null. */
  @BeanProperty
  @ManyToOne
  @Column(name = "contest_id", nullable = true)
  var contest: Contest = null

  override def toString: String =
    new ToStringBuilder(this)
      .append("id", id)
      .append("uuid", uuid)
      .append("timestamp", timestamp)
      .append("description", description)
      .append("victivTxnType", victivTxnType.toString)
      .append("contest.id", if (this.contest == null) null else contest.getId).toString
}

/** Companion object for [[VictivTxnDetail]]. */
object VictivTxnDetail {
  implicit val victivTxnDetailWrites = new Writes[VictivTxnDetail] {
    def writes(detail: VictivTxnDetail) = Json.obj(
      "uuid" -> detail.uuid,
      "timestamp" -> detail.timestamp,
      "victivTxnType" -> detail.victivTxnType.toString,
      "contest" -> (if (detail.contest == null) JsNull else detail.contest.getUrlId)
    )
  }

  def apply(id: Long, uuid: String, timestamp: Date, description: String, victivTxnType: VictivTxnType, contest: Contest) = {
    val result = new VictivTxnDetail()
    result.id = id
    result.uuid = uuid
    result.timestamp = timestamp
    result.description = description
    result.victivTxnType = victivTxnType
    result.contest = contest
    result
  }

  def unapply(detail: VictivTxnDetail) = Some((detail.id, detail.uuid, detail.timestamp, detail.description,
    detail.victivTxnType, detail.contest))
}

