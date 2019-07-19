package models.contest;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by mwalsh on 6/24/14.
 */

@Entity
@DiscriminatorValue("1")
public class ContestStateOpen extends ContestState {

    public ContestStateOpen() {
        this.setId(1);
        this.setName("open");
    }

    @Override
    public ContestState proceed() {
        return new ContestStateEntriesLocked();
    }

    @Override
    public ContestState error() {
        return new ContestStateCancelled();
    }
}
