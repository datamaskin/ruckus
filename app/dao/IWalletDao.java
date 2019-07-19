package dao;

import models.wallet.UserWalletTxn;

import java.util.List;

/** AN interface to the DAO for wallet operations. */
public interface IWalletDao {
    /**
     * Fetch transaction for a particular user.
     *
     * @param userId The user id to look for.
     * @return The list of transaction objects.
     */
    List<UserWalletTxn> transactionsForUser(final Long userId);

    /**
     * Save a transaction as well as its detail
     *
     * @param txn The transaction to save
     */
    void save(UserWalletTxn txn);
}
