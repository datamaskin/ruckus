package managers.views;

import service.ContestAthletesService;
import service.ContestListService;
import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.contest.*;
import models.sports.League;
import models.sports.SportEvent;
import models.sports.SportEventGrouping;
import models.sports.SportEventGroupingType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utilities.BaseTest;
import utils.TimeService;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by dmaclean on 7/11/14.
 */
public class ContestListManagerTest extends BaseTest {
    Contest contest;
    private ContestListService contestListManager;

    @Before
    public void setUp() {
        ContestAthletesService contestAthletesManager = new ContestAthletesService(new TimeService());
        contestListManager = new ContestListService(contestAthletesManager);

        SportEvent sportEvent1 = new SportEvent(1378361, League.MLB, new Date(), "desc", "shortDesc", 9, false, 2014, -1, 1);
        Ebean.save(sportEvent1);

        // Set up Contest Grouping
        ContestGrouping grouping = new ContestGrouping(ContestGrouping.MLB_ALL.getName(), League.MLB);
        Ebean.save(grouping);

        // Set up payouts
        ContestPayout contestPayout = new ContestPayout(1, 1, 100);
        ArrayList<ContestPayout> contestPayouts = new ArrayList<>();
        contestPayouts.add(contestPayout);

        ArrayList<SportEvent> sportEvents = new ArrayList<>();
        sportEvents.add(sportEvent1);

        SportEventGroupingType type = new SportEventGroupingType(League.MLB, "", null);
        Ebean.save(type);
        SportEventGrouping sportEventGrouping = new SportEventGrouping(sportEvents, type);
        Ebean.save(Arrays.asList(type, sportEventGrouping));

        contest = new Contest(ContestType.H2H, "ABC", League.MLB, 2, true, 100, 2, 5000000, sportEventGrouping, contestPayouts, null);
        contest.setContestState(ContestState.active);
        Ebean.save(contest);
    }

    @After
    public void tearDown() {
        contestListManager = null;
    }

    @Test
    public void testGetContestAsJson_EmptyContest() {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>() {};

        try {
            String result = contestListManager.getContestAsJson(contest.getUrlId());
            Map<String, Object> resultMap = mapper.readValue(result, typeReference);
            assertTrue(result.contains("\"opp\":\"H2H\""));
            assertTrue(result.contains("\"prizePool\":100"));

            List<ContestPayout> p = (List<ContestPayout>) resultMap.get("payout");
            assertEquals(1, p.size());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

//    @Test
//    public void testGetContestAsJson_1EntryContest() {
//        try {
//            String result = contestListManager.getContestAsJson(contest.getUrlId());
//            assertTrue(result.contains("\"opp\":\"H2H\""));
//        } catch (JsonProcessingException e) {
//            fail(e.getMessage());
//        }
//    }
}
