package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.DaoFactory;
import dao.ISportsDao;
import models.contest.Contest;
import models.sports.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import stats.manager.IStatsDefenseVsPositionManager;
import stats.translator.IFantasyPointTranslator;
import utils.ITimeService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Retrieves data for the athlete comparison tool.
 */
public class AthleteCompareService extends AbstractCachingService {

    private ISportsDao sportsDao;
    private ObjectMapper mapper = new ObjectMapper();
    private IFantasyPointTranslator translator;
    private IStatsDefenseVsPositionManager defenseVsPositionManager;
    private AthletePercentOwnedService athletePercentOwnedManager;
    private ITimeService timeService;

    public AthleteCompareService(ISportsDao sportsDao, AthletePercentOwnedService athletePercentOwnedManager, ITimeService timeService) {
        this.sportsDao = sportsDao;
        this.athletePercentOwnedManager = athletePercentOwnedManager;
        this.timeService = timeService;
    }

    @Cacheable(value = "athleteComparison", key = "#athleteSportEventInfoId")
    public String getComparison(String contestId, Integer athleteSportEventInfoId) throws JsonProcessingException {
        Map<String, Object> data = new HashMap<>();
        AthleteSportEventInfo athleteSportEventInfo = DaoFactory.getSportsDao().findAthleteSportEventInfo(athleteSportEventInfoId);
        Contest contest = DaoFactory.getContestDao().findContest(contestId);
        if (athleteSportEventInfo == null) {
            data.put("error", "Unable to parse provided id for AthleteSportEventInfo.");
            return mapper.writeValueAsString(data);
        }
        if (contest == null) {
            data.put("error", "Unable to parse provided id for Contest.");
            return mapper.writeValueAsString(data);
        }

        /*
         * FPPG
         */
        data.put("fppg", DaoFactory.getSportsDao().calculateFantasyPointsPerGame(translator, timeService, athleteSportEventInfo, 17));

        /*
         * Rank
         */
        Position position = athleteSportEventInfo.getAthlete().getPositions().get(0);
        Map<String, Object> rankData = new HashMap<>();
        int[] rankResult = DaoFactory.getSportsDao().calculateRank(position, translator, athleteSportEventInfo,
                athleteSportEventInfo.getSportEvent().getSeason(), athleteSportEventInfo.getSportEvent().getLeague(), 17);
        rankData.put("place", rankResult[0]);
        rankData.put("total", rankResult[1]);
        data.put("rank", rankData);

        /*
         * Averages
         */
        Map<String, BigDecimal> averages = calculateStatAverages(athleteSportEventInfo, 5);
        List<Map<String, Object>> averagesList = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : averages.entrySet()) {
            Map<String, Object> currAvgMap = new HashMap<>();
            currAvgMap.put("name", entry.getKey());
            currAvgMap.put("value", entry.getValue());
            averagesList.add(currAvgMap);
        }
        data.put("averages", averagesList);

        AthleteSportEventInfo previousASEI = sportsDao.findPreviousAthleteSportEventInfo(athleteSportEventInfo);
        data.put("percentOwned", previousASEI == null ? BigDecimal.ZERO : athletePercentOwnedManager.getPercentOwnedForAthlete(contest, previousASEI));

        SportEvent sportEvent = athleteSportEventInfo.getSportEvent();
        Map<Integer, Integer> dvp = defenseVsPositionManager.calculateDefenseVsPosition(sportEvent.getStartTime(), position);
        Team opponent = (athleteSportEventInfo.getAthlete().getTeam().getId() == sportEvent.getTeams().get(0).getId()) ? sportEvent.getTeams().get(1) : sportEvent.getTeams().get(0);
        data.put("defenseVsPosition", dvp.get(opponent.getId()));

        data.put("injuryStatus", athleteSportEventInfo.getAthlete().getInjuryStatus());

        return mapper.writeValueAsString(data);
    }

    public Map<String, BigDecimal> calculateStatAverages(AthleteSportEventInfo athleteSportEventInfo,
                                                         int pastNGames) {
        if (athleteSportEventInfo.getSportEvent().getLeague().equals(League.MLB) ||
                athleteSportEventInfo.getSportEvent().getLeague().equals(League.NFL)) {
            return sportsDao.calculateStatAverages(athleteSportEventInfo, pastNGames);
        }

        return null;
    }


    @CacheEvict(value = "athleteComparison", key = "#athleteSportEventInfoId")
    public void updateComparison(Integer athleteSportEventInfoId) {
    }

    public IFantasyPointTranslator getTranslator() {
        return translator;
    }

    public void setTranslator(IFantasyPointTranslator translator) {
        this.translator = translator;
    }

    public void setAthletePercentOwnedManager(AthletePercentOwnedService athletePercentOwnedManager) {
        this.athletePercentOwnedManager = athletePercentOwnedManager;
    }

    public void setDefenseVsPositionManager(IStatsDefenseVsPositionManager defenseVsPositionManager) {
        this.defenseVsPositionManager = defenseVsPositionManager;
    }

    @Override
    @CacheEvict(value = "athleteComparison", allEntries = true)
    public void flushAllCaches() {}
}
