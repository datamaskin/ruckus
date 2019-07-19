package models.stats.nfl;

import models.sports.Athlete;
import models.sports.SportEvent;
import models.sports.Team;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by mgiles on 7/21/14.
 */
@Entity
public class StatsNflAthleteByEvent {
    public static final String SPORT_EVENT_ID = "sport_event_id";
    public static final String TEAM_ID = "team_id";
    public static final String ATHLETE_ID = "athlete_id";
    public static final String UNIQUE_KEY = "unique_key";

    @Id
    private int id;
    @Version
    private Timestamp lastUpdate;

    @ManyToOne
    @Column(name = ATHLETE_ID)
    private Athlete athlete;

    @ManyToOne
    @Column(name = SPORT_EVENT_ID)
    private SportEvent sportEvent;

    @ManyToOne
    @Column(name = TEAM_ID)
    private Team team;

    private String position = "";
    private int season = 0;
    private int week = 0;
    private int eventTypeId = 0;
    private Integer opponentId = 0;
    private Integer locationId = 0;
    private Date startTime;
    private Integer participationOffense = 0;
    private Integer participationDefense = 0;
    private Integer participationSpecialTeams = 0;

    @Column(unique = true)
    private String uniqueKey;
    @Column(columnDefinition = "Decimal(10,2)")
    private BigDecimal fppInThisEvent;
    @Column(columnDefinition = "TEXT")
    private String fantasyPointsPerGameRange = "";
    @Column(columnDefinition = "TEXT")
    private String fantasyPointsAvgRange = "";
    @Column(columnDefinition = "TEXT")
    private String predFppAllowedQbAvgRange;
    @Column(columnDefinition = "TEXT")
    private String predFppAllowedWrAvgRange;
    private Integer passingCompletions = 0;
    private float passingCompletionPercentage = 0;
    private Integer passingInterceptions = 0;
    private float passingYardsPerAttempt = 0;
    private Integer passingSacked = 0;
    private Integer passingSackedYardsLost = 0;
    private Integer passingLong = 0;
    private boolean passingIsLongTouchdown = false;
    private float passingRating = 0;
    @Column(columnDefinition = "TEXT")
    private String passingRatingPerGameRange = "";
    @Column(columnDefinition = "TEXT")
    private String passingRatingAvgRange = "";
    private Integer passingYardsAtCatch = 0;
    private float passingYardsAtCatchAverage = 0;
    private Integer passingYardsAfterCatch = 0;
    private float passingYardsAfterCatchAverage = 0;
    private Integer passingAttempts = 0;
    @Column(columnDefinition = "TEXT")
    private String passingAttemptsPerGameRange = "";
    @Column(columnDefinition = "TEXT")
    private String passingAttemptsPercentOfMaxPerGameRange = "";
    @Column(columnDefinition = "TEXT")
    private String passingAttemptsAvgRange = "";
    @Column(columnDefinition = "TEXT")
    private String passingAttemptsPercentOfMaxAvgRange = "";
    private Integer passingYards = 0;
    @Column(columnDefinition = "TEXT")
    private String passingYardsPerGameRange = "";
    @Column(columnDefinition = "TEXT")
    private String passingYardsAvgRange = "";
    private Integer receivingReceptions = 0;
    private float receivingAverage = 0;
    private Integer receivingLong = 0;
    private boolean receivingIsLongTouchdown = false;
    private Integer receivingYardsAtCatch = 0;
    private float receivingYardsAtCatchAverage = 0;
    private Integer receivingYardsAfterCatch = 0;
    private float receivingYardsAfterCatchAverage = 0;
    private Integer receivingYards = 0;
    @Column(columnDefinition = "TEXT")
    private String receivingYardsPerGameRange = "";
    @Column(columnDefinition = "TEXT")
    private String receivingYardsAvgRange = "";
    private Integer receivingTargets = 0;
    @Column(columnDefinition = "TEXT")
    private String receivingTargetsPerGameRange = "";
    @Column(columnDefinition = "TEXT")
    private String receivingTargetsPercentOfMaxPerGameRange = "";
    @Column(columnDefinition = "TEXT")
    private String receivingTargetsAvgRange = "";
    @Column(columnDefinition = "TEXT")
    private String receivingTargetsPercentOfMaxAvgRange = "";
    private float rushingAverage = 0;
    private Integer rushingLong = 0;
    private boolean rushingIsLongTouchdown = false;
    private Integer rushingStuffed = 0;
    private Integer rushingStuffedYardsLost = 0;
    private float rushingStuffedPercentage = 0;
    private Integer rushingAttempts = 0;
    @Column(columnDefinition = "TEXT")
    private String rushingAttemptsPerGameRange = "";
    @Column(columnDefinition = "TEXT")
    private String rushingAttemptsPercentOfMaxPerGameRange = "";
    @Column(columnDefinition = "TEXT")
    private String rushingAttemptsAvgRange = "";
    @Column(columnDefinition = "TEXT")
    private String rushingAttemptsPercentOfMaxAvgRange = "";
    private Integer rushingYards = 0;
    @Column(columnDefinition = "TEXT")
    private String rushingYardsPerGameRange = "";
    @Column(columnDefinition = "TEXT")
    private String rushingYardsAvgRange = "";
    private Integer passingTouchdowns = 0;
    private Integer rushingTouchdowns = 0;
    private Integer receivingTouchdowns = 0;
    private Integer puntReturningTouchdowns = 0;
    private Integer kickoffReturningTouchdowns = 0;
    @Column(columnDefinition = "TEXT")
    private String touchDownsPerGameRange = "";
    @Column(columnDefinition = "TEXT")
    private String touchDownsAvgRange = "";
    @Column(columnDefinition = "TEXT")
    private String opponentPointsAllowedAtPositionPerGameRange = "";
    @Column(columnDefinition = "TEXT")
    private String opponentPointsAllowedAtPositionAvgRange = "";
    private Integer firstDownsTotal = 0;
    private Integer firstDownsRushing = 0;
    private Integer firstDownsPassing = 0;
    private Integer firstDownsReceiving = 0;
    private Integer firstDownsPenalty = 0;
    private Integer fumblesTotal = 0;
    private Integer fumblesPass = 0;
    private Integer fumblesRush = 0;
    private Integer fumblesSpecialTeams = 0;
    private Integer fumblesReceiving = 0;
    private Integer fumblesDefense = 0;
    private Integer fumblesMisc = 0;
    private Integer fumblesLostTotal = 0;
    private Integer fumblesLostPass = 0;
    private Integer fumblesLostRush = 0;
    private Integer fumblesLostSpecialTeams = 0;
    private Integer fumblesLostReceiving = 0;
    private Integer fumblesLostDefense = 0;
    private Integer fumblesLostMisc = 0;
    private Integer twoPointConversionsMade = 0;
    private Integer twoPointConversionsPasses = 0;
    private Integer twoPointConversionsAttempts = 0;
    private Integer penaltiesNumber = 0;
    private Integer penaltiesYards = 0;
    private Integer penaltiesFalseStart = 0;
    private Integer penaltiesHolding = 0;
    private Integer kickoffsNumber = 0;
    private Integer kickoffsEndZone = 0;
    private float kickoffsTouchbackPercentage = 0;
    private Integer kickoffsYards = 0;
    private float kickoffsAverage = 0;
    private Integer kickoffsReturns = 0;
    private Integer kickoffsReturnYards = 0;
    private float kickoffsReturnAverage = 0;
    private Integer kickoffsTouchbacks = 0;
    private Integer kickingExtraPointsMade = 0;
    private Integer kickingExtraPointsAttempts = 0;
    private Integer kickingExtraPointsBlocked = 0;
    private float kickingExtraPointsPercentage = 0;
    private Integer kickingLong = 0;
    private Integer kickingPoints = 0;
    private Integer kickingFieldGoalsMade0to19 = 0;
    private Integer kickingFieldGoalsAttempts0to19 = 0;
    private Integer kickingFieldGoalsBlocked0to19 = 0;
    private float kickingFieldGoalsPercentage0to19 = 0;
    private Integer kickingFieldGoalsMade20to29 = 0;
    private Integer kickingFieldGoalsAttempts20to29 = 0;
    private Integer kickingFieldGoalsBlocked20to29 = 0;
    private float kickingFieldGoalsPercentage20to29 = 0;
    private Integer kickingFieldGoalsMade30to39 = 0;
    private Integer kickingFieldGoalsAttempts30to39 = 0;
    private Integer kickingFieldGoalsBlocked30to39 = 0;
    private float kickingFieldGoalsPercentage30to39 = 0;
    private Integer kickingFieldGoalsMade40to49 = 0;
    private Integer kickingFieldGoalsAttempts40to49 = 0;
    private Integer kickingFieldGoalsBlocked40to49 = 0;
    private float kickingFieldGoalsPercentage40to49 = 0;
    private Integer kickingFieldGoalsMade50Plus = 0;
    private Integer kickingFieldGoalsAttempts50Plus = 0;
    private Integer kickingFieldGoalsBlocked50Plus = 0;
    private float kickingFieldGoalsPercentage50Plus = 0;
    private Integer puntReturningReturns = 0;
    private Integer puntReturningYards = 0;
    private float puntReturningAverage = 0;
    private Integer puntReturningFairCatches = 0;
    private Integer puntReturningLong = 0;
    private boolean puntReturningIsLongTouchdown = false;
    private Integer kickoffReturningReturns = 0;
    private Integer kickoffReturningYards = 0;
    private float kickoffReturningAverage = 0;
    private Integer kickoffReturningFairCatches = 0;
    private Integer kickoffReturningLong = 0;
    private boolean kickoffReturningIsLongTouchdown = false;

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

