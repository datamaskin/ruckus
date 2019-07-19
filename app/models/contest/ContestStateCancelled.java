package models.contest;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by mwalsh on 6/24/14.
 */

@Entity
@DiscriminatorValue("6")
public class ContestStateCancelled extends ContestState {
    public ContestStateCancelled() {
        this.setId(6);
        this.setName("cancelled");
    }

    @Override
    public ContestState proceed() {
        throw new IllegalArgumentException("ContestStateCancelled is terminal.");
    }

    @Override
    public ContestState error() {
        throw new IllegalArgumentException("ContestStateCancelled is terminal.");
    }

}
