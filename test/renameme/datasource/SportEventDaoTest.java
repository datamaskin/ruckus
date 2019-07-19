package renameme.datasource;

import com.avaje.ebean.Ebean;
import dao.SportsDao;
import models.sports.*;
import org.junit.Before;
import org.junit.Test;
import utilities.BaseTest;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by mwalsh on 7/9/14.
 */
public class SportEventDaoTest extends BaseTest {

    SportsDao dao;
    League league = League.NFL;
    ZoneId eastern = ZoneId.of("US/Eastern");
    ZonedDateTime date = ZonedDateTime.now(eastern).with(TemporalAdjusters.nextOrSame(DayOfWeek.TUESDAY));

    @Before
    public void setup() {
        dao = new SportsDao();

        ZonedDateTime wedNightStart = date.with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY)).withHour(8).truncatedTo(ChronoUnit.HOURS);
        ZonedDateTime thursdayNightStart = date.with(TemporalAdjusters.next(DayOfWeek.THURSDAY)).withHour(8).truncatedTo(ChronoUnit.HOURS);
        ZonedDateTime sundayNoonStart = date.with(TemporalAdjusters.next(DayOfWeek.SUNDAY)).withHour(1).truncatedTo(ChronoUnit.HOURS);
        ZonedDateTime sundayLateStart = date.with(TemporalAdjusters.next(DayOfWeek.SUNDAY)).withHour(4).truncatedTo(ChronoUnit.HOURS);
        ZonedDateTime mondayNightStart = date.with(TemporalAdjusters.next(DayOfWeek.MONDAY)).withHour(8).truncatedTo(ChronoUnit.HOURS);

        SportEvent event1Wed = new SportEvent(123, league,
                Date.from(wedNightStart.toInstant()),
                "Wed night", "Wed", 90, false, 2014, -1, 1);
        SportEvent event2Thu = new SportEvent(234, league,
                Date.from(thursdayNightStart.toInstant()),
                "Thursday night", "Thu", 90, false, 2014, -1, 1);
        SportEvent event3Sun = new SportEvent(345, league,
                Date.from(sundayNoonStart.toInstant()),
                "Sunday early", "Sun AM", 90, false, 2014, -1, 1);
        SportEvent event4Sun = new SportEvent(456, league,
                Date.from(sundayLateStart.toInstant()),
                "Sunday late", "Sun Late", 90, false, 2014, -1, 1);
        SportEvent event5Mon = new SportEvent(567, league,
                Date.from(mondayNightStart.toInstant()),
                "Monday night", "Mon Night", 90, false, 2014, -1, 1);

        Ebean.save(Arrays.asList(event1Wed, event2Thu, event3Sun, event4Sun, event5Mon));

    }

    @Test
    public void testRetrievalThursdayToSunday() {
        SportEventGroupingType type = new SportEventGroupingType(league, "NFL EARLY",
                Arrays.asList(new SportEventDateRangeSelector(DayOfWeek.THURSDAY, 0, 0, DayOfWeek.SUNDAY, 1, 0)));

        List<SportEvent> events = dao.findSportEvents(type, date);
        Collections.sort(events, (o1, o2) -> o1.getStatProviderId() - o2.getStatProviderId());

        assertEquals(2, events.size());
        assertEquals(234, events.get(0).getStatProviderId());
        assertEquals(345, events.get(1).getStatProviderId());
    }

    @Test
    public void testRetrievalAllSunday() {
        SportEventGroupingType type = new SportEventGroupingType(league, "NFL EARLY",
                Arrays.asList(new SportEventDateRangeSelector(DayOfWeek.SUNDAY, 0, 0, DayOfWeek.MONDAY, 0, 0)));

        List<SportEvent> events = dao.findSportEvents(type, date);
        Collections.sort(events, (o1, o2) -> o1.getStatProviderId() - o2.getStatProviderId());

        assertEquals(2, events.size());
        assertEquals(345, events.get(0).getStatProviderId());
        assertEquals(456, events.get(1).getStatProviderId());
    }

    @Test
    public void testRetrievalSundayMonday() {
        SportEventGroupingType type = new SportEventGroupingType(league, "NFL EARLY",
                Arrays.asList(new SportEventDateRangeSelector(DayOfWeek.SUNDAY, 0, 0, DayOfWeek.TUESDAY, 0, 0)));

        List<SportEvent> events = dao.findSportEvents(type, date);
        Collections.sort(events, (o1, o2) -> o1.getStatProviderId() - o2.getStatProviderId());

        assertEquals(3, events.size());
        assertEquals(345, events.get(0).getStatProviderId());
        assertEquals(456, events.get(1).getStatProviderId());
        assertEquals(567, events.get(2).getStatProviderId());
    }

    @Test
    public void testRetrievalWednesday() {
        SportEventGroupingType type = new SportEventGroupingType(league, "NFL EARLY",
                Arrays.asList(new SportEventDateRangeSelector(DayOfWeek.WEDNESDAY, 0, 0, DayOfWeek.THURSDAY, 0, 0)));

        List<SportEvent> events = dao.findSportEvents(type, date);
        Collections.sort(events, (o1, o2) -> o1.getStatProviderId() - o2.getStatProviderId());

        assertEquals(1, events.size());
        assertEquals(123, events.get(0).getStatProviderId());
    }
}
