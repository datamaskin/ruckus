package models.stats.nfl;

import models.sports.Athlete;
import models.sports.Team;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Created by mgiles on 8/20/14.
 */
@Entity
public class StatsNflDepthChart {
    public static final String TEAM_ID = "team_id";
    public static final String ATHLETE_ID = "athlete_id";

    @Id
    private int id;

    private int week = -1;
    private int season = -1;
    private int eventTypeId = -1;
    private int depth = 0;
    private String depthPosition;

    @ManyToOne
    @Column(name = TEAM_ID)
    private Team team;

    @ManyToOne
    @Column(name = ATHLETE_ID)
    private Athlete athlete;

    public String getDepthPosition() {
        return depthPosition;
    }

    public void setDepthPosition(String depthPosition) {
        this.depthPosition = depthPosition;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public int getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(int eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Athlete getAthlete() {
        return athlete;
    }

    public void setAthlete(Athlete athlete) {
        this.athlete = athlete;
    }
}
