package models.contest;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by mwalsh on 6/24/14.
 */

@Entity
@DiscriminatorValue("4")
public class ContestStateActive extends ContestState {
    public ContestStateActive() {
        this.setId(4);
        this.setName("active");
    }

    @Override
    public ContestState proceed() {
        return new ContestStateComplete();
    }

    @Override
    public ContestState error() {
        return new ContestStateCancelled();
    }

}
