package distributed.tasks;

import com.avaje.ebean.Ebean;
import dao.DaoFactory;
import models.contest.Contest;
import models.contest.ContestPayout;
import models.sports.*;
import org.junit.Before;
import org.junit.Test;
import stats.retriever.ISportEventRetriever;
import utilities.BaseTest;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by mwalsh on 7/10/14.
 */
public class ContestCreatorTaskTest extends BaseTest {

    private static final int NICKEL = 5;

    private ContestCreatorTask task;
    private ZoneId eastern = ZoneId.of("US/Eastern");
    private ZonedDateTime date = ZonedDateTime.now(eastern).with(TemporalAdjusters.next(DayOfWeek.SUNDAY));

    ISportEventRetriever retriever = (nfl, now) -> {
        ZonedDateTime mondayNightStart = date.with(TemporalAdjusters.next(DayOfWeek.MONDAY)).withHour(8).truncatedTo(ChronoUnit.HOURS);
        SportEvent event1 = new SportEvent(123, League.MLB, Date.from(mondayNightStart.toInstant()), "Monday night", "Mon", 90, false, 2014, -1, 1);
        return Arrays.asList(event1);
    };

    SportEventGroupingType nflStandard = new SportEventGroupingType(League.NFL, "NFL Standard",
            Arrays.asList(new SportEventDateRangeSelector(DayOfWeek.SUNDAY, 0, 0, DayOfWeek.MONDAY, 0, 0)));

    @Before
    public void setup() {
        new DaoFactory(context);
        Ebean.save(nflStandard);
        task = new ContestCreatorTask();
    }

    @Test
    public void testContestCreatorTask() {
        SportEventGrouping sportEventGrouping = new SportEventGrouping(
                retriever.getSportEventsForDate(League.NFL, LocalDate.now()),
                nflStandard);

        Ebean.save(sportEventGrouping);

        task.createContests(sportEventGrouping);

        List<Contest> createdContests = Ebean.find(Contest.class).fetch("contestPayouts").findList();

        for(Contest contest: createdContests){
            System.out.println(contest.toString());
            for(ContestPayout contestPayout: contest.getContestPayouts()){
                System.out.println("\t"+contestPayout.toString());
            }
        }

    }

}
