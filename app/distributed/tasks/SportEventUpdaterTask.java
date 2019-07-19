package distributed.tasks;

import common.GlobalConstants;
import dao.DaoFactory;
import models.sports.League;
import models.sports.SportEvent;
import stats.retriever.ISportEventRetriever;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by mwalsh on 6/5/14.
 */
public class SportEventUpdaterTask extends DistributedTask {

    private ISportEventRetriever sportEventStatRetriever;

    public SportEventUpdaterTask(ISportEventRetriever sportEventStatRetriever) {
        this.sportEventStatRetriever = sportEventStatRetriever;
    }

    @Override
    protected String execute() throws Exception {
        // go N years back
        for (int i = GlobalConstants.YEARS_BACK; i >= 0; i--) {
            LocalDate season = LocalDate.now().minusYears(i);
            for (League league : DaoFactory.getSportsDao().findActiveLeagues()) {
                List<SportEvent> events = sportEventStatRetriever.getSportEventsForDate(league, season);
                if (events != null) {
                    for (SportEvent event : events) {
                        SportEvent dbEvent = DaoFactory.getSportsDao().findSportEvent(event.getStatProviderId());
                        if (dbEvent == null) {
                            dbEvent = new SportEvent(event);
                        } else {
                            dbEvent.update(event);
                        }
                        DaoFactory.getSportsDao().saveSportEvent(dbEvent);
                    }
                }
            }
        }
        return null;
    }

}
