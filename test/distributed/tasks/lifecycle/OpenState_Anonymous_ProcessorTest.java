package distributed.tasks.lifecycle;

import service.IContestListService;
import common.GlobalConstants;
import dao.IContestDao;
import models.contest.*;
import models.sports.League;
import models.sports.SportEvent;
import models.sports.SportEventGrouping;
import models.sports.SportEventGroupingType;
import models.user.User;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by mwalsh on 7/25/14.
 */
public class OpenState_Anonymous_ProcessorTest {

    private IContestListService contestListManager = EasyMock.createMock(IContestListService.class);
    private IContestDao contestDao = EasyMock.createMock(IContestDao.class);
    private IRandomizer randomizer  = EasyMock.createMock(IRandomizer.class);
    private Contest contest;
    private SportEventGrouping sportEventGrouping;
    private Instant now;

    public int ENTRIES = 10;

    @Before
    public void setup() {
        now = Instant.now();

        SportEventGroupingType sportEventGroupingType = new SportEventGroupingType(League.NFL, "doesn't matter", null);

        SportEvent sportEvent = new SportEvent(123, League.NFL, Date.from(now), "", "", 90, false, 2014, 1, 1);
        SportEvent sportEvent2 = new SportEvent(124, League.NFL, Date.from(now.plus(5, ChronoUnit.HOURS)), "", "", 90, false, 2014, 1, 1);

        sportEventGrouping = new SportEventGrouping(
                Arrays.asList(sportEvent, sportEvent2),sportEventGroupingType);

        List<ContestPayout> contestPayouts = Arrays.asList(new ContestPayout(1, 1, 380));

        contest = new Contest(ContestType.ANONYMOUS_H2H,
                "", League.NFL, ENTRIES, true, 500, 1, 5000000,
                sportEventGrouping, contestPayouts, null);
    }

    @Test
    public void test2PersonEntries() throws Exception {
        List<Entry> entries = new ArrayList<>();
        for(int i = 0; i < 2; i++){
            User randomUser = new User();
            randomUser.setId(new Long(i));
            entries.add(new Entry(randomUser, contest, new Lineup("", randomUser, League.NFL, sportEventGrouping)));
        }

        OpenStateProcessor processor = new OpenStateProcessor(
                now.plus(5, ChronoUnit.SECONDS), contestDao, contestListManager, randomizer);

        EasyMock.expect(contestDao.findEntries(contest)).andReturn(entries);
        EasyMock.expect(randomizer.getRandomInt(2)).andReturn(1);

        EasyMock.expect(contestDao.joinContest(entries.get(0).getUser(), contest, entries.get(0).getLineup())).andReturn(GlobalConstants.CONTEST_ENTRY_SUCCESS);
        EasyMock.expect(contestDao.joinContest(entries.get(1).getUser(), contest, entries.get(1).getLineup())).andReturn(GlobalConstants.CONTEST_ENTRY_SUCCESS);

        contestDao.saveContest(contest);
        EasyMock.expectLastCall();

        //replay
        EasyMock.replay(contestDao);
        EasyMock.replay(randomizer);
        processor.process(contest);

        EasyMock.verify(contestDao);
        EasyMock.verify(randomizer);
    }

    @Test
    public void test10PersonEntries_All_Sequential_No_Repeats() throws Exception {
        List<Entry> entries = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            User randomUser = new User();
            randomUser.setId(new Long(i));
            entries.add(new Entry(randomUser, contest, new Lineup("", randomUser, League.NFL, sportEventGrouping)));
        }

        OpenStateProcessor processor = new OpenStateProcessor(
                now.plus(5, ChronoUnit.SECONDS), contestDao, contestListManager, randomizer);

        EasyMock.expect(contestDao.findEntries(contest)).andReturn(entries);

        EasyMock.expect(randomizer.getRandomInt(10)).andReturn(1);
        EasyMock.expect(contestDao.joinContest(entries.get(0).getUser(), contest, entries.get(0).getLineup())).andReturn(GlobalConstants.CONTEST_ENTRY_SUCCESS);
        EasyMock.expect(contestDao.joinContest(entries.get(1).getUser(), contest, entries.get(1).getLineup())).andReturn(GlobalConstants.CONTEST_ENTRY_SUCCESS);
        contestDao.saveContest(contest);
        EasyMock.expectLastCall();

