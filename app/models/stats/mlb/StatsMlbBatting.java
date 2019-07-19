package models.stats.mlb;

import models.sports.SportEvent;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Created by mwalsh on 7/5/14.
 */
@Entity
public class StatsMlbBatting {
    @Id
    private int id;
    @Version
    private Timestamp lastUpdate;
    private int statProviderId;
    private SportEvent sportEvent;
    private Integer eventId;
    private Integer atBats;
    private Integer flyballs;
    private Integer groundballs;
    private Integer lineDrives;
    private Integer groundIntoDoublePlaysOpportunities;
    private Integer groundIntoDoublePlaysTotal;
    private Integer hitByPitch;
    private Integer hitsDoubles;
    private Integer hitsExtraBaseHits;
    private Integer hitsHomeRuns;
    private Integer hitsSingles;
    private Integer hitsTotal;
    private Integer hitsTriples;
    private Float onBasePercentage;
    private Float sluggingPercentage;
    private Float onBasePlusSluggingPercentage;
    private Integer pitchesSeenRatePerPlateAppearance;
    private Integer pitchesSeenTotal;
    private Integer plateAppearances;
    private Integer runsBattedInGameWinning;
    private Integer runsBattedInTotal;
    private Integer runsScored;
    private Integer sacrificesHits;
    private Integer sacrificesFlies;
    private Integer stolenBasesAttempts;
    private Integer stolenBasesCaughtStealing;
    private Integer stolenBasesTotal;
    private Integer strikeOuts;
    private Integer timesOnBase;
    private Integer totalBases;
    private Integer walksIntentional;
    private Integer walksTotal;
    @Column(columnDefinition = "Decimal(10,2)")
    private BigDecimal fpp;
    private String averageFpp;
    private Integer opposition;

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Integer getOpposition() {
        return opposition;
    }

    public void setOpposition(Integer opposition) {
        this.opposition = opposition;
    }

    public String getAverageFpp() {
        return averageFpp;
    }

    public void setAverageFpp(String averageFpp) {
        this.averageFpp = averageFpp;
    }

    public BigDecimal getFpp() {
        return fpp;
    }

