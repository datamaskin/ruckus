package stats.manager;

import common.GlobalConstants;
import dao.DaoFactory;
import models.sports.League;
import models.sports.SportEvent;
import play.Logger;
import simulator.ContestSimulationManager;
import stats.parser.SportEventParser;
import stats.provider.IStatProvider;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dmaclean on 7/15/14.
 */
public class StatsEventManager {
    protected IStatProvider statProvider;

    /**
     * Fetch the latest state of the events for today and determine if any need to be updated
     * to a complete status.
     *
     * @param league The league whose games we are interested in.
     * @param date   The current date in yyyy-MM-dd format.
     */
    public void refreshSportEventCompletion(League league, Date date) {
        if (ContestSimulationManager.isSimulation()) {
            return;
        }

        SportEventParser parser = new SportEventParser();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Map<String, String> map = new HashMap<>();

        map.put(GlobalConstants.STATS_INC_KEY_RESOURCE, "events");
        map.put("date", simpleDateFormat.format(date));
        try {
            String results = statProvider.getStats(map);
            List<SportEvent> sportEvents = parser.parse(league, results);

            sportEvents.stream().filter(sportEvent -> sportEvent.isComplete()).forEach(sportEvent -> {
                SportEvent sportEventToUpdate = DaoFactory.getSportsDao().findSportEvent(sportEvent.getStatProviderId());
                sportEventToUpdate.setComplete(true);
                sportEventToUpdate.setUnitsRemaining(sportEvent.getUnitsRemaining());
                DaoFactory.getSportsDao().saveSportEvent(sportEventToUpdate);
            });

            Logger.info("Processed sport event completion update for " + league.getAbbreviation() + " for " + simpleDateFormat.format(date));
        } catch (Exception e) {
            Logger.error("Unable to process sport event completion update for " + league.getAbbreviation() + " for " + simpleDateFormat.format(date) + ": " + e.getMessage());
        }
    }

    public void setStatProvider(IStatProvider statProvider) {
        this.statProvider = statProvider;
    }
}
