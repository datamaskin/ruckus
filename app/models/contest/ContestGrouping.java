package models.contest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import models.sports.League;
import play.data.validation.Constraints;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Model to represent the logical grouping of SportEvents for Contests based on the time they start.
 */
@Entity
public class ContestGrouping {

    public static final ContestGrouping NFL_FULL = new ContestGrouping(1, "Full (Th-Mon)", League.NFL);
    public static final ContestGrouping NFL_STANDARD = new ContestGrouping(2, "Standard (Sun-Mon)", League.NFL);
    public static final ContestGrouping NFL_LATE = new ContestGrouping(3, "Late (Sun PM-Mon)", League.NFL);
    public static final ContestGrouping MLB_ALL = new ContestGrouping(4, "All", League.MLB);
    public static final ContestGrouping MLB_EARLY = new ContestGrouping(5, "Early", League.MLB);
    public static final ContestGrouping MLB_LATE = new ContestGrouping(6, "Late", League.MLB);

    public static final ContestGrouping[] ALL = new ContestGrouping[]{
            NFL_FULL, NFL_STANDARD, NFL_LATE, MLB_ALL, MLB_EARLY, MLB_LATE
    };

    @Id
    private int id;

    @Constraints.Required
    @Column(nullable = false)
    @ManyToOne
    @JsonIgnore
    private League league;
    @Constraints.Required
    @Column(nullable = false)
    private String name;

    public ContestGrouping() {
    }

    public ContestGrouping(int id, String name, League league) {
        this.id = id;
        this.name = name;
        this.league = league;
    }

    public ContestGrouping(String name, League league) {
        this.name = name;
        this.league = league;
    }

    public League getLeague() {
        return league;
    }

    public void setLeague(League league) {
        this.league = league;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean equals(Object otherGrouping) {
        if (!(otherGrouping instanceof ContestGrouping)) {
            return false;
        }

        if (otherGrouping == this) {
            return true;
        }

        ContestGrouping e = (ContestGrouping) otherGrouping;
        if (e.league.equals(this.league) && e.name.equals(this.name)) {
            return true;
        }

        return false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
