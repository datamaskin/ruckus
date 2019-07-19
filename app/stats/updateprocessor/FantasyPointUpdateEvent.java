package stats.updateprocessor;

import models.sports.SportEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Model class to represent a sports event that results in a change in a athlete's fantasy point total.
 */
public class FantasyPointUpdateEvent {
    /**
     * The unit of time that this update took place.
     */
    private int currentUnitOfTime;

    /**
     * A plain-text description of the event that caused the point update.
     */
    private String eventDescription;

    /**
     * Score for the home team.
     */
    private int homeScore;

    /**
     * Score for the away team.
     */
    private int awayScore;

    /**
     * A reference to the sport event represented by this update.
     */
    private SportEvent sportEvent;

    /**
     * Indicator codes to help the UI determine whether the athlete is in a position to score fantasy points.
     */
    private Map<Integer, Integer> indicators;

    /*
     * Flag to indicate whether the received message is a stat correction or regular update.
     */
    private boolean isStatCorrection = false;

    /**
     * List of FantasyPointAthleteUpdateEvent for athletes involved in the event.
     */
    private List<FantasyPointAthleteUpdateEvent> fantasyPointAthleteUpdateEventList;

    public FantasyPointUpdateEvent() {
        indicators = new HashMap<>();
        fantasyPointAthleteUpdateEventList = new ArrayList<>();
    }

    public FantasyPointUpdateEvent(String eventDescription, int homeScore, int awayScore) {
        this.eventDescription = eventDescription;
        this.homeScore = homeScore;
        this.awayScore = awayScore;

        indicators = new HashMap<>();
        fantasyPointAthleteUpdateEventList = new ArrayList<>();
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public int getCurrentUnitOfTime() {
        return currentUnitOfTime;
    }

    public void setCurrentUnitOfTime(int currentUnitOfTime) {
        this.currentUnitOfTime = currentUnitOfTime;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public void setHomeScore(int homeScore) {
        this.homeScore = homeScore;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public void setAwayScore(int awayScore) {
        this.awayScore = awayScore;
    }

    public SportEvent getSportEvent() {
        return sportEvent;
    }

    public void setSportEvent(SportEvent sportEvent) {
        this.sportEvent = sportEvent;
    }

    public List<FantasyPointAthleteUpdateEvent> getFantasyPointAthleteUpdateEventList() {
        return fantasyPointAthleteUpdateEventList;
    }

    public void setFantasyPointAthleteUpdateEventList(List<FantasyPointAthleteUpdateEvent> fantasyPointAthleteUpdateEventList) {
        this.fantasyPointAthleteUpdateEventList = fantasyPointAthleteUpdateEventList;
    }

    public Map<Integer, Integer> getIndicators() {
        return indicators;
    }

    public void setIndicators(Map<Integer, Integer> indicators) {
        this.indicators = indicators;
    }

    public boolean isStatCorrection() {
        return isStatCorrection;
    }

    public void setStatCorrection(boolean isStatCorrection) {
        this.isStatCorrection = isStatCorrection;
    }
}
