package models.contest;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by mwalsh on 6/24/14.
 */

@Entity
@DiscriminatorValue("2")
public class ContestStateEntriesLocked extends ContestState {

    public ContestStateEntriesLocked() {
        this.setId(2);
        this.setName("entries_locked");
    }

    @Override
    public ContestState proceed() {
        return new ContestStateRosterLocked();
    }

    @Override
    public ContestState error() {
        return new ContestStateCancelled();
    }
}
