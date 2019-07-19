package models.contest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import models.sports.League;
import play.data.validation.Constraints;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Model to represent the available salary caps for contests in various leagues/sports.
 */
@Entity
public class ContestSalary {

    @Id
    private int id;

    @Constraints.Required
    @ManyToOne
    @Column(nullable = false)
    @JsonIgnore
    private League league;

    @Constraints.Min(1)
    @Column(nullable = false)
    private int salary;

    public ContestSalary() {
    }

    public ContestSalary(League league, int salary) {
        this.league = league;
        this.salary = salary;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
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

    public boolean equals(Object otherSalary) {
        if (!(otherSalary instanceof ContestSalary)) {
            return false;
        }

        if (otherSalary == this) {
            return true;
        }

        ContestSalary e = (ContestSalary) otherSalary;
        if (e.league.equals(this.league) && e.salary == this.salary) {
            return true;
        }

        return false;
    }
}
