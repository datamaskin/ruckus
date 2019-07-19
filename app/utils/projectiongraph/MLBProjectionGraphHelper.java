package utils.projectiongraph;

import models.contest.Lineup;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Projection Graph helper for MLB.
 */
public class MLBProjectionGraphHelper extends BaseProjectionGraphHelper {

    public MLBProjectionGraphHelper(Lineup lineup) {
        super(lineup);
    }

    @Override
    public BigDecimal calculateTotalLineupProjection(boolean writeToProjectionTable) {
        return null;
    }

    @Override
    public void updatePerformanceData(List<BigDecimal> currentData, List<BigDecimal> projectedData, BigDecimal fantasyPointUpdate, int unitOfTime, int graphCapacity) {
        int slot = unitOfTime * 2 - 2;

            /*
             * This may be an update that comes in after several innings have lapsed with no update.  Therefore, we
             * want to ensure that all buckets between a previous update (if any) and this one are filled properly.
             */
        while (currentData.size() < slot + 1) {
            if (currentData.isEmpty()) {
                currentData.add(BigDecimal.ZERO);
                currentData.add(BigDecimal.ZERO);
                continue;
            }
            currentData.add(currentData.get(currentData.size() - 1));
            currentData.add(currentData.get(currentData.size() - 1));
        }

            /*
             * For updates that come from a later game earlier in the game, update previous buckets and propagate
             * those changes into later buckets.
             *
             * Example: Game 1 gets a lineup 5 points in 1st inning and 1 in 5th, so we get:
             *      5, 5, 5, 5, 6, [projections....]
             *
             * Game 2 later on gives lineup 1 point in 2nd inning, so we get:
             *      5, 6, 6, 6, 7, [projections....]
             */
        for (int i = slot; i < currentData.size(); i += 2) {
            currentData.set(i, currentData.get(i).add(fantasyPointUpdate));
            currentData.set(i + 1, currentData.get(i + 1).add(fantasyPointUpdate));
        }

        projectedData.clear();
        if (currentData.size() == graphCapacity - 2) {
            currentData.add(currentData.get(currentData.size() - 1));
            currentData.add(currentData.get(currentData.size() - 1));
        } else {
                /*
                 * Adjust projected data.
                 */
            BigDecimal total = currentData.get(currentData.size() - 1);
            BigDecimal unitsRemaining = new BigDecimal(9 - (currentData.size() / 2));
            BigDecimal progression = (new BigDecimal(10).subtract(total)).divide(unitsRemaining, 2, RoundingMode.HALF_EVEN);
            for (int i = currentData.size(); i < graphCapacity; i += 2) {
                projectedData.add(total);
                projectedData.add(total);
                total = total.add(progression);
            }
        }
    }
}
