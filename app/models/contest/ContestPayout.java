package models.contest;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ContestPayout {

    @Id

    private int id;
    private int leadingPosition;
    private int trailingPosition;
    private int payoutAmount;

    public ContestPayout(int leadingPosition, int trailingPosition, int payoutAmount) {
        this.leadingPosition = leadingPosition;
        this.trailingPosition = trailingPosition;
        this.payoutAmount = payoutAmount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLeadingPosition() {
        return leadingPosition;
    }

    public void setLeadingPosition(int leadingPosition) {
        this.leadingPosition = leadingPosition;
    }

    public int getTrailingPosition() {
        return trailingPosition;
    }

    public void setTrailingPosition(int trailingPosition) {
        this.trailingPosition = trailingPosition;
    }

    public int getPayoutAmount() {
        return payoutAmount;
    }

    public void setPayoutAmount(int payoutAmount) {
        this.payoutAmount = payoutAmount;
    }

    @Override
    public String toString() {
        return "ContestPayout{" +
                "leadingPosition=" + leadingPosition +
                ", trailingPosition=" + trailingPosition +
                ", payoutAmount=" + payoutAmount +
                '}';
    }
}
