package distributed.tasks;

import dao.ISportsDao;
import models.sports.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stats.retriever.IAthleteInjuryRetriever;
import stats.retriever.IAthleteRetriever;
import utilities.BaseTest;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by dmaclean on 7/25/14.
 */
public class AthleteUpdaterTaskTest extends BaseTest {

    private AthleteUpdaterTask athleteUpdaterTask;

    private Athlete athleteTomBrady;
    private Athlete athleteGronk;
    private Athlete athleteEdelman;
    private Athlete athletePeytonManning;
    private AthleteSportEventInfo athleteSportEventInfoBrady;
    private AthleteSportEventInfo athleteSportEventInfoBrady2;
    private AthleteSportEventInfo athleteSportEventInfoGronk;
    private AthleteSportEventInfo athleteSportEventInfoGronk2;
//    private AthleteSportEventInfo athleteSportEventInfoEdelman;
    private AthleteSportEventInfo athleteSportEventInfoManning;
    private Team patriots;
    private Team ravens;
    private Team broncos;
    private Team raiders;
    private SportEvent sportEvent;
    private SportEvent sportEvent2;
    private SportEvent sportEvent3;
    private SportEvent sportEvent4;

    private ISportsDao sportsDao;

    @Before
    public void setUp() {
        IAthleteInjuryRetriever athleteInjuryRetriever = new TestAthleteInjuryRetriever();
        IAthleteRetriever athleteStatRetriever = new TestAthleteRetriever();
        sportsDao = context.getBean("sportsDao", ISportsDao.class);

        athleteUpdaterTask = new AthleteUpdaterTask(athleteInjuryRetriever, athleteStatRetriever);

        // Set up Team
        patriots = new Team(League.NFL, "New England", "Patriots", "NE", 1);
        sportsDao.saveTeam(patriots);

        ravens = new Team(League.NFL, "Baltimore", "Ravens", "BAL", 2);
        sportsDao.saveTeam(ravens);

        broncos = new Team(League.NFL, "Denver", "Broncos", "DEN", 3);
        sportsDao.saveTeam(broncos);

        raiders = new Team(League.NFL, "Oakland", "Raiders", "OAK", 4);
        sportsDao.saveTeam(raiders);

        // Set up Athlete
        athleteTomBrady = new Athlete(1, "Tom", "Brady", patriots, "12");
        athleteTomBrady.setPositions(Arrays.asList(Position.FB_QUARTERBACK));
        sportsDao.saveAthlete(athleteTomBrady);

        athleteGronk = new Athlete(2, "Rob", "Gronkowski", patriots, "87");
        athleteGronk.setPositions(Arrays.asList(Position.FB_TIGHT_END));
        sportsDao.saveAthlete(athleteGronk);

        athleteEdelman = new Athlete(3, "Julian", "Edelman", patriots, "80");
        athleteEdelman.setPositions(Arrays.asList(Position.FB_WIDE_RECEIVER));
//        sportsDao.saveAthlete(athleteEdelman);

        athletePeytonManning = new Athlete(4, "Peyton", "Manning", broncos, "9");
        athletePeytonManning.setPositions(Arrays.asList(Position.FB_QUARTERBACK));
        sportsDao.saveAthlete(athletePeytonManning);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 1);
        Date today = cal.getTime();
        cal.add(Calendar.DATE, 1);
        Date tomorrow = cal.getTime();
        cal.add(Calendar.DATE, 2);
        Date twoDays = cal.getTime();

        // Set up SportEvent
        sportEvent = new SportEvent(1, League.NFL, today, "test", "", 60, false, 2014, -1, 1);
        sportEvent.setTeams(Arrays.asList(patriots, ravens));
        sportsDao.saveSportEvent(sportEvent);

        sportEvent2 = new SportEvent(2, League.NFL, tomorrow, "test", "", 60, false, 2014, -1, 1);
        sportEvent2.setTeams(Arrays.asList(patriots, ravens));
        sportsDao.saveSportEvent(sportEvent2);

//        sportEvent3 = new SportEvent(3, League.NFL, twoDays, "test", "", 60, false);
//        sportEvent3.setTeams(Arrays.asList(patriots, ravens));
//        sportsDao.saveSportEvent(sportEvent3);

        sportEvent4 = new SportEvent(4, League.NFL, tomorrow, "test", "", 60, false, 2014, -1, 1);
        sportEvent4.setTeams(Arrays.asList(broncos, raiders));
        sportsDao.saveSportEvent(sportEvent4);

        athleteSportEventInfoBrady = new AthleteSportEventInfo(sportEvent, athleteTomBrady, new BigDecimal("10.00"), "", "");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoBrady);
        athleteSportEventInfoBrady2 = new AthleteSportEventInfo(sportEvent2, athleteTomBrady, new BigDecimal("10.00"), "", "");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoBrady2);

        athleteSportEventInfoGronk = new AthleteSportEventInfo(sportEvent, athleteGronk, new BigDecimal("12.00"), "", "");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoGronk);
        athleteSportEventInfoGronk2 = new AthleteSportEventInfo(sportEvent2, athleteGronk, new BigDecimal("12.00"), "", "");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoGronk2);
//        athleteSportEventInfoEdelman = new AthleteSportEventInfo(sportEvent, athleteEdelman, new BigDecimal("11.00"),
//                "[{\"receivingYards\":90}]",
//                "[{\"timestamp\":12345,\"description\":\"test\", \"fpChange\":\"+1\",\"athleteSportEventInfoId\":3}]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoEdelman);
        athleteSportEventInfoManning = new AthleteSportEventInfo(sportEvent, athletePeytonManning, new BigDecimal("10.00"), "", "");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoManning);
    }

    @After
    public void tearDown() {
        athleteUpdaterTask = null;
    }

    @Test
    public void testExecute() {
        assertTrue(sportsDao.findAthleteSportEventInfos(athleteEdelman).isEmpty());

        try {
            athleteUpdaterTask.execute();

            Athlete dbAthleteEdelman = sportsDao.findAthlete(athleteEdelman.getStatProviderId());

            List<AthleteSportEventInfo> athleteSportEventInfoList = sportsDao.findAthleteSportEventInfos(dbAthleteEdelman);
            assertTrue(athleteSportEventInfoList.size() == 2);
            assertTrue(athleteSportEventInfoList.get(0).getSportEvent().equals(sportEvent));
            assertTrue(athleteSportEventInfoList.get(1).getSportEvent().equals(sportEvent2));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private class TestAthleteInjuryRetriever implements IAthleteInjuryRetriever {

        @Override
        public Map<Integer, String> getAthleteInjuries(League league) {
            return new HashMap<>();
        }
    }

    private class TestAthleteRetriever implements IAthleteRetriever {

        @Override
        public List<Athlete> getAllAthletesForLeague(League league) {
            if(league.equals(League.NFL)) {
                return Arrays.asList(athleteEdelman, athleteGronk, athletePeytonManning, athleteTomBrady);
            }
            return new ArrayList<>();
        }

        @Override
        public Athlete getAthlete(League league, Integer id) {
            return null;
        }
    }
}
