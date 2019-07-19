package managers.views;

import service.AthleteCompareService;
import service.AthletePercentOwnedService;
import service.edge.TestScoringRulesService;
import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.GlobalConstants;
import dao.ISportsDao;
import models.contest.*;
import models.sports.*;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import stats.manager.nfl.DefenseVsPositionManager;
import stats.translator.nfl.FantasyPointTranslator;
import utilities.BaseTest;
import utils.ITimeService;
import utils.TimeService;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by dmaclean on 7/9/14.
 * Modified by gislas on 8/8/14.
 */
public class AthleteCompareManagerTest extends BaseTest {
    private AthleteCompareService athleteCompareManager;

    private ObjectMapper mapper = new ObjectMapper();
    private TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>() {
    };

    private ISportsDao sportsDao;

    @BeforeClass
    public static void setup2() {
    }

    @Before
    public void setUp() {
        sportsDao = context.getBean("sportsDao", ISportsDao.class);
        athleteCompareManager = context.getBean("AthleteCompareManager", AthleteCompareService.class);

        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_SINGLE_LABEL, League.MLB, GlobalConstants.SCORING_MLB_SINGLE_FACTOR));
        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_DOUBLE_LABEL, League.MLB, GlobalConstants.SCORING_MLB_DOUBLE_FACTOR));
        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_TRIPLE_LABEL, League.MLB, GlobalConstants.SCORING_MLB_TRIPLE_FACTOR));
        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_HOMERUN_LABEL, League.MLB, GlobalConstants.SCORING_MLB_HOMERUN_FACTOR));
        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_RUN_BATTED_IN_LABEL, League.MLB, GlobalConstants.SCORING_MLB_RUN_BATTED_IN_FACTOR));
        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_RUN_LABEL, League.MLB, GlobalConstants.SCORING_MLB_RUN_FACTOR));
        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_WALK_LABEL, League.MLB, GlobalConstants.SCORING_MLB_WALK_FACTOR));
        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_HIT_BY_PITCH_LABEL, League.MLB, GlobalConstants.SCORING_MLB_HIT_BY_PITCH_FACTOR));
        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_STOLEN_BASE_LABEL, League.MLB, GlobalConstants.SCORING_MLB_STOLEN_BASE_FACTOR));
        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_CAUGHT_STEALING_LABEL, League.MLB, GlobalConstants.SCORING_MLB_CAUGHT_STEALING_FACTOR));

        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_INNING_PITCHED_LABEL, League.MLB, GlobalConstants.SCORING_MLB_INNING_PITCHED_FACTOR));
        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_STRIKEOUT_LABEL, League.MLB, GlobalConstants.SCORING_MLB_STRIKEOUT_FACTOR));
        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_WIN_LABEL, League.MLB, GlobalConstants.SCORING_MLB_WIN_FACTOR));
        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_EARNED_RUN_LABEL, League.MLB, GlobalConstants.SCORING_MLB_EARNED_RUN_FACTOR));
        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_PITCHER_HIT_LABEL, League.MLB, GlobalConstants.SCORING_MLB_PITCHER_HIT_FACTOR));
        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_PITCHER_WALK_LABEL, League.MLB, GlobalConstants.SCORING_MLB_PITCHER_WALK_FACTOR));
        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_PITCHER_HIT_BY_PITCH_LABEL, League.MLB, GlobalConstants.SCORING_MLB_PITCHER_HIT_BY_PITCH_FACTOR));
    }

    @After
    public void tearDown() {
        athleteCompareManager = null;
    }

    @Test
    public void testGetComparison_NFLOffense() {
        Team patriots = new Team(League.NFL, "New England", "Patriots", "NE", 230);
        sportsDao.saveTeam(patriots);

        Team ravens = new Team(League.NFL, "Baltimore", "Ravens", "BAL", 231);
        sportsDao.saveTeam(ravens);

        List<Team> teams = new ArrayList<>();
        teams.add(patriots);
        teams.add(ravens);

        SportEvent sportEvent1 = new SportEvent(1378361, League.NFL, new Date(), "desc", "shortDesc", 60, false, 2014, -1, 1);
        sportEvent1.setTeams(teams);
        sportsDao.saveSportEvent(sportEvent1);

        Athlete tomBrady = new Athlete(213968, "Tom", "Brady", patriots, "12");
        tomBrady.setPositions(Arrays.asList(Position.FB_QUARTERBACK));
        Ebean.save(tomBrady);

        Athlete athletePatriots = new Athlete(230, "", "Patriots", patriots, "DEF");
        athletePatriots.setPositions(Arrays.asList(Position.FB_DEFENSE));
        Ebean.save(athletePatriots);

        Athlete athleteRavens = new Athlete(231, "", "Ravens", ravens, "DEF");
        athleteRavens.setPositions(Arrays.asList(Position.FB_DEFENSE));
        Ebean.save(athleteRavens);

        AthleteSportEventInfo athleteSportEventInfoTomBrady = new AthleteSportEventInfo(sportEvent1, tomBrady, new BigDecimal(0), "", "");
        Ebean.save(athleteSportEventInfoTomBrady);

        /*
         * Set up for contests/entries.
         */
        // Set up Contest Grouping
        ContestGrouping grouping = new ContestGrouping(ContestGrouping.NFL_FULL.getName(), League.NFL);
        Ebean.save(grouping);

        SportEventGroupingType type = new SportEventGroupingType(League.NFL, "", null);
        Ebean.save(type);
        SportEventGrouping sportEventGrouping = new SportEventGrouping(Arrays.asList(sportEvent1), type);
        Ebean.save(Arrays.asList(type, sportEventGrouping));

        // Set up Contest
        Contest contest = new Contest(ContestType.H2H, "212312", League.NFL, 2, true, 100, 1, 50000, sportEventGrouping, Arrays.asList(new ContestPayout(1, 1, 100)), null);
        contest.setContestState(ContestState.active);
        Ebean.save(contest);



        ITimeService timeService = new TimeService();
        DefenseVsPositionManager defenseVsPositionManager = new DefenseVsPositionManager(timeService);
        defenseVsPositionManager.setDvpCache(new HashMap<>());

        athleteCompareManager.setAthletePercentOwnedManager(new AthletePercentOwnedService());
        athleteCompareManager.setDefenseVsPositionManager(defenseVsPositionManager);
        athleteCompareManager.setTranslator(new FantasyPointTranslator(new TestScoringRulesService()));

        try {
            String result = athleteCompareManager.getComparison(contest.getUrlId(), athleteSportEventInfoTomBrady.getId());

            Map<String, Object> resultMap = mapper.readValue(result, typeReference);
            assertEquals("", resultMap.get("injuryStatus"));

            tomBrady.setInjuryStatus("probable");
            sportsDao.saveAthlete(tomBrady);

            result = athleteCompareManager.getComparison(contest.getUrlId(), athleteSportEventInfoTomBrady.getId());

            resultMap = mapper.readValue(result, typeReference);
            assertEquals("probable", resultMap.get("injuryStatus"));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetComparison_BadAthleteSportEventInfoId() {
        try {
            String result = athleteCompareManager.getComparison("", (Integer)99999);
            Map<String, Object> data = mapper.readValue(result, typeReference);

            assertTrue(data.get("error").equals("Unable to parse provided id for AthleteSportEventInfo."));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetComparison_BadContestId() {
        try {
            Team giants = new Team(League.MLB, "San Francisco", "Giants", "SF", 230);
            sportsDao.saveTeam(giants);

            Athlete timLincecum = new Athlete(326472, "Tim", "Lincecum", giants, "12");
            Ebean.save(timLincecum);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
            SportEvent sportEvent1 = new SportEvent(1380308, League.MLB, simpleDateFormat.parse("06/13/2014 19:05"), "desc", "shortDesc", 9, false, 2014, -1, 1);
            Ebean.save(sportEvent1);

            AthleteSportEventInfo athleteSportEventInfo1 = new AthleteSportEventInfo(sportEvent1, timLincecum, new BigDecimal(0), "", "");
            Ebean.save(athleteSportEventInfo1);

            String result = athleteCompareManager.getComparison("", (Integer)athleteSportEventInfo1.getId());
            Map<String, Object> data = mapper.readValue(result, typeReference);

            assertTrue(data.get("error").equals("Unable to parse provided id for Contest."));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
