package models.stats.nfl;

import models.sports.Athlete;
import models.sports.SportEvent;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by mgiles on 7/25/14.
 */
@Entity
public class StatsNflProjection {
    public static final String SPORT_EVENT_ID = "sport_event_id";
    public static final String ATHLETE_ID = "athlete_id";
    public static final String UNIQUE_KEY = "unique_key";

    @Id
    private int id;

    @Version
    private Timestamp lastUpdate;

    @ManyToOne
    @Column(name = ATHLETE_ID)
    private Athlete athlete;
    private int statsAthleteId;

    @ManyToOne
    @Column(name = SPORT_EVENT_ID)
    private SportEvent sportEvent;

    private String position = "";
    private int season = 0;
    private int week = 0;
    private Date startTime;
    private float actualFpp;
    private float projectedFpp;
    private float projectedFppMod;
    private float overUnder;
    private float pointSpread;
    private boolean participatedLastTwo = false;
    private int depth = 0;
    private int eventTypeId;
    private String depthPosition;

    @Column(unique = true)
    private String uniqueKey;
    @Column(columnDefinition = "TEXT")
    private String predFppAllowedQbAvgRange;
    @Column(columnDefinition = "TEXT")
    private String predFppAllowedWrAvgRange;
    @Column(columnDefinition = "TEXT")
    private String predFppAllowedRbAvgRange;
    @Column(columnDefinition = "TEXT")
    private String predFppAllowedTeAvgRange;
    @Column(columnDefinition = "TEXT")
    private String histFppAvgRange;
    @Column(columnDefinition = "TEXT")
    private String histPassAttemptsPercentMaxAvgRange;
    @Column(columnDefinition = "TEXT")
    private String histRecTargetsAvgRange;
    @Column(columnDefinition = "TEXT")
    private String histRecTargetsPercentMaxAvgRange;
    @Column(columnDefinition = "TEXT")
    private String histRushAttemptsPercentMaxAvgRange;
    @Column(columnDefinition = "TEXT")
    private String histPassingRatingAvgRange;
    @Column(columnDefinition = "TEXT")
    private String histRecYardsAvgRange;

    public String getDepthPosition() {
        return depthPosition;
    }

    public void setDepthPosition(String depthPosition) {
        this.depthPosition = depthPosition;
    }

    public int getDepth() {
        return depth;
    }

    public int getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(int eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public String getPredFppAllowedRbAvgRange() {
        return predFppAllowedRbAvgRange;
    }

    public void setPredFppAllowedRbAvgRange(String predFppAllowedRbAvgRange) {
        this.predFppAllowedRbAvgRange = predFppAllowedRbAvgRange;
    }

    public String getPredFppAllowedTeAvgRange() {
        return predFppAllowedTeAvgRange;
    }

    public void setPredFppAllowedTeAvgRange(String predFppAllowedTeAvgRange) {
        this.predFppAllowedTeAvgRange = predFppAllowedTeAvgRange;
    }

    public boolean isParticipatedLastTwo() {
        return participatedLastTwo;
    }

    public void setParticipatedLastTwo(boolean participatedLastTwo) {
        this.participatedLastTwo = participatedLastTwo;
    }

    public String getHistPassingRatingAvgRange() {
        return histPassingRatingAvgRange;
    }

    public void setHistPassingRatingAvgRange(String histPassingRatingAvgRange) {
        this.histPassingRatingAvgRange = histPassingRatingAvgRange;
    }

    public String getHistRecYardsAvgRange() {
        return histRecYardsAvgRange;
    }

    public void setHistRecYardsAvgRange(String histRecYardsAvgRange) {
        this.histRecYardsAvgRange = histRecYardsAvgRange;
    }

    public int getStatsAthleteId() {
        return statsAthleteId;
    }

    public void setStatsAthleteId(int statsAthleteId) {
        this.statsAthleteId = statsAthleteId;
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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public float getActualFpp() {
        return actualFpp;
    }

    public void setActualFpp(float actualFpp) {
        this.actualFpp = actualFpp;
    }

    public float getProjectedFppMod() {
        return projectedFppMod;
    }

    public void setProjectedFppMod(float projectedFppMod) {
        this.projectedFppMod = projectedFppMod;
    }

    public SportEvent getSportEvent() {
        return sportEvent;
    }

    public void setSportEvent(SportEvent sportEvent) {
        this.sportEvent = sportEvent;
    }

    public String getHistFppAvgRange() {
        return histFppAvgRange;
    }

    public void setHistFppAvgRange(String histFppAvgRange) {
        this.histFppAvgRange = histFppAvgRange;
    }

    public String getHistPassAttemptsPercentMaxAvgRange() {
        return histPassAttemptsPercentMaxAvgRange;
    }

    public void setHistPassAttemptsPercentMaxAvgRange(String histPassAttemptsPercentMaxAvgRange) {
        this.histPassAttemptsPercentMaxAvgRange = histPassAttemptsPercentMaxAvgRange;
    }

    public String getHistRecTargetsAvgRange() {
        return histRecTargetsAvgRange;
    }

    public void setHistRecTargetsAvgRange(String histRecTargetsAvgRange) {
        this.histRecTargetsAvgRange = histRecTargetsAvgRange;
    }

    public String getHistRecTargetsPercentMaxAvgRange() {
        return histRecTargetsPercentMaxAvgRange;
    }

    public void setHistRecTargetsPercentMaxAvgRange(String histRecTargetsPercentMaxAvgRange) {
        this.histRecTargetsPercentMaxAvgRange = histRecTargetsPercentMaxAvgRange;
    }

    public String getHistRushAttemptsPercentMaxAvgRange() {
        return histRushAttemptsPercentMaxAvgRange;
    }

    public void setHistRushAttemptsPercentMaxAvgRange(String histRushAttemptsPercentMaxAvgRange) {
        this.histRushAttemptsPercentMaxAvgRange = histRushAttemptsPercentMaxAvgRange;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Athlete getAthlete() {
        return athlete;
    }

    public void setAthlete(Athlete athlete) {
        this.athlete = athlete;
    }

    public String getPredFppAllowedQbAvgRange() {
        return predFppAllowedQbAvgRange;
    }

    public void setPredFppAllowedQbAvgRange(String predFppAllowedQbAvgRange) {
        this.predFppAllowedQbAvgRange = predFppAllowedQbAvgRange;
    }

    public String getPredFppAllowedWrAvgRange() {
        return predFppAllowedWrAvgRange;
    }

    public void setPredFppAllowedWrAvgRange(String predFppAllowedWrAvgRange) {
        this.predFppAllowedWrAvgRange = predFppAllowedWrAvgRange;
    }

    public float getProjectedFpp() {
        return projectedFpp;
    }

    public void setProjectedFpp(float projectedFpp) {
        this.projectedFpp = projectedFpp;
        this.projectedFppMod = projectedFpp;
    }

    public float getOverUnder() {
        return overUnder;
    }

    public void setOverUnder(float overUnder) {
        this.overUnder = overUnder;
    }

    public float getPointSpread() {
        return pointSpread;
    }

    public void setPointSpread(float pointSpread) {
        this.pointSpread = pointSpread;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
