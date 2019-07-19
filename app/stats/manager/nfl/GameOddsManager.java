package stats.manager.nfl;

import common.GlobalConstants;
import play.Logger;
import stats.parser.GameOddsParser;
import stats.provider.nfl.StatsIncProviderNFL;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mgiles on 7/26/14.
 */
public class GameOddsManager {
    public void process() throws Exception {
        GameOddsParser parser = new GameOddsParser();

        int year = LocalDate.now().getYear();
        for (int i = GlobalConstants.YEARS_BACK; i >= 0; i--) {
            // 22 regular and post season NFL weeks
            for (int week = 1; week < 23; week++) {
                Map<String, String> map = new HashMap<>();
                map.put(GlobalConstants.STATS_INC_KEY_RESOURCE, "odds");
                map.put("season", String.valueOf(year - i));
                map.put("week", String.valueOf(week));

                try {
                    String results = new StatsIncProviderNFL().getStats(map);
                    parser.parse(results);

                    Logger.info("Processed NFL ODDS for season"
                            + String.valueOf(year - i)
                            + ", week "
                            + String.valueOf(week));
                } catch (Exception e) {
                    if (e.getMessage().contains("Unexpected response status: 404")) {
                        Logger.info("No data available for NFL ODDS for season"
                                + String.valueOf(year - i)
                                + ", week "
                                + String.valueOf(week));
                    } else {
                        Logger.error("Unable to process NFL ODDS for season"
                                + String.valueOf(year - i)
                                + ", week "
                                + String.valueOf(week), e.getMessage());
                    }
                }
            }
        }
    }
}
