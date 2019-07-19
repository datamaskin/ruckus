package stats.retriever;

import models.sports.League;
import models.sports.SportEvent;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by mwalsh on 6/5/14.
 */
public interface ISportEventRetriever {
    List<SportEvent> getSportEventsForDate(League nfl, LocalDate now);
}
