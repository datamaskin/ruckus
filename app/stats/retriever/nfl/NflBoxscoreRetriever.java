package stats.retriever.nfl;

import common.GlobalConstants;
import models.sports.SportEvent;
import play.Logger;
import stats.parser.IStatsParser;
import stats.provider.IStatProvider;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dmaclean on 8/19/14.
 */
public class NflBoxscoreRetriever implements INflBoxscoreRetriever {
    private IStatsParser<Map<Integer, BigDecimal>> parser;
    private IStatProvider statProvider;

    public NflBoxscoreRetriever(IStatsParser<Map<Integer, BigDecimal>> parser, IStatProvider statProvider) {
        this.parser = parser;
        this.statProvider = statProvider;
    }

    @Override
    public List<Map<Integer, BigDecimal>> reconcileBoxscores(SportEvent sportEvent) {
        try {
            Map<String, String> map = new HashMap<>();
            map.put(GlobalConstants.STATS_INC_KEY_RESOURCE, "events/" + sportEvent.getStatProviderId());
            map.put("box", "true");

            String results = statProvider.getStats(map);
            return parser.parse(results);
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return new ArrayList<>();
        }
    }

    public void setParser(IStatsParser<Map<Integer, BigDecimal>> parser) {
        this.parser = parser;
    }

    public void setStatProvider(IStatProvider statProvider) {
        this.statProvider = statProvider;
    }
}
