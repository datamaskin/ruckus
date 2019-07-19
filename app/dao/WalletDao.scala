package dao

import java.lang.Long
import java.util
import com.avaje.ebean.Ebean
import models.wallet.UserWalletTxn

/** An implementation of the wallet Dao in scala */
class WalletDao extends AbstractDao with IWalletDao {

  override def transactionsForUser(userId: Long): util.List[UserWalletTxn] = {
    Ebean.find(classOf[UserWalletTxn])
      .fetch("camsDetail")
      .fetch("paypalDetail")
      .fetch("bitcoinDetail")
      .fetch("victivDetail")
      .fetch("wallet")
      .where.eq("wallet.user.id", userId)
      .findList()
  }

  override def save(txn: UserWalletTxn) = {
    Ebean.save(txn)
  }
}

