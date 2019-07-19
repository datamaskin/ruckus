package stats.retriever.nfl;

import models.sports.SportEvent;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by dmaclean on 8/19/14.
 */
public interface INflBoxscoreRetriever {
    List<Map<Integer, BigDecimal>> reconcileBoxscores(SportEvent sportEvent);
}
