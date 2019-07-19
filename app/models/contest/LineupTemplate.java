package models.contest;

import models.sports.League;
import models.sports.Position;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class LineupTemplate {

    public static final String LEAGUE_ID = "league_id";
    public static final String POSITION_ID = "position_id";
    public static final String NUMBER_OF_ATHLETES = "number_of_athletes";

    @Id
    private Integer id;

    @OneToOne
    @Column(nullable = false, name = LEAGUE_ID)
    private League league;

    @OneToOne
    @Column(nullable = false, name = POSITION_ID)
    private Position position;

    @Column(nullable = false, name = NUMBER_OF_ATHLETES)
    private int numberOfAthletes;

    public LineupTemplate(League league, Position position, int numberOfAthletes) {
        this.league = league;
        this.position = position;
        this.numberOfAthletes = numberOfAthletes;
    }

    public int getNumberOfAthletes() {
        return numberOfAthletes;
    }

    public void setNumberOfAthletes(int numberOfAthletes) {
        this.numberOfAthletes = numberOfAthletes;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public League getLeague() {
        return league;
    }

    public void setLeague(League league) {
        this.league = league;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
