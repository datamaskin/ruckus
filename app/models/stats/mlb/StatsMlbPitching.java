package models.stats.mlb;

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
public class StatsMlbPitching {
    @Id
    private int id;
    @Version
    private Timestamp lastUpdate;
    private int statProviderId;
    private Integer eventId;
    private Integer balks;
    private Integer ballsHitAllowedFlyballs;
    private Integer ballsHitAllowedGroundBalls;
    private Integer ballsHitAllowedLineDrives;
    private Double baseRunnersAllowedRatePerNineInnings;
    private Integer baseRunnersAllowedTotal;
    private Double earnedRunAverage;
    private Integer gamesComplete;
    private Integer gamesQualityStarts;
    private Integer gamesShutouts;
    private Integer gamesStarts;
    private Integer gamesTotal;
    private Double groundIntoDoublePlaysPercentage;
    private Double groundIntoDoublePlaysRatePerNineInnings;
    private Integer groundIntoDoublePlaysTotal;
    private Integer hitBatsmen;
    private Integer hitsAllowedDoubles;
    private Integer hitsAllowedHomerunsTotal;
    private Double hitsAllowedHomerunsRatePerNineInnings;
    private Double hitsAllowedRatePerNineInnings;
    private Integer hitsAllowedTotal;
    private Integer hitsAllowedTriples;
    private Integer wildPitches;
    private Integer totalBattersFaced;
    private Integer walksIntentional;
    private Integer walksTotal;
    private Double walksPlusHitsRatePerInning;
    private Double walksPlusHitsTotal;
    private Double strikeoutWalkRatio;
    private Integer totalBasesAgainst;
    private Double strikeoutsRatePerNineInnings;
    private Integer strikeoutsTotal;
    private Integer stolenBasesAgainstAttempts;
    private Integer stolenBasesAgainstCaughtStealing;
    private Integer stolenBasesAgainstTotal;
    private Integer savesBlown;
    private Integer savesOpportunities;
    private Integer savesTotal;
    private Integer sacrificesFlies;
    private Integer sacrificesHits;
    private Double runSupportRatePerNineInnings;
    private Integer runSupportTotal;
    private Integer runsAllowedEarnedRuns;
    private Integer runsAllowedRunsBattedIn;
    private Integer runsAllowedTotal;
    private Double pitchesPerInning;
    private Integer pitchesTotal;
    private Integer holds;
    private Integer inheritedRunnersStranded;
    private Integer inheritedRunnersTotal;
    private Double inningsPitched;
    private Integer opponentAtBats;
    private Double opponentBattingAverage;
    private Double opponentOnBasePercentage;
    private Double opponentSluggingPercentage;
    private Integer pickoffsPlusPitcherCaughtStealing;
    private Integer pickoffsThrows;
    private Double pickoffsThrowsPerBaseRunner;
    private Integer pickoffsTotal;
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

    public BigDecimal getFpp() {
        return fpp;
    }

    public void setFpp(BigDecimal fpp) {
        this.fpp = fpp;
    }

    public String getAverageFpp() {
        return averageFpp;
    }

