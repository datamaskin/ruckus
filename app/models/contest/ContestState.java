package models.contest;

import javax.persistence.*;

/**
 * Created by mwalsh on 6/24/14.
 */

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class ContestState {

    public static final ContestStateOpen open                   = new ContestStateOpen();
    public static final ContestStateEntriesLocked locked        = new ContestStateEntriesLocked();
    public static final ContestStateRosterLocked rosterLocked   = new ContestStateRosterLocked();
    public static final ContestStateActive active               = new ContestStateActive();
    public static final ContestStateCancelled cancelled         = new ContestStateCancelled();
    public static final ContestStateComplete complete           = new ContestStateComplete();
    public static final ContestStateHistory history             = new ContestStateHistory();
    public static final ContestStateUninitialized uninitialized = new ContestStateUninitialized();
    public static final String NAME = "name";

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = NAME)
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract ContestState proceed();

    public abstract ContestState error();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContestState)) return false;

        ContestState that = (ContestState) o;

        if (id != that.id) return false;
        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        return result;
    }
}
