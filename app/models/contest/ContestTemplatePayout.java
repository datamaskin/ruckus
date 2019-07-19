package models.contest;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.RoundingMode;

@Entity
public class ContestTemplatePayout {

    @Id
    private int id;
    @Column(nullable = false)
    private int leadingPosition;
    @Column(nullable = false)
    private int trailingPosition;
    @Column(nullable = false)
    private float payoutPercentage;
    @Column(nullable = false)
    private RoundingMode roundingMode;

    public ContestTemplatePayout(int leadingPosition, int trailingPosition, float payoutPercentage, RoundingMode roundingMode) {
        if (leadingPosition > trailingPosition) {
            throw new IllegalArgumentException("Leading Position cannot be greater than Trailing Position.");
        }
        this.leadingPosition = leadingPosition;
        this.trailingPosition = trailingPosition;
        this.payoutPercentage = payoutPercentage;
        this.roundingMode = roundingMode;
    }

    public int getId(){
        return id;
    }
    
    public int getLeadingPosition() {
        return leadingPosition;
    }

    public int getTrailingPosition() {
        return trailingPosition;
    }

    public float getPayoutPercentage() {
        return payoutPercentage;
    }

    public RoundingMode getRoundingMode() {
        return roundingMode;
    }

    @Override
    public String toString() {
        return "ContestTemplatePayout{" +
                "id=" + id +
                ", leadingPosition=" + leadingPosition +
                ", trailingPosition=" + trailingPosition +
                ", payoutPercentage=" + payoutPercentage +
                ", roundingMode=" + roundingMode +
                '}';
    }
}