        EasyMock.expect(randomizer.getRandomInt(8)).andReturn(1);
        EasyMock.expect(contestDao.joinContest(entries.get(2).getUser(), contest, entries.get(2).getLineup())).andReturn(GlobalConstants.CONTEST_ENTRY_SUCCESS);
        EasyMock.expect(contestDao.joinContest(entries.get(3).getUser(), contest, entries.get(3).getLineup())).andReturn(GlobalConstants.CONTEST_ENTRY_SUCCESS);
        contestDao.saveContest(contest);
        EasyMock.expectLastCall();

        EasyMock.expect(randomizer.getRandomInt(6)).andReturn(1);
        EasyMock.expect(contestDao.joinContest(entries.get(4).getUser(), contest, entries.get(4).getLineup())).andReturn(GlobalConstants.CONTEST_ENTRY_SUCCESS);
        EasyMock.expect(contestDao.joinContest(entries.get(5).getUser(), contest, entries.get(5).getLineup())).andReturn(GlobalConstants.CONTEST_ENTRY_SUCCESS);
        contestDao.saveContest(contest);
        EasyMock.expectLastCall();

        EasyMock.expect(randomizer.getRandomInt(4)).andReturn(1);
        EasyMock.expect(contestDao.joinContest(entries.get(6).getUser(), contest, entries.get(6).getLineup())).andReturn(GlobalConstants.CONTEST_ENTRY_SUCCESS);
        EasyMock.expect(contestDao.joinContest(entries.get(7).getUser(), contest, entries.get(7).getLineup())).andReturn(GlobalConstants.CONTEST_ENTRY_SUCCESS);
        contestDao.saveContest(contest);
        EasyMock.expectLastCall();

        EasyMock.expect(randomizer.getRandomInt(2)).andReturn(1);
        EasyMock.expect(contestDao.joinContest(entries.get(8).getUser(), contest, entries.get(8).getLineup())).andReturn(GlobalConstants.CONTEST_ENTRY_SUCCESS);
        EasyMock.expect(contestDao.joinContest(entries.get(9).getUser(), contest, entries.get(9).getLineup())).andReturn(GlobalConstants.CONTEST_ENTRY_SUCCESS);
        contestDao.saveContest(contest);
        EasyMock.expectLastCall();

        //replay
        EasyMock.replay(contestDao);
        EasyMock.replay(randomizer);
        processor.process(contest);

        EasyMock.verify(contestDao);
        EasyMock.verify(randomizer);
    }

    @Test
    public void test5PersonEntries_All_Sequential_No_Repeats() throws Exception {
        List<Entry> entries = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            User randomUser = new User();
            randomUser.setId(new Long(i));
            entries.add(new Entry(randomUser, contest, new Lineup("", randomUser, League.NFL, sportEventGrouping)));
        }

        OpenStateProcessor processor = new OpenStateProcessor(
                now.plus(5, ChronoUnit.SECONDS), contestDao, contestListManager, randomizer);

        EasyMock.expect(contestDao.findEntries(contest)).andReturn(entries);

        EasyMock.expect(randomizer.getRandomInt(5)).andReturn(2);
        EasyMock.expect(contestDao.joinContest(entries.get(0).getUser(), contest, entries.get(0).getLineup())).andReturn(GlobalConstants.CONTEST_ENTRY_SUCCESS);
        EasyMock.expect(contestDao.joinContest(entries.get(1).getUser(), contest, entries.get(1).getLineup())).andReturn(GlobalConstants.CONTEST_ENTRY_SUCCESS);
        contestDao.saveContest(contest);
        EasyMock.expectLastCall();

        EasyMock.expect(randomizer.getRandomInt(3)).andReturn(1);
        EasyMock.expect(contestDao.joinContest(entries.get(2).getUser(), contest, entries.get(2).getLineup())).andReturn(GlobalConstants.CONTEST_ENTRY_SUCCESS);
        EasyMock.expect(contestDao.joinContest(entries.get(3).getUser(), contest, entries.get(3).getLineup())).andReturn(GlobalConstants.CONTEST_ENTRY_SUCCESS);
        contestDao.saveContest(contest);
        EasyMock.expectLastCall();

        //replay
        EasyMock.replay(contestDao);
        EasyMock.replay(randomizer);
        processor.process(contest);

        EasyMock.verify(contestDao);
        EasyMock.verify(randomizer);
    }

