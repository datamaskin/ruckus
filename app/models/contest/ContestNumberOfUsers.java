package models.contest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import models.sports.League;
import play.data.validation.Constraints;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Model representing the minimum and maximum number of users required for a contest.
 */
@Entity
public class ContestNumberOfUsers {

    @Id
    @JsonIgnore
    private int id;

    @ManyToOne
    @Column(nullable = false)
    @Constraints.Required
    @JsonIgnore
    private League league;

    @Constraints.Required
    @Constraints.Min(1)
    @Column(nullable = false)
    private int minimum;
    @Constraints.Required
    @Constraints.Min(2)
    @Column(nullable = false)
    private int maximum;

    public ContestNumberOfUsers() {
    }

    public ContestNumberOfUsers(League league, int min, int max) {
        this.league = league;
        this.minimum = min;
        this.maximum = max;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public League getLeague() {
        return league;
    }

    public void setLeague(League league) {
        this.league = league;
    }


    public int getMinimum() {
        return minimum;
    }

    public void setMinimum(int minimum) {
        this.minimum = minimum;
    }

    public int getMaximum() {
        return maximum;
    }

    public void setMaximum(int maximum) {
        this.maximum = maximum;
    }

    public boolean equals(Object otherNumberOfUsers) {
        if (!(otherNumberOfUsers instanceof ContestNumberOfUsers)) {
            return false;
        }

        if (otherNumberOfUsers == this) {
            return true;
        }

        ContestNumberOfUsers e = (ContestNumberOfUsers) otherNumberOfUsers;
        if (e.league.equals(this.league) && e.minimum == this.minimum && e.maximum == this.maximum) {
            return true;
        }

        return false;
    }
}
