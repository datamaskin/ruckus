package distributed.tasks;

import service.ContestAthletesService;
import service.IContestListService;
import common.GlobalConstants;
import dao.DaoFactory;
import distributed.DistributedServices;
import models.contest.Contest;
import models.contest.ContestState;
import models.sports.AthleteSportEventInfo;
import models.sports.League;
import models.sports.Position;
import models.sports.SportEvent;
import play.Logger;
import stats.manager.IStatsDefenseVsPositionManager;
import stats.manager.nfl.DefenseVsPositionManager;
import stats.translator.IFantasyPointTranslator;

import java.util.List;
import java.util.Map;

/**
 * Created by mgiles on 7/26/14.
 */
public class CachePopulatorTask extends DistributedTask {
    @Override
    protected String execute() throws Exception {
        IStatsDefenseVsPositionManager nflDefenseVsPositionManager = DistributedServices.getContext().getBean("NflDefenseVsPositionManager", IStatsDefenseVsPositionManager.class);
        IFantasyPointTranslator nflTranslator = DistributedServices.getContext().getBean("NFLFantasyPointTranslator", IFantasyPointTranslator.class);
        List<Contest> contests = DaoFactory.getContestDao().findContests(ContestState.open);

        IContestListService contestCache = DistributedServices.getContext().getBean("ContestListManager", IContestListService.class);
        ContestAthletesService athleteCache = (ContestAthletesService) DistributedServices.getContext().getBean("ContestAthletesManager");
        for (Contest contest : contests) {
            try {
                Logger.info("Filling caches for contest: " + contest.getUrlId());

                IFantasyPointTranslator translator = contest.getStatsFantasyPointTranslator(DistributedServices.getContext());
                contestCache.getContestAsJson(contest.getUrlId());

                athleteCache.setTranslator(translator);
                athleteCache.getContestAthletesAsJson(contest.getUrlId());
            } catch (Exception e) {
                Logger.error("Error: " + e.getMessage());
            }
        }

        try {
            cacheAthleteCompareData(nflDefenseVsPositionManager, nflTranslator, contests);
        } catch (Exception e) {
            Logger.error("Error: " + e.getMessage());
        }

        return "ok";
    }

    private void cacheAthleteCompareData(IStatsDefenseVsPositionManager nflDefenseVsPositionManager, IFantasyPointTranslator nflTranslator, List<Contest> contests) {
        boolean nflAthleteCompareCached = false;

        Map<Integer, Map<Integer, Integer>> dvpCache = DistributedServices.getInstance().getMap(GlobalConstants.NFL_DEFENSE_VS_POSITION_MAP);

        for(Contest contest: contests) {
            if(contest.getLeague().equals(League.NFL) && nflAthleteCompareCached) {
                continue;
            }

            List<SportEvent> sportEvents = contest.getSportEventGrouping().getSportEvents();
            if(!sportEvents.isEmpty()) {
                SportEvent sportEvent = sportEvents.get(0);
                List<AthleteSportEventInfo> athleteSportEventInfos = DaoFactory.getSportsDao().findAthleteSportEventInfos(sportEvent);

                for (Position position : Position.ALL_FOOTBALL) {
                    /*
                     * Defense-vs-position calculation.
                     */
                    Logger.info("Caching DvP for " + position.getAbbreviation());
                    ((DefenseVsPositionManager) nflDefenseVsPositionManager).setDvpCache(dvpCache);
                    nflDefenseVsPositionManager.calculateDefenseVsPosition(sportEvent.getStartTime(), position);

                    /*
                     * Athlete Rank calculation.
                     */
                    for(AthleteSportEventInfo athleteSportEventInfo: athleteSportEventInfos) {
                        if(!athleteSportEventInfo.getAthlete().getPositions().contains(position)) {
                            continue;
                        }
                        Logger.info("Caching Athlete Rank for " + position.getAbbreviation());
                        DaoFactory.getSportsDao().calculateRank(position, nflTranslator, athleteSportEventInfo,
                                sportEvent.getSeason(), sportEvent.getLeague(), 17);
                        break;
                    }
                }
            }

            nflAthleteCompareCached = true;
        }
    }
}
