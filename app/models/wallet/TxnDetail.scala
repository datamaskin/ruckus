package models.wallet

import java.util.Date
import javax.persistence._
import org.apache.commons.lang3.builder.{EqualsBuilder, ToStringBuilder, HashCodeBuilder}
import scala.beans.BeanProperty

/** Base class for all Transaction Detail subclasses. */
@MappedSuperclass
abstract class TxnDetail {
  /** Primary key ID of the transaction detail. */
  @BeanProperty
  @Id
  @Column(name = "id", nullable = false)
  var id: Long = 0

  /** A unique code that identifies this bonus wallet and is safe to publish. */
  @BeanProperty
  @Column(name = "uuid", nullable = false, unique = true)
  var uuid: String = null

  /** The timestamp when this transaction detail occurred. */
  @BeanProperty
  @Column(name = "time_stamp", nullable = false)
  var timestamp: Date = null

  /** A description string. */
  @BeanProperty
  @Column(name = "description", nullable = false)
  var description: String = null

  override def hashCode(): Int = new HashCodeBuilder().append(id).append(uuid).toHashCode

  override def equals(obj: scala.Any): Boolean = obj match {
    case that: TxnDetail => new EqualsBuilder().append(id, that.id).append(uuid, that.uuid).isEquals
    case _ => false
  }

  override def toString: String =
    new ToStringBuilder(this)
      .append("id", id)
      .append("uuid", uuid)
      .append("timestamp", timestamp)
      .append("description", description)
      .toString
}

