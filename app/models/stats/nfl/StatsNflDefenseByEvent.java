package models.stats.nfl;

import models.sports.Athlete;
import models.sports.SportEvent;
import models.sports.Team;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by dmaclean on 8/5/14.
 */
@Entity
public class StatsNflDefenseByEvent {
    @Id
    private int id;

    @ManyToOne
    private Athlete athlete;

    @ManyToOne
    private SportEvent sportEvent;

    @ManyToOne
    private Team team;

    private int season;

    private int week;

    @ManyToOne
    private Team opponent;

    private int locationId;

    private Date startTime;

    private int eventTypeId;

    @Column(columnDefinition = "Decimal(10,2)")
    private BigDecimal fppInThisEvent;

    @Column(columnDefinition = "TEXT")
    private String fantasyPointsPerGameRange;

    @Column(columnDefinition = "TEXT")
    private String fantasyPointsAvgRange;

    private int interceptions = 0;
    private int fumbleRecoveries = 0;
    private int interceptionReturnTouchdowns = 0;
    private int fumbleRecoveryTouchdowns = 0;
    private int kickReturnTouchdowns = 0;
    private int puntReturnTouchdowns = 0;
    private int blockedPuntOrFieldGoalReturnTouchdowns = 0;
    private int safeties = 0;
    private int sacks = 0;
    private int blockedKicks = 0;
    private int pointsAllowed = 0;

    @Version
    private Timestamp lastUpdate;

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

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
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

    public Team getOpponent() {
        return opponent;
    }

    public void setOpponent(Team opponent) {
        this.opponent = opponent;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public BigDecimal getFppInThisEvent() {
        return fppInThisEvent;
    }

    public void setFppInThisEvent(BigDecimal fppInThisEvent) {
        this.fppInThisEvent = fppInThisEvent;
    }

    public String getFantasyPointsPerGameRange() {
        return fantasyPointsPerGameRange;
    }

    public void setFantasyPointsPerGameRange(String fantasyPointsPerGameRange) {
        this.fantasyPointsPerGameRange = fantasyPointsPerGameRange;
    }

    public String getFantasyPointsAvgRange() {
        return fantasyPointsAvgRange;
    }

    public void setFantasyPointsAvgRange(String fantasyPointsAvgRange) {
        this.fantasyPointsAvgRange = fantasyPointsAvgRange;
    }

    public int getInterceptions() {
        return interceptions;
    }

    public void setInterceptions(int interceptions) {
        this.interceptions = interceptions;
    }

    public int getFumbleRecoveries() {
        return fumbleRecoveries;
    }

    public void setFumbleRecoveries(int fumbleRecoveries) {
        this.fumbleRecoveries = fumbleRecoveries;
    }

    public int getInterceptionReturnTouchdowns() {
        return interceptionReturnTouchdowns;
    }

    public void setInterceptionReturnTouchdowns(int interceptionReturnTouchdowns) {
        this.interceptionReturnTouchdowns = interceptionReturnTouchdowns;
    }

    public int getFumbleRecoveryTouchdowns() {
        return fumbleRecoveryTouchdowns;
    }

    public void setFumbleRecoveryTouchdowns(int fumbleRecoveryTouchdowns) {
        this.fumbleRecoveryTouchdowns = fumbleRecoveryTouchdowns;
    }

    public int getKickReturnTouchdowns() {
        return kickReturnTouchdowns;
    }

    public void setKickReturnTouchdowns(int kickReturnTouchdowns) {
        this.kickReturnTouchdowns = kickReturnTouchdowns;
    }

    public int getPuntReturnTouchdowns() {
        return puntReturnTouchdowns;
    }

    public void setPuntReturnTouchdowns(int puntReturnTouchdowns) {
        this.puntReturnTouchdowns = puntReturnTouchdowns;
    }

    public int getBlockedPuntOrFieldGoalReturnTouchdowns() {
        return blockedPuntOrFieldGoalReturnTouchdowns;
    }

    public void setBlockedPuntOrFieldGoalReturnTouchdowns(int blockedPuntOrFieldGoalReturnTouchdowns) {
        this.blockedPuntOrFieldGoalReturnTouchdowns = blockedPuntOrFieldGoalReturnTouchdowns;
    }

    public int getSafeties() {
        return safeties;
    }

    public void setSafeties(int safeties) {
        this.safeties = safeties;
    }

    public int getSacks() {
        return sacks;
    }

    public void setSacks(int sacks) {
        this.sacks = sacks;
    }

    public int getBlockedKicks() {
        return blockedKicks;
    }

    public void setBlockedKicks(int blockedKicks) {
        this.blockedKicks = blockedKicks;
    }

    public int getPointsAllowed() {
        return pointsAllowed;
    }

    public void setPointsAllowed(int pointsAllowed) {
        this.pointsAllowed = pointsAllowed;
    }

    public int getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(int eventTypeId) {
        this.eventTypeId = eventTypeId;
    }
}