    public void setFpp(BigDecimal fpp) {
        this.fpp = fpp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatProviderId() {
        return statProviderId;
    }

    public void setStatProviderId(int statProviderId) {
        this.statProviderId = statProviderId;
    }

    public SportEvent getSportEvent() {
        return sportEvent;
    }

    public void setSportEvent(SportEvent sportEvent) {
        this.sportEvent = sportEvent;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Integer getAtBats() {
        return atBats;
    }

    public void setAtBats(Integer atBats) {
        this.atBats = atBats;
    }

    public Integer getFlyballs() {
        return flyballs;
    }

    public void setFlyballs(Integer flyballs) {
        this.flyballs = flyballs;
    }

    public Integer getGroundballs() {
        return groundballs;
    }

    public void setGroundballs(Integer groundballs) {
        this.groundballs = groundballs;
    }

    public Integer getLineDrives() {
        return lineDrives;
    }

    public void setLineDrives(Integer lineDrives) {
        this.lineDrives = lineDrives;
    }

    public Integer getGroundIntoDoublePlaysOpportunities() {
        return groundIntoDoublePlaysOpportunities;
    }

    public void setGroundIntoDoublePlaysOpportunities(Integer groundIntoDoublePlaysOpportunities) {
        this.groundIntoDoublePlaysOpportunities = groundIntoDoublePlaysOpportunities;
    }

    public Integer getGroundIntoDoublePlaysTotal() {
        return groundIntoDoublePlaysTotal;
    }

    public void setGroundIntoDoublePlaysTotal(Integer groundIntoDoublePlaysTotal) {
        this.groundIntoDoublePlaysTotal = groundIntoDoublePlaysTotal;
    }

    public Integer getHitByPitch() {
        return hitByPitch;
    }

    public void setHitByPitch(Integer hitByPitch) {
        this.hitByPitch = hitByPitch;
    }

    public Integer getHitsDoubles() {
        return hitsDoubles;
    }

    public void setHitsDoubles(Integer hitsDoubles) {
        this.hitsDoubles = hitsDoubles;
    }

    public Integer getHitsExtraBaseHits() {
        return hitsExtraBaseHits;
    }

    public void setHitsExtraBaseHits(Integer hitsExtraBaseHits) {
        this.hitsExtraBaseHits = hitsExtraBaseHits;
    }

    public Integer getHitsHomeRuns() {
        return hitsHomeRuns;
    }

    public void setHitsHomeRuns(Integer hitsHomeRuns) {
        this.hitsHomeRuns = hitsHomeRuns;
    }

    public Integer getHitsSingles() {
        return hitsSingles;
    }

    public void setHitsSingles(Integer hitsSingles) {
        this.hitsSingles = hitsSingles;
    }

    public Integer getHitsTotal() {
        return hitsTotal;
    }

    public void setHitsTotal(Integer hitsTotal) {
        this.hitsTotal = hitsTotal;
    }

    public Integer getHitsTriples() {
        return hitsTriples;
    }

    public void setHitsTriples(Integer hitsTriples) {
        this.hitsTriples = hitsTriples;
    }

    public Float getOnBasePercentage() {
        return onBasePercentage;
    }

    public void setOnBasePercentage(Float onBasePercentage) {
        this.onBasePercentage = onBasePercentage;
    }

    public Float getSluggingPercentage() {
        return sluggingPercentage;
    }

    public void setSluggingPercentage(Float sluggingPercentage) {
        this.sluggingPercentage = sluggingPercentage;
    }

    public Float getOnBasePlusSluggingPercentage() {
        return onBasePlusSluggingPercentage;
    }

    public void setOnBasePlusSluggingPercentage(Float onBasePlusSluggingPercentage) {
        this.onBasePlusSluggingPercentage = onBasePlusSluggingPercentage;
    }

    public Integer getPitchesSeenRatePerPlateAppearance() {
        return pitchesSeenRatePerPlateAppearance;
    }

    public void setPitchesSeenRatePerPlateAppearance(Integer pitchesSeenRatePerPlateAppearance) {
        this.pitchesSeenRatePerPlateAppearance = pitchesSeenRatePerPlateAppearance;
    }

    public Integer getPitchesSeenTotal() {
        return pitchesSeenTotal;
    }

    public void setPitchesSeenTotal(Integer pitchesSeenTotal) {
        this.pitchesSeenTotal = pitchesSeenTotal;
    }

    public Integer getPlateAppearances() {
        return plateAppearances;
    }

    public void setPlateAppearances(Integer plateAppearances) {
        this.plateAppearances = plateAppearances;
    }

    public Integer getRunsBattedInGameWinning() {
        return runsBattedInGameWinning;
    }

    public void setRunsBattedInGameWinning(Integer runsBattedInGameWinning) {
        this.runsBattedInGameWinning = runsBattedInGameWinning;
    }

    public Integer getRunsBattedInTotal() {
        return runsBattedInTotal;
    }

    public void setRunsBattedInTotal(Integer runsBattedInTotal) {
        this.runsBattedInTotal = runsBattedInTotal;
    }

    public Integer getRunsScored() {
        return runsScored;
    }

    public void setRunsScored(Integer runsScored) {
        this.runsScored = runsScored;
    }

    public Integer getSacrificesHits() {
        return sacrificesHits;
    }

    public void setSacrificesHits(Integer sacrificesHits) {
        this.sacrificesHits = sacrificesHits;
    }

    public Integer getSacrificesFlies() {
        return sacrificesFlies;
    }

    public void setSacrificesFlies(Integer sacrificesFlies) {
        this.sacrificesFlies = sacrificesFlies;
    }

    public Integer getStolenBasesAttempts() {
        return stolenBasesAttempts;
    }

    public void setStolenBasesAttempts(Integer stolenBasesAttempts) {
        this.stolenBasesAttempts = stolenBasesAttempts;
    }

    public Integer getStolenBasesCaughtStealing() {
        return stolenBasesCaughtStealing;
    }

    public void setStolenBasesCaughtStealing(Integer stolenBasesCaughtStealing) {
        this.stolenBasesCaughtStealing = stolenBasesCaughtStealing;
    }

    public Integer getStolenBasesTotal() {
        return stolenBasesTotal;
    }

    public void setStolenBasesTotal(Integer stolenBasesTotal) {
        this.stolenBasesTotal = stolenBasesTotal;
    }

    public Integer getStrikeOuts() {
        return strikeOuts;
    }

    public void setStrikeOuts(Integer strikeOuts) {
        this.strikeOuts = strikeOuts;
    }

    public Integer getTimesOnBase() {
        return timesOnBase;
    }

    public void setTimesOnBase(Integer timesOnBase) {
        this.timesOnBase = timesOnBase;
    }

    public Integer getTotalBases() {
        return totalBases;
    }

    public void setTotalBases(Integer totalBases) {
        this.totalBases = totalBases;
    }

    public Integer getWalksIntentional() {
        return walksIntentional;
    }

    public void setWalksIntentional(Integer walksIntentional) {
        this.walksIntentional = walksIntentional;
    }

    public Integer getWalksTotal() {
        return walksTotal;
    }

    public void setWalksTotal(Integer walksTotal) {
        this.walksTotal = walksTotal;
    }

}
