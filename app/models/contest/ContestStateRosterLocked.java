package models.contest;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by mwalsh on 6/27/14.
 */

@Entity
@DiscriminatorValue("3")
public class ContestStateRosterLocked extends ContestState {

    public ContestStateRosterLocked() {
        this.setId(3);
        this.setName("roster_locked");
    }

    @Override
    public ContestState proceed() {
        return new ContestStateActive();
    }

    @Override
    public ContestState error() {
        return new ContestStateCancelled();
    }
}
