package utils.projectiongraph;

import dao.DaoFactory;
import models.contest.Lineup;
import models.contest.LineupSpot;
import models.sports.Athlete;
import models.sports.AthleteSportEventInfo;
import models.sports.Position;
import models.sports.SportEvent;
import models.stats.nfl.StatsNflProjection;
import models.stats.nfl.StatsNflProjectionDefense;
import models.stats.predictive.StatsProjection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Created by dmaclean on 8/26/14.
 */
public class NFLProjectionGraphHelper extends BaseProjectionGraphHelper {
    private BigDecimal lineupProjection;

    public NFLProjectionGraphHelper(Lineup lineup) {
        super(lineup);
    }

    @Override
    public BigDecimal calculateTotalLineupProjection(boolean writeToProjectionTable) {
        /*
         * Go through the lineup and find out who our defense is.
         */
        Athlete defense = null;
        SportEvent defenseSportEvent = null;
        for(LineupSpot lineupSpot: lineup.getLineupSpots()) {
            if(lineupSpot.getAthlete().getPositions().contains(Position.FB_DEFENSE)) {
                defense = lineupSpot.getAthlete();
                defenseSportEvent = lineupSpot.getAthleteSportEventInfo().getSportEvent();
                break;
            }
        }

        /*
         * Pull the projections for each athlete and the defense, then sum all the projections.  Also, if the
         * writeToProjectionTable flag is true, we need to save projections off to stats_projection.
         */
        List<StatsNflProjection> athleteProjections = statsDao.getProjectionsForLineup(lineup);
        StatsNflProjectionDefense statsNflProjectionDefense = statsDao.findNflPredictionDefense(defenseSportEvent, defense);

        BigDecimal total = BigDecimal.ZERO;
        for(StatsNflProjection statsNflProjection: athleteProjections) {
            total = total.add(new BigDecimal(statsNflProjection.getProjectedFppMod()));
            if(writeToProjectionTable) {
                saveStatsProjection(statsNflProjection.getAthlete(), statsNflProjection.getSportEvent(), statsNflProjection.getProjectedFpp(), statsNflProjection.getProjectedFppMod());
            }
        }

        total = total.add(new BigDecimal(statsNflProjectionDefense.getProjectedFppMod())).setScale(2, RoundingMode.HALF_EVEN);
        if(writeToProjectionTable) {
            saveStatsProjection(statsNflProjectionDefense.getAthlete(), statsNflProjectionDefense.getSportEvent(), statsNflProjectionDefense.getProjectedFpp(), statsNflProjectionDefense.getProjectedFppMod());
        }

        return total;
    }

    @Override
    public void updatePerformanceData(List<BigDecimal> currentData, List<BigDecimal> projectedData, BigDecimal fantasyPointUpdate, int unitOfTime, int graphCapacity) {
        /*
         * A football game has 60 minutes, so we'll have each slot represent 3 minutes.
         *
         * Minutes 0-2 will be slot 0, 3-5 will be slot 1, etc.
         */
        int slot = unitOfTime/3;

        /*
         * This may be an update that comes in after several minutes have lapsed with no update.  Therefore, we
         * want to ensure that all buckets between a previous update (if any) and this one are filled properly.
         */
        if (currentData.isEmpty()) {
            for(int i=0; i<=slot; i++) {
                currentData.add(BigDecimal.ZERO);
            }
        }

        while (currentData.size() <= slot) {
            currentData.add(currentData.get(currentData.size() - 1));
        }

        /*
         * For updates that come from a later game earlier in the game, update previous buckets and propagate
         * those changes into later buckets.
         *
         * Example: Game 1 gets a lineup 5 points in 1st minute and 1 in 8th, so we get:
         *      5, 5, 6, [projections....]
         *
         * Game 2 later on gives lineup 1 point in 3rd minute, so we get:
         *      5, 6, 7, [projections....]
         */
        for (int i = slot; i < currentData.size(); i++) {
            currentData.set(i, currentData.get(i).add(fantasyPointUpdate));
        }

        projectedData.clear();
        /*
         * Adjust projected data.
         */
        if(lineupProjection == null) {
            lineupProjection = calculateTotalLineupProjection(true);
        }

        BigDecimal total = currentData.get(currentData.size() - 1);
        BigDecimal progression = lineupProjection.divide(new BigDecimal(graphCapacity), 2, RoundingMode.HALF_EVEN);
        for (int i = currentData.size(); i < graphCapacity; i++) {
            projectedData.add(total);
            total = total.add(progression);
        }
    }

    private void saveStatsProjection(Athlete athlete, SportEvent sportEvent, Float projectedFpp, Float projectedFppMod) {
        AthleteSportEventInfo athleteSportEventInfo = DaoFactory.getSportsDao().findAthleteSportEventInfo(athlete, sportEvent);
        StatsProjection statsProjection = statsDao.findStatsProjection(athleteSportEventInfo);

        if(statsProjection == null) {
            statsProjection = new StatsProjection(athleteSportEventInfo, projectedFpp, projectedFppMod);
        }
        statsDao.saveStatsProjection(statsProjection);
    }
}
