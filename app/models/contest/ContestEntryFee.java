package models.contest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import models.sports.League;
import play.data.validation.Constraints;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * The model for a contest entry fee.
 */
@Entity
public class ContestEntryFee {

    @Id
    private int id;

    @ManyToOne
    @Column(nullable = false)
    @Constraints.Required
    @JsonIgnore
    private League league;

    @Column(nullable = false)
    @Constraints.Min(0)
    private int entryFee;

    public ContestEntryFee() {
    }

    public ContestEntryFee(League league, int entryFee) {
        this.league = league;
        this.entryFee = entryFee;
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

    public int getEntryFee() {
        return entryFee;
    }

    public void setEntryFee(int entryFee) {
        this.entryFee = entryFee;
    }

    public boolean equals(Object otherEntryFee) {
        if (!(otherEntryFee instanceof ContestEntryFee)) {
            return false;
        }

        if (otherEntryFee == this) {
            return true;
        }

        ContestEntryFee e = (ContestEntryFee) otherEntryFee;
        if (e.league.equals(this.league) && e.entryFee == this.entryFee) {
            return true;
        }

        return false;
    }
}
