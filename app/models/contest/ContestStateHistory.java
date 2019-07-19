package models.contest;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by mgiles on 8/18/14.
 */
@Entity
@DiscriminatorValue("7")
public class ContestStateHistory extends ContestState {
    public ContestStateHistory() {
        this.setId(7);
        this.setName("history");
    }
    @Override
    public ContestState proceed() {
        throw new IllegalArgumentException("ContestStateHistory is terminal.");
    }

    @Override
    public ContestState error() {
        throw new IllegalArgumentException("ContestStateHistory is terminal.");
    }
}
