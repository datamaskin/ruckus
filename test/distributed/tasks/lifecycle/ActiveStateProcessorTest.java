package distributed.tasks.lifecycle;

import service.IChatService;
import service.IContestListService;
import com.avaje.ebean.Ebean;
import dao.ContestDao;
import dao.IContestDao;
import models.contest.Contest;
import models.contest.ContestPayout;
import models.contest.ContestState;
import models.contest.ContestType;
import models.sports.League;
import models.sports.SportEvent;
import models.sports.SportEventGrouping;
import models.sports.SportEventGroupingType;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import utilities.BaseTest;
import utils.ContestIdGeneratorImpl;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by mwalsh on 7/25/14.
 */
public class ActiveStateProcessorTest extends BaseTest {

    private ActiveStateProcessor processor;
    private SportEventGrouping sportEventGrouping;
    private IContestDao contestDao;
    private Instant now;
    private SportEvent sportEvent1;
    private SportEvent sportEvent2;
    private SportEvent sportEvent3;
    private SportEvent sportEvent4;
    
    @Before
    public void setup() {
        contestDao = new ContestDao(new ContestIdGeneratorImpl());
        IContestListService contestListManager = EasyMock.createMock(IContestListService.class);
        IChatService chatManager = EasyMock.createMock(IChatService.class);
        processor = new ActiveStateProcessor(contestDao, contestListManager, chatManager);

        SportEventGroupingType sportEventGroupingType = new SportEventGroupingType(League.NFL, "doesn't matter", null);
        Ebean.save(sportEventGroupingType);

        now = Instant.now();

        sportEvent1 = new SportEvent(123, League.NFL, Date.from(now), "", "", 90, false, 2014, 1, 1);
        sportEvent2 = new SportEvent(234, League.NFL, Date.from(now), "", "", 90, false, 2014, 1, 1);
        sportEvent3 = new SportEvent(345, League.NFL, Date.from(now), "", "", 90, false, 2014, 1, 1);
        sportEvent4 = new SportEvent(456, League.NFL, Date.from(now), "", "", 90, false, 2014, 1, 1);
        List<SportEvent> events = Arrays.asList(sportEvent1, sportEvent2, sportEvent3, sportEvent4);
        Ebean.save(events);

        sportEventGrouping = new SportEventGrouping(events, sportEventGroupingType);
        Ebean.save(sportEventGrouping);
    }

    @Test
    public void testAllGamesComplete() throws Exception {
        List<ContestPayout> contestPayouts = Arrays.asList(new ContestPayout(1, 1, 380));
        Contest contest = new Contest(ContestType.NORMAL, "", League.NFL, 2,
                true, 200, 1, 5000000, sportEventGrouping, contestPayouts, null);

        contest.proceedNext();
        contestDao.saveContest(contest);
        processor.process(contest);
        assertEquals(ContestState.open, contestDao.findContest(contest.getId()).getContestState());

        contest.proceedNext();
        contestDao.saveContest(contest);
        processor.process(contest);
        assertEquals(ContestState.locked, contestDao.findContest(contest.getId()).getContestState());

        contest.proceedNext();
        contestDao.saveContest(contest);
        processor.process(contest);
        assertEquals(ContestState.rosterLocked, contestDao.findContest(contest.getId()).getContestState());

        contest.proceedNext();
        contestDao.saveContest(contest);
        processor.process(contest);
        assertEquals(ContestState.active, contestDao.findContest(contest.getId()).getContestState());

        sportEvent1.setComplete(true);
        processor.process(contest);
        assertEquals(ContestState.active, contestDao.findContest(contest.getId()).getContestState());

        sportEvent2.setComplete(true);
        processor.process(contest);
        assertEquals(ContestState.active, contestDao.findContest(contest.getId()).getContestState());

        sportEvent3.setComplete(true);
        processor.process(contest);
        assertEquals(ContestState.active, contestDao.findContest(contest.getId()).getContestState());

        sportEvent4.setComplete(true);
        processor.process(contest);
        assertEquals(ContestState.complete, contestDao.findContest(contest.getId()).getContestState());

    }

}
