package models.contest;

import models.user.User;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Created by mwalsh on 8/4/14.
 */
@Entity
public class ContestResults {

    @Id
    private long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Contest contest;

    @ManyToOne
    private Entry entry;

    private int payout;

    public ContestResults(User user, Contest contest, Entry entry, int payout) {
        this.user = user;
        this.contest = contest;
        this.entry = entry;
        this.payout = payout;
    }

    public long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Contest getContest() {
        return contest;
    }

    public Entry getEntry() {
        return entry;
    }

    public int getPayout() {
        return payout;
    }
}
