package models.contest;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by mwalsh on 6/24/14.
 */

@Entity
@DiscriminatorValue("10")
public class ContestStateUninitialized extends ContestState {

    public ContestStateUninitialized() {
        this.setId(10);
        this.setName("uninitialized");
    }

    @Override
    public ContestState proceed() {
        return new ContestStateOpen();
    }

    @Override
    public ContestState error() {
        return new ContestStateCancelled();
    }
}
