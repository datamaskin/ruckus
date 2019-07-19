package stats.translator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Interface for classes that will perform translation of stat data to fantasy points.
 */
public interface IFantasyPointTranslator {
    /**
     * Perform calculation of fantasy points based on the map of stats provided.
     *
     * @param stats     A map of stats to translate into Victis fantasy points.
     * @return          A BigDecimal representing the number of fantasy points that the stats are worth.
     */
    BigDecimal calculateFantasyPoints(Map<String, BigDecimal> stats);

    List<BigDecimal> determineFantasyPointIncrementForEvent(Map<String, Integer> eventData);
}
