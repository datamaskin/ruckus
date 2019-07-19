package distributed.tasks;

import models.sports.League;
import stats.manager.StatsEventManager;
import stats.provider.StatProviderFactory;
import utils.ITimeService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Distributed task that runs periodically to check on and update the completeness and units remaining of sport events.
 */
public class SportEventCompletedUpdaterTask extends DistributedTask {

    private ITimeService timeService;

    public SportEventCompletedUpdaterTask(ITimeService timeService) {
        this.timeService = timeService;
    }

    @Override
    protected String execute() throws Exception {
        StatsEventManager statsEventManager = new StatsEventManager();

        for(League league: League.ALL_LEAGUES) {
            statsEventManager.setStatProvider(StatProviderFactory.getStatsProvider(league.getAbbreviation()));

            Instant now = timeService.getNow();
            Date today = Date.from(now);
            Date yesterday = Date.from(now.minus(1, ChronoUnit.DAYS));

            statsEventManager.refreshSportEventCompletion(league, yesterday);
            statsEventManager.refreshSportEventCompletion(league, today);
        }

        return null;
    }
}