//    @Test
//    public void test10PersonEntries_All_Sequential_Half_Repeats() throws Exception {
//        List<Entry> entries = new ArrayList<>();
//
//        for(int i = 0; i < 5; i++){
//            User randomUser = new User();
//            randomUser.setId(new Long(i));
//            entries.add(new Entry(randomUser, contest, new Lineup("", randomUser, League.NFL, sportEventGrouping)));
//        }
//
//        User randomUser = new User();
//        randomUser.setId(new Long(6));
//        for(int i = 0; i < 5; i++){
//            entries.add(new Entry(randomUser, contest, new Lineup("", randomUser, League.NFL, sportEventGrouping)));
//        }
//
//        OpenStateProcessor processor = new OpenStateProcessor(
//                now.plus(5, ChronoUnit.SECONDS), contestDao, contestListManager, randomizer);
//
//        EasyMock.expect(contestDao.findEntries(contest)).andReturn(entries);
//
//        EasyMock.expect(randomizer.getRandomInt(10)).andReturn(1);
//        EasyMock.expect(contestDao.joinContest(entries.get(0).getUser(), contest, entries.get(0).getLineup())).andReturn(GlobalConstants.CONTEST_ENTRY_SUCCESS);
//        EasyMock.expect(contestDao.joinContest(entries.get(1).getUser(), contest, entries.get(1).getLineup())).andReturn(GlobalConstants.CONTEST_ENTRY_SUCCESS);
//        contestDao.saveContest(contest);
//        EasyMock.expectLastCall();
//
//        EasyMock.expect(randomizer.getRandomInt(8)).andReturn(1);
//        EasyMock.expect(contestDao.joinContest(entries.get(2).getUser(), contest, entries.get(2).getLineup())).andReturn(GlobalConstants.CONTEST_ENTRY_SUCCESS);
//        EasyMock.expect(contestDao.joinContest(entries.get(3).getUser(), contest, entries.get(3).getLineup())).andReturn(GlobalConstants.CONTEST_ENTRY_SUCCESS);
//        contestDao.saveContest(contest);
//        EasyMock.expectLastCall();
//
//        EasyMock.expect(randomizer.getRandomInt(6)).andReturn(1);
//        EasyMock.expect(contestDao.joinContest(entries.get(4).getUser(), contest, entries.get(4).getLineup())).andReturn(GlobalConstants.CONTEST_ENTRY_SUCCESS);
//        EasyMock.expect(contestDao.joinContest(entries.get(5).getUser(), contest, entries.get(5).getLineup())).andReturn(GlobalConstants.CONTEST_ENTRY_SUCCESS);
//        contestDao.saveContest(contest);
//        EasyMock.expectLastCall();
//
//        EasyMock.expect(randomizer.getRandomInt(4)).andReturn(1);
//        contestDao.saveContest(contest);
//        EasyMock.expectLastCall();
//
//        EasyMock.expect(randomizer.getRandomInt(4)).andReturn(2);
//        EasyMock.expect(contestDao.joinContest(entries.get(6).getUser(), contest, entries.get(6).getLineup())).andReturn(GlobalConstants.CONTEST_ENTRY_SUCCESS);
//        EasyMock.expect(contestDao.joinContest(entries.get(7).getUser(), contest, entries.get(7).getLineup())).andReturn(GlobalConstants.CONTEST_ENTRY_SUCCESS);
//        contestDao.saveContest(contest);
//        EasyMock.expectLastCall();
//
//        EasyMock.expect(randomizer.getRandomInt(2)).andReturn(1);
//        EasyMock.expect(contestDao.joinContest(entries.get(8).getUser(), contest, entries.get(8).getLineup())).andReturn(GlobalConstants.CONTEST_ENTRY_SUCCESS);
//        EasyMock.expect(contestDao.joinContest(entries.get(9).getUser(), contest, entries.get(9).getLineup())).andReturn(GlobalConstants.CONTEST_ENTRY_SUCCESS);
//        contestDao.saveContest(contest);
//        EasyMock.expectLastCall();
//
//        //replay
//        EasyMock.replay(contestDao);
//        EasyMock.replay(randomizer);
//        processor.process(contest);
//
//        EasyMock.verify(contestDao);
//        EasyMock.verify(randomizer);
//    }
}
