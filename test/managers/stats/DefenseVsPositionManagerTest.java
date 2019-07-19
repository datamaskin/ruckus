package managers.stats;

import service.ScoringRulesService;
import com.avaje.ebean.Ebean;
import common.GlobalConstants;
import dao.ISportsDao;
import models.contest.ScoringRule;
import models.sports.*;
import models.stats.mlb.StatsMlbBatting;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import renameme.FileStatsRetriever;
import stats.manager.mlb.DefenseVsPositionManager;
import stats.parser.mlb.BattingParser;
import stats.translator.mlb.FantasyPointTranslator;
import utilities.BaseTest;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by dmaclean on 7/13/14.
 */
public class DefenseVsPositionManagerTest extends BaseTest {
    private DefenseVsPositionManager defenseVsPositionManager;

    private ISportsDao sportsDao;

    @Before
    public void setUp() {
        ApplicationContext context = new FileSystemXmlApplicationContext("test/spring-test.xml");
        sportsDao = context.getBean("sportsDao", ISportsDao.class);

        defenseVsPositionManager = (DefenseVsPositionManager) context.getBean("MlbDefenseVsPositionManager");

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

    public void tearDown() {
        defenseVsPositionManager = null;
    }

    @Test
    public void testCalculateDefenseVsPosition() {
        Map<Integer, Map<Integer, Integer>> dvpCache = new HashMap<>();
        defenseVsPositionManager.setDvpCache(dvpCache);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
        try {
            Team tigers = new Team(League.MLB, "Detroit", "Tigers", "DET", 230);
            sportsDao.saveTeam(tigers);

            Team redSox = new Team(League.MLB, "Boston", "Red Sox", "BOS", 231);
            sportsDao.saveTeam(redSox);

            List<Team> teams = new ArrayList<>();
            teams.add(tigers);
            teams.add(redSox);

            Athlete miguelCabrera = new Athlete(213968, "Miguel", "Cabrera", tigers, "12");
            miguelCabrera.setPositions(Arrays.asList(Position.BS_FIRST_BASE));
            Ebean.save(miguelCabrera);

            Athlete albertPujols = new Athlete(12345, "Albert", "Pujols", redSox, "11");
            albertPujols.setPositions(Arrays.asList(Position.BS_FIRST_BASE));
            Ebean.save(albertPujols);

            SportEvent sportEvent1 = new SportEvent(1378361, League.MLB, simpleDateFormat.parse("05/12/2014 19:05"), "desc", "shortDesc", 9, false, 2014, -1, 1);
            sportEvent1.setTeams(teams);
            Ebean.save(sportEvent1);
            SportEvent sportEvent2 = new SportEvent(1419285, League.MLB, simpleDateFormat.parse("06/19/2014 19:05"), "desc", "shortDesc", 9, false, 2014, -1, 1);
            sportEvent2.setTeams(teams);
            Ebean.save(sportEvent2);


            AthleteSportEventInfo athleteSportEventInfoCabrera = new AthleteSportEventInfo(sportEvent1, miguelCabrera, new BigDecimal(0), "", "");
            Ebean.save(athleteSportEventInfoCabrera);

            AthleteSportEventInfo athleteSportEventInfoPujols = new AthleteSportEventInfo(sportEvent1, albertPujols, new BigDecimal(0), "", "");
            Ebean.save(athleteSportEventInfoPujols);

            /*
             * Set up stats for Pujols.
             */
            StatsMlbBatting statsMlbBattingPujols = new StatsMlbBatting();
            statsMlbBattingPujols.setStatProviderId(12345);
            statsMlbBattingPujols.setEventId(1378361);
            statsMlbBattingPujols.setHitsSingles(2);
            statsMlbBattingPujols.setHitsDoubles(2);
            statsMlbBattingPujols.setHitsTriples(2);
            statsMlbBattingPujols.setHitsHomeRuns(2);
            statsMlbBattingPujols.setRunsBattedInTotal(2);
            statsMlbBattingPujols.setRunsScored(2);
            statsMlbBattingPujols.setWalksTotal(2);
            statsMlbBattingPujols.setHitByPitch(2);
            statsMlbBattingPujols.setStolenBasesTotal(2);
            statsMlbBattingPujols.setStolenBasesCaughtStealing(0);
            Ebean.save(statsMlbBattingPujols);

            StatsMlbBatting statsMlbBattingPujols2 = new StatsMlbBatting();
            statsMlbBattingPujols2.setStatProviderId(12345);
            statsMlbBattingPujols2.setEventId(1419285);
            statsMlbBattingPujols2.setHitsSingles(0);
            statsMlbBattingPujols2.setHitsDoubles(0);
            statsMlbBattingPujols2.setHitsTriples(0);
            statsMlbBattingPujols2.setHitsHomeRuns(0);
            statsMlbBattingPujols2.setRunsBattedInTotal(0);
            statsMlbBattingPujols2.setRunsScored(0);
            statsMlbBattingPujols2.setWalksTotal(0);
            statsMlbBattingPujols2.setHitByPitch(0);
            statsMlbBattingPujols2.setStolenBasesTotal(0);
            statsMlbBattingPujols2.setStolenBasesCaughtStealing(100);
            Ebean.save(statsMlbBattingPujols2);

            String results = new FileStatsRetriever("test_files/mlb_batting_stats.json").getResults();
            BattingParser parserBatting = new BattingParser(new FantasyPointTranslator(new ScoringRulesService()));

            parserBatting.parse(results);
            List<StatsMlbBatting> statsBatting = Ebean.find(StatsMlbBatting.class).findList();
            List<StatsMlbBatting> removal = new ArrayList<>();
            for (StatsMlbBatting statsMlbBatting : statsBatting) {
                if (statsMlbBatting.getEventId() != 1419285 && statsMlbBatting.getEventId() != 1378361) {
                    removal.add(statsMlbBatting);
                }
            }
            for (StatsMlbBatting statsMlbBatting : removal) {
                statsBatting.remove(statsMlbBatting);
            }
            Ebean.save(statsBatting);

            defenseVsPositionManager.calculateDefenseVsPosition(2014);

            assertTrue(!dvpCache.containsKey(Position.BS_FLEX.getId()));
            assertTrue(dvpCache.size() == 6);
            assertTrue(dvpCache.get(Position.BS_FIRST_BASE.getId()).size() == 2);
            assertTrue(dvpCache.get(Position.BS_FIRST_BASE.getId()).get(redSox.getId()) == 1);
            assertTrue(dvpCache.get(Position.BS_FIRST_BASE.getId()).get(tigers.getId()) == 2);

        } catch (Exception e) {
            fail(e.getMessage());
        }

    }
}