    public void setAverageFpp(String averageFpp) {
        this.averageFpp = averageFpp;
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

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Integer getBalks() {
        return balks;
    }

    public void setBalks(Integer balks) {
        this.balks = balks;
    }

    public Integer getBallsHitAllowedFlyballs() {
        return ballsHitAllowedFlyballs;
    }

    public void setBallsHitAllowedFlyballs(Integer ballsHitAllowedFlyballs) {
        this.ballsHitAllowedFlyballs = ballsHitAllowedFlyballs;
    }

    public Integer getBallsHitAllowedGroundBalls() {
        return ballsHitAllowedGroundBalls;
    }

    public void setBallsHitAllowedGroundBalls(Integer ballsHitAllowedGroundBalls) {
        this.ballsHitAllowedGroundBalls = ballsHitAllowedGroundBalls;
    }

    public Integer getBallsHitAllowedLineDrives() {
        return ballsHitAllowedLineDrives;
    }

    public void setBallsHitAllowedLineDrives(Integer ballsHitAllowedLineDrives) {
        this.ballsHitAllowedLineDrives = ballsHitAllowedLineDrives;
    }

    public Double getBaseRunnersAllowedRatePerNineInnings() {
        return baseRunnersAllowedRatePerNineInnings;
    }

    public void setBaseRunnersAllowedRatePerNineInnings(Double baseRunnersAllowedRatePerNineInnings) {
        this.baseRunnersAllowedRatePerNineInnings = baseRunnersAllowedRatePerNineInnings;
    }

    public Integer getBaseRunnersAllowedTotal() {
        return baseRunnersAllowedTotal;
    }

    public void setBaseRunnersAllowedTotal(Integer baseRunnersAllowedTotal) {
        this.baseRunnersAllowedTotal = baseRunnersAllowedTotal;
    }

    public Double getEarnedRunAverage() {
        return earnedRunAverage;
    }

    public void setEarnedRunAverage(Double earnedRunAverage) {
        this.earnedRunAverage = earnedRunAverage;
    }

    public Integer getGamesComplete() {
        return gamesComplete;
    }

    public void setGamesComplete(Integer gamesComplete) {
        this.gamesComplete = gamesComplete;
    }

    public Integer getGamesQualityStarts() {
        return gamesQualityStarts;
    }

    public void setGamesQualityStarts(Integer gamesQualityStarts) {
        this.gamesQualityStarts = gamesQualityStarts;
    }

    public Integer getGamesShutouts() {
        return gamesShutouts;
    }

    public void setGamesShutouts(Integer gamesShutouts) {
        this.gamesShutouts = gamesShutouts;
    }

    public Integer getGamesStarts() {
        return gamesStarts;
    }

    public void setGamesStarts(Integer gamesStarts) {
        this.gamesStarts = gamesStarts;
    }

    public Integer getGamesTotal() {
        return gamesTotal;
    }

    public void setGamesTotal(Integer gamesTotal) {
        this.gamesTotal = gamesTotal;
    }

    public Double getGroundIntoDoublePlaysPercentage() {
        return groundIntoDoublePlaysPercentage;
    }

    public void setGroundIntoDoublePlaysPercentage(Double groundIntoDoublePlaysPercentage) {
        this.groundIntoDoublePlaysPercentage = groundIntoDoublePlaysPercentage;
    }

    public Double getGroundIntoDoublePlaysRatePerNineInnings() {
        return groundIntoDoublePlaysRatePerNineInnings;
    }

    public void setGroundIntoDoublePlaysRatePerNineInnings(Double groundIntoDoublePlaysRatePerNineInnings) {
        this.groundIntoDoublePlaysRatePerNineInnings = groundIntoDoublePlaysRatePerNineInnings;
    }

    public Integer getGroundIntoDoublePlaysTotal() {
        return groundIntoDoublePlaysTotal;
    }

    public void setGroundIntoDoublePlaysTotal(Integer groundIntoDoublePlaysTotal) {
        this.groundIntoDoublePlaysTotal = groundIntoDoublePlaysTotal;
    }

    public Integer getHitBatsmen() {
        return hitBatsmen;
    }

    public void setHitBatsmen(Integer hitBatsmen) {
        this.hitBatsmen = hitBatsmen;
    }

    public Integer getHitsAllowedDoubles() {
        return hitsAllowedDoubles;
    }

    public void setHitsAllowedDoubles(Integer hitsAllowedDoubles) {
        this.hitsAllowedDoubles = hitsAllowedDoubles;
    }

    public Integer getHitsAllowedHomerunsTotal() {
        return hitsAllowedHomerunsTotal;
    }

    public void setHitsAllowedHomerunsTotal(Integer hitsAllowedHomerunsTotal) {
        this.hitsAllowedHomerunsTotal = hitsAllowedHomerunsTotal;
    }

    public Double getHitsAllowedHomerunsRatePerNineInnings() {
        return hitsAllowedHomerunsRatePerNineInnings;
    }

    public void setHitsAllowedHomerunsRatePerNineInnings(Double hitsAllowedHomerunsRatePerNineInnings) {
        this.hitsAllowedHomerunsRatePerNineInnings = hitsAllowedHomerunsRatePerNineInnings;
    }

    public Double getHitsAllowedRatePerNineInnings() {
        return hitsAllowedRatePerNineInnings;
    }

    public void setHitsAllowedRatePerNineInnings(Double hitsAllowedRatePerNineInnings) {
        this.hitsAllowedRatePerNineInnings = hitsAllowedRatePerNineInnings;
    }

    public Integer getHitsAllowedTotal() {
        return hitsAllowedTotal;
    }

    public void setHitsAllowedTotal(Integer hitsAllowedTotal) {
        this.hitsAllowedTotal = hitsAllowedTotal;
    }

    public Integer getHitsAllowedTriples() {
        return hitsAllowedTriples;
    }

    public void setHitsAllowedTriples(Integer hitsAllowedTriples) {
        this.hitsAllowedTriples = hitsAllowedTriples;
    }

    public Integer getWildPitches() {
        return wildPitches;
    }

    public void setWildPitches(Integer wildPitches) {
        this.wildPitches = wildPitches;
    }

    public Integer getTotalBattersFaced() {
        return totalBattersFaced;
    }

    public void setTotalBattersFaced(Integer totalBattersFaced) {
        this.totalBattersFaced = totalBattersFaced;
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

    public Double getWalksPlusHitsRatePerInning() {
        return walksPlusHitsRatePerInning;
    }

    public void setWalksPlusHitsRatePerInning(Double walksPlusHitsRatePerInning) {
        this.walksPlusHitsRatePerInning = walksPlusHitsRatePerInning;
    }

    public Double getWalksPlusHitsTotal() {
        return walksPlusHitsTotal;
    }

    public void setWalksPlusHitsTotal(Double walksPlusHitsTotal) {
        this.walksPlusHitsTotal = walksPlusHitsTotal;
    }

    public Double getStrikeoutWalkRatio() {
        return strikeoutWalkRatio;
    }

    public void setStrikeoutWalkRatio(Double strikeoutWalkRatio) {
        this.strikeoutWalkRatio = strikeoutWalkRatio;
    }

    public Integer getTotalBasesAgainst() {
        return totalBasesAgainst;
    }

    public void setTotalBasesAgainst(Integer totalBasesAgainst) {
        this.totalBasesAgainst = totalBasesAgainst;
    }

    public Double getStrikeoutsRatePerNineInnings() {
        return strikeoutsRatePerNineInnings;
    }

    public void setStrikeoutsRatePerNineInnings(Double strikeoutsRatePerNineInnings) {
        this.strikeoutsRatePerNineInnings = strikeoutsRatePerNineInnings;
    }

    public Integer getStrikeoutsTotal() {
        return strikeoutsTotal;
    }

    public void setStrikeoutsTotal(Integer strikeoutsTotal) {
        this.strikeoutsTotal = strikeoutsTotal;
    }

    public Integer getStolenBasesAgainstAttempts() {
        return stolenBasesAgainstAttempts;
    }

    public void setStolenBasesAgainstAttempts(Integer stolenBasesAgainstAttempts) {
        this.stolenBasesAgainstAttempts = stolenBasesAgainstAttempts;
    }

    public Integer getStolenBasesAgainstCaughtStealing() {
        return stolenBasesAgainstCaughtStealing;
    }

    public void setStolenBasesAgainstCaughtStealing(Integer stolenBasesAgainstCaughtStealing) {
        this.stolenBasesAgainstCaughtStealing = stolenBasesAgainstCaughtStealing;
    }

    public Integer getStolenBasesAgainstTotal() {
        return stolenBasesAgainstTotal;
    }

    public void setStolenBasesAgainstTotal(Integer stolenBasesAgainstTotal) {
        this.stolenBasesAgainstTotal = stolenBasesAgainstTotal;
    }

    public Integer getSavesBlown() {
        return savesBlown;
    }

    public void setSavesBlown(Integer savesBlown) {
        this.savesBlown = savesBlown;
    }

    public Integer getSavesOpportunities() {
        return savesOpportunities;
    }

    public void setSavesOpportunities(Integer savesOpportunities) {
        this.savesOpportunities = savesOpportunities;
    }

    public Integer getSavesTotal() {
        return savesTotal;
    }

    public void setSavesTotal(Integer savesTotal) {
        this.savesTotal = savesTotal;
    }

    public Integer getSacrificesFlies() {
        return sacrificesFlies;
    }

    public void setSacrificesFlies(Integer sacrificesFlies) {
        this.sacrificesFlies = sacrificesFlies;
    }

    public Integer getSacrificesHits() {
        return sacrificesHits;
    }

    public void setSacrificesHits(Integer sacrificesHits) {
        this.sacrificesHits = sacrificesHits;
    }

    public Double getRunSupportRatePerNineInnings() {
        return runSupportRatePerNineInnings;
    }

    public void setRunSupportRatePerNineInnings(Double runSupportRatePerNineInnings) {
        this.runSupportRatePerNineInnings = runSupportRatePerNineInnings;
    }

    public Integer getRunSupportTotal() {
        return runSupportTotal;
    }

    public void setRunSupportTotal(Integer runSupportTotal) {
        this.runSupportTotal = runSupportTotal;
    }

    public Integer getRunsAllowedEarnedRuns() {
        return runsAllowedEarnedRuns;
    }

    public void setRunsAllowedEarnedRuns(Integer runsAllowedEarnedRuns) {
        this.runsAllowedEarnedRuns = runsAllowedEarnedRuns;
    }

    public Integer getRunsAllowedRunsBattedIn() {
        return runsAllowedRunsBattedIn;
    }

    public void setRunsAllowedRunsBattedIn(Integer runsAllowedRunsBattedIn) {
        this.runsAllowedRunsBattedIn = runsAllowedRunsBattedIn;
    }

    public Integer getRunsAllowedTotal() {
        return runsAllowedTotal;
    }

    public void setRunsAllowedTotal(Integer runsAllowedTotal) {
        this.runsAllowedTotal = runsAllowedTotal;
    }

    public Double getPitchesPerInning() {
        return pitchesPerInning;
    }

    public void setPitchesPerInning(Double pitchesPerInning) {
        this.pitchesPerInning = pitchesPerInning;
    }

    public Integer getPitchesTotal() {
        return pitchesTotal;
    }

    public void setPitchesTotal(Integer pitchesTotal) {
        this.pitchesTotal = pitchesTotal;
    }

    public Integer getHolds() {
        return holds;
    }

    public void setHolds(Integer holds) {
        this.holds = holds;
    }

    public Integer getInheritedRunnersStranded() {
        return inheritedRunnersStranded;
    }

    public void setInheritedRunnersStranded(Integer inheritedRunnersStranded) {
        this.inheritedRunnersStranded = inheritedRunnersStranded;
    }

    public Integer getInheritedRunnersTotal() {
        return inheritedRunnersTotal;
    }

    public void setInheritedRunnersTotal(Integer inheritedRunnersTotal) {
        this.inheritedRunnersTotal = inheritedRunnersTotal;
    }

    public Double getInningsPitched() {
        return inningsPitched;
    }

    public void setInningsPitched(Double inningsPitched) {
        this.inningsPitched = inningsPitched;
    }

    public Integer getOpponentAtBats() {
        return opponentAtBats;
    }

    public void setOpponentAtBats(Integer opponentAtBats) {
        this.opponentAtBats = opponentAtBats;
    }

    public Double getOpponentBattingAverage() {
        return opponentBattingAverage;
    }

    public void setOpponentBattingAverage(Double opponentBattingAverage) {
        this.opponentBattingAverage = opponentBattingAverage;
    }

    public Double getOpponentOnBasePercentage() {
        return opponentOnBasePercentage;
    }

    public void setOpponentOnBasePercentage(Double opponentOnBasePercentage) {
        this.opponentOnBasePercentage = opponentOnBasePercentage;
    }

    public Double getOpponentSluggingPercentage() {
        return opponentSluggingPercentage;
    }

    public void setOpponentSluggingPercentage(Double opponentSluggingPercentage) {
        this.opponentSluggingPercentage = opponentSluggingPercentage;
    }

    public Integer getPickoffsPlusPitcherCaughtStealing() {
        return pickoffsPlusPitcherCaughtStealing;
    }

    public void setPickoffsPlusPitcherCaughtStealing(Integer pickoffsPlusPitcherCaughtStealing) {
        this.pickoffsPlusPitcherCaughtStealing = pickoffsPlusPitcherCaughtStealing;
    }

    public Integer getPickoffsThrows() {
        return pickoffsThrows;
    }

    public void setPickoffsThrows(Integer pickoffsThrows) {
        this.pickoffsThrows = pickoffsThrows;
    }

    public Double getPickoffsThrowsPerBaseRunner() {
        return pickoffsThrowsPerBaseRunner;
    }

    public void setPickoffsThrowsPerBaseRunner(Double pickoffsThrowsPerBaseRunner) {
        this.pickoffsThrowsPerBaseRunner = pickoffsThrowsPerBaseRunner;
    }

    public Integer getPickoffsTotal() {
        return pickoffsTotal;
    }

    public void setPickoffsTotal(Integer pickoffsTotal) {
        this.pickoffsTotal = pickoffsTotal;
    }
}
