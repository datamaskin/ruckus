package stats.updateprocessor;

import service.ScoringRulesService;
import com.fasterxml.jackson.core.type.TypeReference;
import dao.ISportsDao;
import dao.IStatsDao;
import models.sports.SportEvent;
import models.stats.StatsLiveFeedData;
import org.apache.commons.codec.digest.DigestUtils;
import play.Logger;
import play.Play;
import simulator.ContestSimulationManager;
import stats.translator.IFantasyPointTranslator;
import utils.ITimeService;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by dmaclean on 7/29/14.
 */
public abstract class BaseUpdateProcessor implements IUpdateProcessor {
    protected static final TypeReference<List<Map<String, Object>>> boxScoreTypeReference = new TypeReference<List<Map<String, Object>>>() {};

    protected static final String BOXSCORE_JSON_FIELD_NAME = "name";
    protected static final String BOXSCORE_JSON_FIELD_ABBR = "abbr";
    protected static final String BOXSCORE_JSON_FIELD_AMOUNT = "amount";
    protected static final String BOXSCORE_JSON_FIELD_FPP = "fpp";
    protected static final String BOXSCORE_JSON_FIELD_ID = "id";

    protected ScoringRulesService scoringRulesManager;
    protected IStatsDao statsDao;
    protected ISportsDao sportsDao;
    protected IFantasyPointTranslator fantasyPointTranslator;
    protected ITimeService timeService;

    public BaseUpdateProcessor(ScoringRulesService scoringRulesManager, IStatsDao statsDao, ISportsDao sportsDao, IFantasyPointTranslator fantasyPointTranslator, ITimeService timeService) {
        this.scoringRulesManager = scoringRulesManager;
        this.statsDao = statsDao;
        this.sportsDao = sportsDao;
        this.fantasyPointTranslator = fantasyPointTranslator;
        this.timeService = timeService;
    }

    @Override
    public void recordStatsUpdate(String statData, int gameId) {
        if (Play.isTest() || ContestSimulationManager.isSimulation()) {
            return;
        }

        Logger.info("Recording update for event " + gameId);
        SportEvent sportEvent = sportsDao.findSportEvent(gameId);

        try {
            String hash = DigestUtils.md5Hex(new ByteArrayInputStream(statData.getBytes()));
            if (statsDao.isDuplicateLiveFeed(hash)) {
                Logger.info("Message for " + gameId + " is a duplicate.  Ignoring...");
                return;
            }

            StatsLiveFeedData liveFeedData = new StatsLiveFeedData(sportEvent, statData, hash);
            sportsDao.saveLiveFeedData(liveFeedData);
        } catch (Exception e) {
            Logger.error("Could not record update from Stats: " + e.getMessage());
        }
    }
}
