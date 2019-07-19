package models.stats.nfl;

import models.sports.Athlete;
import models.sports.SportEvent;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Model class representing the data for a projection for an NFL Defense
 * in a given season/week.
 */
@Entity
public class StatsNflProjectionDefense {
    @Id
    private int id;

    @ManyToOne
    private Athlete athlete;

    @ManyToOne
    private SportEvent sportEvent;

    private int season;

    private int week;

    private Date startTime;

    private String teamName;

    private String opponentTeamName;

    private boolean isHome;

    @Column(columnDefinition = "TEXT")
    private String histFppAvgRange;

    @Column(columnDefinition = "TEXT")
    private String histOpponentOffenseFppAvg;

    private float actualFpp;
    private float projectedFpp;
    private float projectedFppMod;
    @Version
    private Timestamp lastUpdate;

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Athlete getAthlete() {
        return athlete;
    }

    public void setAthlete(Athlete athlete) {
        this.athlete = athlete;
    }

    public SportEvent getSportEvent() {
        return sportEvent;
    }

    public void setSportEvent(SportEvent sportEvent) {
        this.sportEvent = sportEvent;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getOpponentTeamName() {
        return opponentTeamName;
    }

    public void setOpponentTeamName(String opponentTeamName) {
        this.opponentTeamName = opponentTeamName;
    }

    public boolean isHome() {
        return isHome;
    }

    public void setHome(boolean isHome) {
        this.isHome = isHome;
    }

    public String getHistFppAvgRange() {
        return histFppAvgRange;
    }

    public void setHistFppAvgRange(String histFppAvgRange) {
        this.histFppAvgRange = histFppAvgRange;
    }

    public String getHistOpponentOffenseFppAvg() {
        return histOpponentOffenseFppAvg;
    }

    public void setHistOpponentOffenseFppAvg(String histOpponentOffenseFppAvg) {
        this.histOpponentOffenseFppAvg = histOpponentOffenseFppAvg;
    }

    public float getActualFpp() {
        return actualFpp;
    }

    public void setActualFpp(float actualFpp) {
        this.actualFpp = actualFpp;
    }

    public float getProjectedFpp() {
        return projectedFpp;
    }

    public void setProjectedFpp(float projectedFpp) {
        this.projectedFpp = projectedFpp;
    }

    public float getProjectedFppMod() {
        return projectedFppMod;
    }

    public void setProjectedFppMod(float projectedFppMod) {
        this.projectedFppMod = projectedFppMod;
    }
}