    public String getPassingRatingPerGameRange() {
        return passingRatingPerGameRange;
    }

    public void setPassingRatingPerGameRange(String passingRatingPerGameRange) {
        this.passingRatingPerGameRange = passingRatingPerGameRange;
    }

    public String getPassingRatingAvgRange() {
        return passingRatingAvgRange;
    }

    public void setPassingRatingAvgRange(String passingRatingAvgRange) {
        this.passingRatingAvgRange = passingRatingAvgRange;
    }

    public int getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(int eventTypeId) {
        this.eventTypeId = eventTypeId;
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

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public String getReceivingTargetsPercentOfMaxPerGameRange() {
        return receivingTargetsPercentOfMaxPerGameRange;
    }

    public void setReceivingTargetsPercentOfMaxPerGameRange(String receivingTargetsPercentOfMaxPerGameRange) {
        this.receivingTargetsPercentOfMaxPerGameRange = receivingTargetsPercentOfMaxPerGameRange;
    }

    public String getRushingAttemptsPercentOfMaxPerGameRange() {
        return rushingAttemptsPercentOfMaxPerGameRange;
    }

    public void setRushingAttemptsPercentOfMaxPerGameRange(String rushingAttemptsPercentOfMaxPerGameRange) {
        this.rushingAttemptsPercentOfMaxPerGameRange = rushingAttemptsPercentOfMaxPerGameRange;
    }

    public String getPassingAttemptsPercentOfMaxPerGameRange() {
        return passingAttemptsPercentOfMaxPerGameRange;
    }

    public void setPassingAttemptsPercentOfMaxPerGameRange(String passingAttemptsPercentOfMaxPerGameRange) {
        this.passingAttemptsPercentOfMaxPerGameRange = passingAttemptsPercentOfMaxPerGameRange;
    }

    public String getReceivingTargetsPercentOfMaxAvgRange() {
        return receivingTargetsPercentOfMaxAvgRange;
    }

    public void setReceivingTargetsPercentOfMaxAvgRange(String receivingTargetsPercentOfMaxAvgRange) {
        this.receivingTargetsPercentOfMaxAvgRange = receivingTargetsPercentOfMaxAvgRange;
    }

    public String getRushingAttemptsPercentOfMaxAvgRange() {
        return rushingAttemptsPercentOfMaxAvgRange;
    }

    public void setRushingAttemptsPercentOfMaxAvgRange(String rushingAttemptsPercentOfMaxAvgRange) {
        this.rushingAttemptsPercentOfMaxAvgRange = rushingAttemptsPercentOfMaxAvgRange;
    }

    public String getPassingAttemptsPercentOfMaxAvgRange() {
        return passingAttemptsPercentOfMaxAvgRange;
    }

    public void setPassingAttemptsPercentOfMaxAvgRange(String passingAttemptsPercentOfMaxAvgRange) {
        this.passingAttemptsPercentOfMaxAvgRange = passingAttemptsPercentOfMaxAvgRange;
    }

    public SportEvent getSportEvent() {
        return sportEvent;
    }

    public void setSportEvent(SportEvent sportEvent) {
        this.sportEvent = sportEvent;
    }

    public Integer getOpponentId() {
        return opponentId;
    }

    public void setOpponentId(Integer opponentId) {
        this.opponentId = opponentId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getOpponentPointsAllowedAtPositionAvgRange() {
        return opponentPointsAllowedAtPositionAvgRange;
    }

    public void setOpponentPointsAllowedAtPositionAvgRange(String opponentPointsAllowedAtPositionAvgRange) {
        this.opponentPointsAllowedAtPositionAvgRange = opponentPointsAllowedAtPositionAvgRange;
    }

    public String getOpponentPointsAllowedAtPositionPerGameRange() {
        return opponentPointsAllowedAtPositionPerGameRange;
    }

    public void setOpponentPointsAllowedAtPositionPerGameRange(String opponentPointsAllowedAtPositionPerGameRange) {
        this.opponentPointsAllowedAtPositionPerGameRange = opponentPointsAllowedAtPositionPerGameRange;
    }

    public String getPassingAttemptsPerGameRange() {
        return passingAttemptsPerGameRange;
    }

    public void setPassingAttemptsPerGameRange(String passingAttemptsPerGameRange) {
        this.passingAttemptsPerGameRange = passingAttemptsPerGameRange;
    }

    public String getPassingAttemptsAvgRange() {
        return passingAttemptsAvgRange;
    }

    public void setPassingAttemptsAvgRange(String passingAttemptsAvgRange) {
        this.passingAttemptsAvgRange = passingAttemptsAvgRange;
    }

    public String getTouchDownsPerGameRange() {
        return touchDownsPerGameRange;
    }

    public void setTouchDownsPerGameRange(String touchDownsPerGameRange) {
        this.touchDownsPerGameRange = touchDownsPerGameRange;
    }

    public String getPassingYardsPerGameRange() {
        return passingYardsPerGameRange;
    }

    public void setPassingYardsPerGameRange(String passingYardsPerGameRange) {
        this.passingYardsPerGameRange = passingYardsPerGameRange;
    }

    public String getRushingYardsPerGameRange() {
        return rushingYardsPerGameRange;
    }

    public void setRushingYardsPerGameRange(String rushingYardsPerGameRange) {
        this.rushingYardsPerGameRange = rushingYardsPerGameRange;
    }

    public String getReceivingYardsPerGameRange() {
        return receivingYardsPerGameRange;
    }

    public void setReceivingYardsPerGameRange(String receivingYardsPerGameRange) {
        this.receivingYardsPerGameRange = receivingYardsPerGameRange;
    }

    public Athlete getAthlete() {
        return athlete;
    }

    public void setAthlete(Athlete athlete) {
        this.athlete = athlete;
    }

    public BigDecimal getFppInThisEvent() {
        return fppInThisEvent;
    }

    public void setFppInThisEvent(BigDecimal fppInThisEvent) {
        this.fppInThisEvent = fppInThisEvent;
    }

    public String getReceivingTargetsPerGameRange() {
        return receivingTargetsPerGameRange;
    }

    public void setReceivingTargetsPerGameRange(String receivingTargetsPerGameRange) {
        this.receivingTargetsPerGameRange = receivingTargetsPerGameRange;
    }

    public String getReceivingTargetsAvgRange() {
        return receivingTargetsAvgRange;
    }

    public void setReceivingTargetsAvgRange(String receivingTargetsAvgRange) {
        this.receivingTargetsAvgRange = receivingTargetsAvgRange;
    }

    public String getRushingAttemptsPerGameRange() {
        return rushingAttemptsPerGameRange;
    }

    public void setRushingAttemptsPerGameRange(String rushingAttemptsPerGameRange) {
        this.rushingAttemptsPerGameRange = rushingAttemptsPerGameRange;
    }

    public String getRushingAttemptsAvgRange() {
        return rushingAttemptsAvgRange;
    }

    public void setRushingAttemptsAvgRange(String rushingAttemptsAvgRange) {
        this.rushingAttemptsAvgRange = rushingAttemptsAvgRange;
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

    public String getTouchDownsAvgRange() {
        return touchDownsAvgRange;
    }

    public void setTouchDownsAvgRange(String touchDownsAvgRange) {
        this.touchDownsAvgRange = touchDownsAvgRange;
    }

    public String getPassingYardsAvgRange() {
        return passingYardsAvgRange;
    }

    public void setPassingYardsAvgRange(String passingYardsAvgRange) {
        this.passingYardsAvgRange = passingYardsAvgRange;
    }

    public String getRushingYardsAvgRange() {
        return rushingYardsAvgRange;
    }

    public void setRushingYardsAvgRange(String rushingYardsAvgRange) {
        this.rushingYardsAvgRange = rushingYardsAvgRange;
    }

    public String getReceivingYardsAvgRange() {
        return receivingYardsAvgRange;
    }

    public void setReceivingYardsAvgRange(String receivingYardsAvgRange) {
        this.receivingYardsAvgRange = receivingYardsAvgRange;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Integer getKickoffReturningReturns() {
        return kickoffReturningReturns;
    }

    public void setKickoffReturningReturns(Integer kickoffReturningReturns) {

        this.kickoffReturningReturns = kickoffReturningReturns;
    }

    public Integer getKickoffReturningYards() {
        return kickoffReturningYards;
    }

    public void setKickoffReturningYards(Integer kickoffReturningYards) {
        this.kickoffReturningYards = kickoffReturningYards;
    }

    public float getKickoffReturningAverage() {
        return kickoffReturningAverage;
    }

    public void setKickoffReturningAverage(float kickoffReturningAverage) {
        this.kickoffReturningAverage = kickoffReturningAverage;
    }

    public Integer getKickoffReturningFairCatches() {
        return kickoffReturningFairCatches;
    }

    public void setKickoffReturningFairCatches(Integer kickoffReturningFairCatches) {
        this.kickoffReturningFairCatches = kickoffReturningFairCatches;
    }

    public Integer getKickoffReturningTouchdowns() {
        return kickoffReturningTouchdowns;
    }

    public void setKickoffReturningTouchdowns(Integer kickoffReturningTouchdowns) {
        this.kickoffReturningTouchdowns = kickoffReturningTouchdowns;
    }

    public Integer getKickoffReturningLong() {
        return kickoffReturningLong;
    }

    public void setKickoffReturningLong(Integer kickoffReturningLong) {
        this.kickoffReturningLong = kickoffReturningLong;
    }

    public boolean isKickoffReturningIsLongTouchdown() {
        return kickoffReturningIsLongTouchdown;
    }

    public void setKickoffReturningIsLongTouchdown(boolean kickoffReturningIsLongTouchdown) {
        this.kickoffReturningIsLongTouchdown = kickoffReturningIsLongTouchdown;
    }

    public Integer getPuntReturningReturns() {
        return puntReturningReturns;
    }

    public void setPuntReturningReturns(Integer puntReturningReturns) {
        this.puntReturningReturns = puntReturningReturns;
    }

    public Integer getPuntReturningYards() {
        return puntReturningYards;
    }

    public void setPuntReturningYards(Integer puntReturningYards) {
        this.puntReturningYards = puntReturningYards;
    }

    public float getPuntReturningAverage() {
        return puntReturningAverage;
    }

    public void setPuntReturningAverage(float puntReturningAverage) {
        this.puntReturningAverage = puntReturningAverage;
    }

    public Integer getPuntReturningFairCatches() {
        return puntReturningFairCatches;
    }

    public void setPuntReturningFairCatches(Integer puntReturningFairCatches) {
        this.puntReturningFairCatches = puntReturningFairCatches;
    }

    public Integer getPuntReturningTouchdowns() {
        return puntReturningTouchdowns;
    }

    public void setPuntReturningTouchdowns(Integer puntReturningTouchdowns) {
        this.puntReturningTouchdowns = puntReturningTouchdowns;
    }

    public Integer getPuntReturningLong() {
        return puntReturningLong;
    }

    public void setPuntReturningLong(Integer puntReturningLong) {
        this.puntReturningLong = puntReturningLong;
    }

    public boolean isPuntReturningIsLongTouchdown() {
        return puntReturningIsLongTouchdown;
    }

    public void setPuntReturningIsLongTouchdown(boolean puntReturningIsLongTouchdown) {
        this.puntReturningIsLongTouchdown = puntReturningIsLongTouchdown;
    }

    public Integer getKickingExtraPointsMade() {
        return kickingExtraPointsMade;
    }

    public void setKickingExtraPointsMade(Integer kickingExtraPointsMade) {
        this.kickingExtraPointsMade = kickingExtraPointsMade;
    }

    public Integer getKickingExtraPointsAttempts() {
        return kickingExtraPointsAttempts;
    }

    public void setKickingExtraPointsAttempts(Integer kickingExtraPointsAttempts) {
        this.kickingExtraPointsAttempts = kickingExtraPointsAttempts;
    }

    public Integer getKickingExtraPointsBlocked() {
        return kickingExtraPointsBlocked;
    }

    public void setKickingExtraPointsBlocked(Integer kickingExtraPointsBlocked) {
        this.kickingExtraPointsBlocked = kickingExtraPointsBlocked;
    }

    public float getKickingExtraPointsPercentage() {
        return kickingExtraPointsPercentage;
    }

    public void setKickingExtraPointsPercentage(float kickingExtraPointsPercentage) {
        this.kickingExtraPointsPercentage = kickingExtraPointsPercentage;
    }

    public Integer getKickingLong() {
        return kickingLong;
    }

    public void setKickingLong(Integer kickingLong) {
        this.kickingLong = kickingLong;
    }

    public Integer getKickingPoints() {
        return kickingPoints;
    }

    public void setKickingPoints(Integer kickingPoints) {
        this.kickingPoints = kickingPoints;
    }

    public Integer getKickingFieldGoalsMade0to19() {
        return kickingFieldGoalsMade0to19;
    }

    public void setKickingFieldGoalsMade0to19(Integer kickingFieldGoalsMade0to19) {
        this.kickingFieldGoalsMade0to19 = kickingFieldGoalsMade0to19;
    }

    public Integer getKickingFieldGoalsAttempts0to19() {
        return kickingFieldGoalsAttempts0to19;
    }

    public void setKickingFieldGoalsAttempts0to19(Integer kickingFieldGoalsAttempts0to19) {
        this.kickingFieldGoalsAttempts0to19 = kickingFieldGoalsAttempts0to19;
    }

    public Integer getKickingFieldGoalsBlocked0to19() {
        return kickingFieldGoalsBlocked0to19;
    }

    public void setKickingFieldGoalsBlocked0to19(Integer kickingFieldGoalsBlocked0to19) {
        this.kickingFieldGoalsBlocked0to19 = kickingFieldGoalsBlocked0to19;
    }

    public float getKickingFieldGoalsPercentage0to19() {
        return kickingFieldGoalsPercentage0to19;
    }

    public void setKickingFieldGoalsPercentage0to19(float kickingFieldGoalsPercentage0to19) {
        this.kickingFieldGoalsPercentage0to19 = kickingFieldGoalsPercentage0to19;
    }

    public Integer getKickingFieldGoalsMade20to29() {
        return kickingFieldGoalsMade20to29;
    }

    public void setKickingFieldGoalsMade20to29(Integer kickingFieldGoalsMade20to29) {
        this.kickingFieldGoalsMade20to29 = kickingFieldGoalsMade20to29;
    }

    public Integer getKickingFieldGoalsAttempts20to29() {
        return kickingFieldGoalsAttempts20to29;
    }

    public void setKickingFieldGoalsAttempts20to29(Integer kickingFieldGoalsAttempts20to29) {
        this.kickingFieldGoalsAttempts20to29 = kickingFieldGoalsAttempts20to29;
    }

    public Integer getKickingFieldGoalsBlocked20to29() {
        return kickingFieldGoalsBlocked20to29;
    }

    public void setKickingFieldGoalsBlocked20to29(Integer kickingFieldGoalsBlocked20to29) {
        this.kickingFieldGoalsBlocked20to29 = kickingFieldGoalsBlocked20to29;
    }

    public float getKickingFieldGoalsPercentage20to29() {
        return kickingFieldGoalsPercentage20to29;
    }

    public void setKickingFieldGoalsPercentage20to29(float kickingFieldGoalsPercentage20to29) {
        this.kickingFieldGoalsPercentage20to29 = kickingFieldGoalsPercentage20to29;
    }

    public Integer getKickingFieldGoalsMade30to39() {
        return kickingFieldGoalsMade30to39;
    }

    public void setKickingFieldGoalsMade30to39(Integer kickingFieldGoalsMade30to39) {
        this.kickingFieldGoalsMade30to39 = kickingFieldGoalsMade30to39;
    }

    public Integer getKickingFieldGoalsAttempts30to39() {
        return kickingFieldGoalsAttempts30to39;
    }

    public void setKickingFieldGoalsAttempts30to39(Integer kickingFieldGoalsAttempts30to39) {
        this.kickingFieldGoalsAttempts30to39 = kickingFieldGoalsAttempts30to39;
    }

    public Integer getKickingFieldGoalsBlocked30to39() {
        return kickingFieldGoalsBlocked30to39;
    }

    public void setKickingFieldGoalsBlocked30to39(Integer kickingFieldGoalsBlocked30to39) {
        this.kickingFieldGoalsBlocked30to39 = kickingFieldGoalsBlocked30to39;
    }

    public float getKickingFieldGoalsPercentage30to39() {
        return kickingFieldGoalsPercentage30to39;
    }

    public void setKickingFieldGoalsPercentage30to39(float kickingFieldGoalsPercentage30to39) {
        this.kickingFieldGoalsPercentage30to39 = kickingFieldGoalsPercentage30to39;
    }

    public Integer getKickingFieldGoalsMade40to49() {
        return kickingFieldGoalsMade40to49;
    }

    public void setKickingFieldGoalsMade40to49(Integer kickingFieldGoalsMade40to49) {
        this.kickingFieldGoalsMade40to49 = kickingFieldGoalsMade40to49;
    }

    public Integer getKickingFieldGoalsAttempts40to49() {
        return kickingFieldGoalsAttempts40to49;
    }

    public void setKickingFieldGoalsAttempts40to49(Integer kickingFieldGoalsAttempts40to49) {
        this.kickingFieldGoalsAttempts40to49 = kickingFieldGoalsAttempts40to49;
    }

    public Integer getKickingFieldGoalsBlocked40to49() {
        return kickingFieldGoalsBlocked40to49;
    }

    public void setKickingFieldGoalsBlocked40to49(Integer kickingFieldGoalsBlocked40to49) {
        this.kickingFieldGoalsBlocked40to49 = kickingFieldGoalsBlocked40to49;
    }

    public float getKickingFieldGoalsPercentage40to49() {
        return kickingFieldGoalsPercentage40to49;
    }

    public void setKickingFieldGoalsPercentage40to49(float kickingFieldGoalsPercentage40to49) {
        this.kickingFieldGoalsPercentage40to49 = kickingFieldGoalsPercentage40to49;
    }

    public Integer getKickingFieldGoalsMade50Plus() {
        return kickingFieldGoalsMade50Plus;
    }

    public void setKickingFieldGoalsMade50Plus(Integer kickingFieldGoalsMade50Plus) {
        this.kickingFieldGoalsMade50Plus = kickingFieldGoalsMade50Plus;
    }

    public Integer getKickingFieldGoalsAttempts50Plus() {
        return kickingFieldGoalsAttempts50Plus;
    }

    public void setKickingFieldGoalsAttempts50Plus(Integer kickingFieldGoalsAttempts50Plus) {
        this.kickingFieldGoalsAttempts50Plus = kickingFieldGoalsAttempts50Plus;
    }

    public Integer getKickingFieldGoalsBlocked50Plus() {
        return kickingFieldGoalsBlocked50Plus;
    }

    public void setKickingFieldGoalsBlocked50Plus(Integer kickingFieldGoalsBlocked50Plus) {
        this.kickingFieldGoalsBlocked50Plus = kickingFieldGoalsBlocked50Plus;
    }

    public float getKickingFieldGoalsPercentage50Plus() {
        return kickingFieldGoalsPercentage50Plus;
    }

    public void setKickingFieldGoalsPercentage50Plus(float kickingFieldGoalsPercentage50Plus) {
        this.kickingFieldGoalsPercentage50Plus = kickingFieldGoalsPercentage50Plus;
    }

    public Integer getKickoffsTouchbacks() {
        return kickoffsTouchbacks;
    }

    public void setKickoffsTouchbacks(Integer kickoffsTouchbacks) {
        this.kickoffsTouchbacks = kickoffsTouchbacks;
    }

    public Integer getKickoffsNumber() {
        return kickoffsNumber;
    }

    public void setKickoffsNumber(Integer kickoffsNumber) {
        this.kickoffsNumber = kickoffsNumber;
    }

    public Integer getKickoffsEndZone() {
        return kickoffsEndZone;
    }

    public void setKickoffsEndZone(Integer kickoffsEndZone) {
        this.kickoffsEndZone = kickoffsEndZone;
    }

    public float getKickoffsTouchbackPercentage() {
        return kickoffsTouchbackPercentage;
    }

    public void setKickoffsTouchbackPercentage(float kickoffsTouchbackPercentage) {
        this.kickoffsTouchbackPercentage = kickoffsTouchbackPercentage;
    }

    public Integer getKickoffsYards() {
        return kickoffsYards;
    }

    public void setKickoffsYards(Integer kickoffsYards) {
        this.kickoffsYards = kickoffsYards;
    }

    public float getKickoffsAverage() {
        return kickoffsAverage;
    }

    public void setKickoffsAverage(float kickoffsAverage) {
        this.kickoffsAverage = kickoffsAverage;
    }

    public Integer getKickoffsReturns() {
        return kickoffsReturns;
    }

    public void setKickoffsReturns(Integer kickoffsReturns) {
        this.kickoffsReturns = kickoffsReturns;
    }

    public Integer getKickoffsReturnYards() {
        return kickoffsReturnYards;
    }

    public void setKickoffsReturnYards(Integer kickoffsReturnYards) {
        this.kickoffsReturnYards = kickoffsReturnYards;
    }

    public float getKickoffsReturnAverage() {
        return kickoffsReturnAverage;
    }

    public void setKickoffsReturnAverage(float kickoffsReturnAverage) {
        this.kickoffsReturnAverage = kickoffsReturnAverage;
    }

    public Integer getPenaltiesNumber() {
        return penaltiesNumber;
    }

    public void setPenaltiesNumber(Integer penaltiesNumber) {
        this.penaltiesNumber = penaltiesNumber;
    }

    public Integer getPenaltiesYards() {
        return penaltiesYards;
    }

    public void setPenaltiesYards(Integer penaltiesYards) {
        this.penaltiesYards = penaltiesYards;
    }

    public Integer getPenaltiesFalseStart() {
        return penaltiesFalseStart;
    }

    public void setPenaltiesFalseStart(Integer penaltiesFalseStart) {
        this.penaltiesFalseStart = penaltiesFalseStart;
    }

    public Integer getPenaltiesHolding() {
        return penaltiesHolding;
    }

    public void setPenaltiesHolding(Integer penaltiesHolding) {
        this.penaltiesHolding = penaltiesHolding;
    }

    public Integer getTwoPointConversionsMade() {
        return twoPointConversionsMade;
    }

    public void setTwoPointConversionsMade(Integer twoPointConversionsMade) {
        this.twoPointConversionsMade = twoPointConversionsMade;
    }

    public Integer getTwoPointConversionsPasses() {
        return twoPointConversionsPasses;
    }

    public void setTwoPointConversionsPasses(Integer twoPointConversionsPasses) {
        this.twoPointConversionsPasses = twoPointConversionsPasses;
    }

    public Integer getTwoPointConversionsAttempts() {
        return twoPointConversionsAttempts;
    }

    public void setTwoPointConversionsAttempts(Integer twoPointConversionsAttempts) {
        this.twoPointConversionsAttempts = twoPointConversionsAttempts;
    }

    public float getReceivingYardsAfterCatchAverage() {
        return receivingYardsAfterCatchAverage;
    }

    public void setReceivingYardsAfterCatchAverage(float receivingYardsAfterCatchAverage) {
        this.receivingYardsAfterCatchAverage = receivingYardsAfterCatchAverage;
    }

    public Integer getReceivingReceptions() {
        return receivingReceptions;
    }

    public void setReceivingReceptions(Integer receivingReceptions) {
        this.receivingReceptions = receivingReceptions;
    }

    public Integer getReceivingYards() {
        return receivingYards;
    }

    public void setReceivingYards(Integer receivingYards) {
        this.receivingYards = receivingYards;
    }

    public float getReceivingAverage() {
        return receivingAverage;
    }

    public void setReceivingAverage(float receivingAverage) {
        this.receivingAverage = receivingAverage;
    }

    public Integer getReceivingLong() {
        return receivingLong;
    }

    public void setReceivingLong(Integer receivingLong) {
        this.receivingLong = receivingLong;
    }

    public boolean isReceivingIsLongTouchdown() {
        return receivingIsLongTouchdown;
    }

    public void setReceivingIsLongTouchdown(boolean receivingIsLongTouchdown) {
        this.receivingIsLongTouchdown = receivingIsLongTouchdown;
    }

    public Integer getReceivingTouchdowns() {
        return receivingTouchdowns;
    }

    public void setReceivingTouchdowns(Integer receivingTouchdowns) {
        this.receivingTouchdowns = receivingTouchdowns;
    }

    public Integer getReceivingTargets() {
        return receivingTargets;
    }

    public void setReceivingTargets(Integer receivingTargets) {
        this.receivingTargets = receivingTargets;
    }

    public Integer getReceivingYardsAtCatch() {
        return receivingYardsAtCatch;
    }

    public void setReceivingYardsAtCatch(Integer receivingYardsAtCatch) {
        this.receivingYardsAtCatch = receivingYardsAtCatch;
    }

    public float getReceivingYardsAtCatchAverage() {
        return receivingYardsAtCatchAverage;
    }

    public void setReceivingYardsAtCatchAverage(float receivingYardsAtCatchAverage) {
        this.receivingYardsAtCatchAverage = receivingYardsAtCatchAverage;
    }

    public Integer getReceivingYardsAfterCatch() {
        return receivingYardsAfterCatch;
    }

    public void setReceivingYardsAfterCatch(Integer receivingYardsAfterCatch) {
        this.receivingYardsAfterCatch = receivingYardsAfterCatch;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Integer getFirstDownsTotal() {
        return firstDownsTotal;
    }

    public void setFirstDownsTotal(Integer firstDownsTotal) {
        this.firstDownsTotal = firstDownsTotal;
    }

    public Integer getFirstDownsRushing() {
        return firstDownsRushing;
    }

    public void setFirstDownsRushing(Integer firstDownsRushing) {
        this.firstDownsRushing = firstDownsRushing;
    }

    public Integer getFirstDownsPassing() {
        return firstDownsPassing;
    }

    public void setFirstDownsPassing(Integer firstDownsPassing) {
        this.firstDownsPassing = firstDownsPassing;
    }

    public Integer getFirstDownsReceiving() {
        return firstDownsReceiving;
    }

    public void setFirstDownsReceiving(Integer firstDownsReceiving) {
        this.firstDownsReceiving = firstDownsReceiving;
    }

    public Integer getFirstDownsPenalty() {
        return firstDownsPenalty;
    }

    public void setFirstDownsPenalty(Integer firstDownsPenalty) {
        this.firstDownsPenalty = firstDownsPenalty;
    }

    public Integer getRushingAttempts() {
        return rushingAttempts;
    }

    public void setRushingAttempts(Integer rushingAttempts) {
        this.rushingAttempts = rushingAttempts;
    }

    public Integer getRushingYards() {
        return rushingYards;
    }

    public void setRushingYards(Integer rushingYards) {
        this.rushingYards = rushingYards;
    }

    public float getRushingAverage() {
        return rushingAverage;
    }

    public void setRushingAverage(float rushingAverage) {
        this.rushingAverage = rushingAverage;
    }

    public Integer getRushingLong() {
        return rushingLong;
    }

    public void setRushingLong(Integer rushingLong) {
        this.rushingLong = rushingLong;
    }

    public boolean isRushingIsLongTouchdown() {
        return rushingIsLongTouchdown;
    }

    public void setRushingIsLongTouchdown(boolean rushingIsLongTouchdown) {
        this.rushingIsLongTouchdown = rushingIsLongTouchdown;
    }

    public Integer getRushingTouchdowns() {
        return rushingTouchdowns;
    }

    public void setRushingTouchdowns(Integer rushingTouchdowns) {
        this.rushingTouchdowns = rushingTouchdowns;
    }

    public Integer getRushingStuffed() {
        return rushingStuffed;
    }

    public void setRushingStuffed(Integer rushingStuffed) {
        this.rushingStuffed = rushingStuffed;
    }

    public Integer getRushingStuffedYardsLost() {
        return rushingStuffedYardsLost;
    }

    public void setRushingStuffedYardsLost(Integer rushingStuffedYardsLost) {
        this.rushingStuffedYardsLost = rushingStuffedYardsLost;
    }

    public float getRushingStuffedPercentage() {
        return rushingStuffedPercentage;
    }

    public void setRushingStuffedPercentage(float rushingStuffedPercentage) {
        this.rushingStuffedPercentage = rushingStuffedPercentage;
    }

    public Integer getPassingCompletions() {
        return passingCompletions;
    }

    public void setPassingCompletions(Integer passingCompletions) {
        this.passingCompletions = passingCompletions;
    }

    public Integer getPassingAttempts() {
        return passingAttempts;
    }

    public void setPassingAttempts(Integer passingAttempts) {
        this.passingAttempts = passingAttempts;
    }

    public float getPassingCompletionPercentage() {
        return passingCompletionPercentage;
    }

    public void setPassingCompletionPercentage(float passingCompletionPercentage) {
        this.passingCompletionPercentage = passingCompletionPercentage;
    }

    public Integer getPassingInterceptions() {
        return passingInterceptions;
    }

    public void setPassingInterceptions(Integer passingInterceptions) {
        this.passingInterceptions = passingInterceptions;
    }

    public Integer getPassingYards() {
        return passingYards;
    }

    public void setPassingYards(Integer passingYards) {
        this.passingYards = passingYards;
    }

    public float getPassingYardsPerAttempt() {
        return passingYardsPerAttempt;
    }

    public void setPassingYardsPerAttempt(float passingYardsPerAttempt) {
        this.passingYardsPerAttempt = passingYardsPerAttempt;
    }

    public Integer getPassingSacked() {
        return passingSacked;
    }

    public void setPassingSacked(Integer passingSacked) {
        this.passingSacked = passingSacked;
    }

    public Integer getPassingSackedYardsLost() {
        return passingSackedYardsLost;
    }

    public void setPassingSackedYardsLost(Integer passingSackedYardsLost) {
        this.passingSackedYardsLost = passingSackedYardsLost;
    }

    public Integer getPassingLong() {
        return passingLong;
    }

    public void setPassingLong(Integer passingLong) {
        this.passingLong = passingLong;
    }

    public boolean isPassingIsLongTouchdown() {
        return passingIsLongTouchdown;
    }

    public void setPassingIsLongTouchdown(boolean passingIsLongTouchdown) {
        this.passingIsLongTouchdown = passingIsLongTouchdown;
    }

    public Integer getPassingTouchdowns() {
        return passingTouchdowns;
    }

    public void setPassingTouchdowns(Integer passingTouchdowns) {
        this.passingTouchdowns = passingTouchdowns;
    }

    public float getPassingRating() {
        return passingRating;
    }

    public void setPassingRating(float passingRating) {
        this.passingRating = passingRating;
    }

    public Integer getPassingYardsAtCatch() {
        return passingYardsAtCatch;
    }

    public void setPassingYardsAtCatch(Integer passingYardsAtCatch) {
        this.passingYardsAtCatch = passingYardsAtCatch;
    }

    public float getPassingYardsAtCatchAverage() {
        return passingYardsAtCatchAverage;
    }

    public void setPassingYardsAtCatchAverage(float passingYardsAtCatchAverage) {
        this.passingYardsAtCatchAverage = passingYardsAtCatchAverage;
    }

    public Integer getPassingYardsAfterCatch() {
        return passingYardsAfterCatch;
    }

    public void setPassingYardsAfterCatch(Integer passingYardsAfterCatch) {
        this.passingYardsAfterCatch = passingYardsAfterCatch;
    }

    public float getPassingYardsAfterCatchAverage() {
        return passingYardsAfterCatchAverage;
    }

    public void setPassingYardsAfterCatchAverage(float passingYardsAfterCatchAverage) {
        this.passingYardsAfterCatchAverage = passingYardsAfterCatchAverage;
    }

    public Integer getFumblesTotal() {
        return fumblesTotal;
    }

    public void setFumblesTotal(Integer fumblesTotal) {
        this.fumblesTotal = fumblesTotal;
    }

    public Integer getFumblesPass() {
        return fumblesPass;
    }

    public void setFumblesPass(Integer fumblesPass) {
        this.fumblesPass = fumblesPass;
    }

    public Integer getFumblesRush() {
        return fumblesRush;
    }

    public void setFumblesRush(Integer fumblesRush) {
        this.fumblesRush = fumblesRush;
    }

    public Integer getFumblesSpecialTeams() {
        return fumblesSpecialTeams;
    }

    public void setFumblesSpecialTeams(Integer fumblesSpecialTeams) {
        this.fumblesSpecialTeams = fumblesSpecialTeams;
    }

    public Integer getFumblesReceiving() {
        return fumblesReceiving;
    }

    public void setFumblesReceiving(Integer fumblesReceiving) {
        this.fumblesReceiving = fumblesReceiving;
    }

    public Integer getFumblesDefense() {
        return fumblesDefense;
    }

    public void setFumblesDefense(Integer fumblesDefense) {
        this.fumblesDefense = fumblesDefense;
    }

    public Integer getFumblesMisc() {
        return fumblesMisc;
    }

    public void setFumblesMisc(Integer fumblesMisc) {
        this.fumblesMisc = fumblesMisc;
    }

    public Integer getFumblesLostTotal() {
        return fumblesLostTotal;
    }

    public void setFumblesLostTotal(Integer fumblesLostTotal) {
        this.fumblesLostTotal = fumblesLostTotal;
    }

    public Integer getFumblesLostPass() {
        return fumblesLostPass;
    }

    public void setFumblesLostPass(Integer fumblesLostPass) {
        this.fumblesLostPass = fumblesLostPass;
    }

    public Integer getFumblesLostRush() {
        return fumblesLostRush;
    }

    public void setFumblesLostRush(Integer fumblesLostRush) {
        this.fumblesLostRush = fumblesLostRush;
    }

    public Integer getFumblesLostSpecialTeams() {
        return fumblesLostSpecialTeams;
    }

    public void setFumblesLostSpecialTeams(Integer fumblesLostSpecialTeams) {
        this.fumblesLostSpecialTeams = fumblesLostSpecialTeams;
    }

    public Integer getFumblesLostReceiving() {
        return fumblesLostReceiving;
    }

    public void setFumblesLostReceiving(Integer fumblesLostReceiving) {
        this.fumblesLostReceiving = fumblesLostReceiving;
    }

    public Integer getFumblesLostDefense() {
        return fumblesLostDefense;
    }

    public void setFumblesLostDefense(Integer fumblesLostDefense) {
        this.fumblesLostDefense = fumblesLostDefense;
    }

    public Integer getFumblesLostMisc() {
        return fumblesLostMisc;
    }

    public void setFumblesLostMisc(Integer fumblesLostMisc) {
        this.fumblesLostMisc = fumblesLostMisc;
    }

    public Integer getParticipationOffense() {
        return participationOffense;
    }

    public void setParticipationOffense(Integer participationOffense) {
        this.participationOffense = participationOffense;
    }

    public Integer getParticipationDefense() {
        return participationDefense;
    }

    public void setParticipationDefense(Integer participationDefense) {
        this.participationDefense = participationDefense;
    }

    public Integer getParticipationSpecialTeams() {
        return participationSpecialTeams;
    }

    public void setParticipationSpecialTeams(Integer participationSpecialTeams) {
        this.participationSpecialTeams = participationSpecialTeams;
    }
}
