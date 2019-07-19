package models.wallet;

/** The enumeration containing all Victiv transaction types. */
public enum VictivTxnType {
    /** A debit from a wallet due to the user entering a contest. */
    CONTEST_ENTRY,

    /** A result of the contest that will credit a wallet. */
    CONTEST_RESULT,

    /** A refund as a result of a contest cancel. */
    CONTEST_CANCEL,

    /** A refund as a result of a user withdrawing from a contest. */
    CONTEST_WITHDRAWAL,

    /** A credit from Victiv deciding to slush money to the wallet. */
    SLUSH,

    /** A credit to a wallet from the user clearing part of an assigned bonus. */
    BONUS_CLEAR,

    /** An affiliate credit to a wallet as a result of affiliate rewards. */
    AFFILIATE_CREDIT
}
