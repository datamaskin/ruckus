package models.contest;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by mwalsh on 6/24/14.
 */

@Entity
@DiscriminatorValue("5")
public class ContestStateComplete extends ContestState {
    public ContestStateComplete() {
        this.setId(5);
        this.setName("complete");
    }

    @Override
    public ContestState proceed() {
        return new ContestStateHistory();
    }

    @Override
    public ContestState error() {
        return new ContestStateHistory();
    }
}
