package stats.retriever;

import common.GlobalConstants;
import models.sports.League;
import models.sports.SportEvent;
import stats.parser.SportEventParser;
import stats.provider.StatProviderFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mwalsh on 6/5/14.
 */
public class SportEventRetriever implements ISportEventRetriever {
    private static final int PRE_SEASON_ID = 0;
    private static final int REG_SEASON_ID = 1;
    private static final int POST_SEASON_ID = 2;
    private static final int PRO_BOWL_ID = 3;

    @Override
    public List<SportEvent> getSportEventsForDate(League league, LocalDate now) {
        try {
            List<SportEvent> events = new ArrayList<>();

            Map<String, String> params = new HashMap<>();
            params.put(GlobalConstants.STATS_INC_KEY_RESOURCE, "events");

            // A couple things:
            // 1) retrieving an entire NFL season is ok, so include the "season" parameter
            // 2) retrieving an entire MLB season times out, so just get today, which is the default without any parameters
            // 3) we want regular and post season events for NFL
            if (league.getAbbreviation().equalsIgnoreCase("NFL")) {
                int[] eventTypes = {PRE_SEASON_ID, REG_SEASON_ID, POST_SEASON_ID};
                for (int eventType : eventTypes) {
                    params.put("season", now.format(DateTimeFormatter.ofPattern("yyyy")));
                    params.put("eventTypeId", String.valueOf(eventType));
                    String result = StatProviderFactory.getStatsProvider(league.getAbbreviation()).getStats(params);
                    events.addAll(new SportEventParser().parse(league, result));
                }
            } else if (league.getAbbreviation().equalsIgnoreCase("MLB")) {
                params.put("date", now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                String result = StatProviderFactory.getStatsProvider(league.getAbbreviation()).getStats(params);
                events.addAll(new SportEventParser().parse(league, result));
            }

            return events;
        } catch (Exception e) {
            play.Logger.info("Problem retrieving sport events for league " + league.getAbbreviation(), e);
            return null;
        }
    }

}
