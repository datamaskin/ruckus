package stats.manager.nfl;

import common.GlobalConstants;
import dao.DaoFactory;
import models.stats.nfl.DynamoStatsNflDepthChartRaw;
import stats.parser.nfl.DepthChartParser;
import stats.provider.nfl.StatsIncProviderNFL;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mgiles on 8/20/14.
 */
public class DepthChartManager {

    private DepthChartParser parser = new DepthChartParser();

    public void process() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put(GlobalConstants.STATS_INC_KEY_RESOURCE, "depthCharts");
        String results = new StatsIncProviderNFL().getStats(map);
        Map<String, String> cache = parser.parse(results);
        DynamoStatsNflDepthChartRaw raw = new DynamoStatsNflDepthChartRaw();
        raw.setId(cache.get("id"));
        DaoFactory.getStatsDao().saveStatsNflDepthChartsRaw(raw, cache.get("results"));
    }
}
