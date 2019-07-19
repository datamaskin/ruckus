package managers.stats;

import com.avaje.ebean.Ebean;
import dao.SportsDao;
import models.sports.League;
import models.sports.Sport;
import models.sports.SportEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stats.manager.StatsEventManager;
import stats.statsinc.FileStatProvider;
import utilities.BaseTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by dmaclean on 7/15/14.
 */
public class StatsMlbEventManagerTest extends BaseTest {
    private StatsEventManager statsEventManager;
    private FileStatProvider statProvider;

    private SportEvent sportEvent1;
    private SportEvent sportEvent2;
    private SportEvent sportEvent3;

    private Sport baseball;
    private League mlb;

    private SimpleDateFormat simpleDateFormat;

    @Before
    public void setUp() {
        statsEventManager = new StatsEventManager();
        statProvider = new FileStatProvider();

        statsEventManager.setStatProvider(statProvider);

        baseball = new Sport(Sport.BASEBALL.getName());
        Ebean.save(baseball);

        mlb = new League(baseball, League.MLB.getName(), League.MLB.getAbbreviation(), League.MLB.getDisplayName(), true);
        Ebean.save(mlb);

        try {
            simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
            sportEvent1 = new SportEvent(1379173, mlb, simpleDateFormat.parse("07/13/2014 17:05"), "desc", "shortDesc", 9, false, 2014, -1, 1);
            Ebean.save(sportEvent1);
            sportEvent2 = new SportEvent(1380029, mlb, simpleDateFormat.parse("07/13/2014 17:05"), "desc", "shortDesc", 9, false, 2014, -1, 1);
            Ebean.save(sportEvent2);
            sportEvent3 = new SportEvent(1234, mlb, simpleDateFormat.parse("07/13/2014 17:05"), "desc", "shortDesc", 9, false, 2014, -1, 1);
            Ebean.save(sportEvent3);
        }
        catch(Exception e) {
            fail(e.getMessage());
        }
    }

    @After
    public void tearDown() {
        statsEventManager = null;
        statProvider = null;
    }

    @Test
    public void testRefreshSportEventCompletion() {
        statProvider.setFilename("test_files/mlb_events_20140713.json");

        try {
            statsEventManager.refreshSportEventCompletion(mlb, simpleDateFormat.parse("07/13/2014 17:05"));

            SportsDao sportsDao = new SportsDao();

            SportEvent updatedSportEvent1 = sportsDao.findSportEvent(1379173);
            SportEvent updatedSportEvent2 = sportsDao.findSportEvent(1380029);
            SportEvent updatedSportEvent3 = sportsDao.findSportEvent(1234);

            assertTrue(updatedSportEvent1.isComplete() && updatedSportEvent1.getUnitsRemaining() == 0);
            assertTrue(updatedSportEvent2.isComplete() && updatedSportEvent2.getUnitsRemaining() == 0);
            assertTrue(!updatedSportEvent3.isComplete() && updatedSportEvent3.getUnitsRemaining() == 9);
        } catch (ParseException e) {
            fail(e.getMessage());
        }
    }
}
