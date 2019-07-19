package service;

import common.GlobalConstants;
import dao.DaoFactory;
import distributed.DistributedServices;
import models.contest.*;
import models.sports.AthleteSportEventInfo;
import models.sports.SportEvent;
import models.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class EdgeCacheService {

    private ContestLiveOverviewService contestLiveOverviewManager;
    private ContestLiveRanksService contestLiveRanksManager;
    private ContestLiveTimelineService contestLiveTimelineManager;
    private ContestLiveDrillinService contestLiveDrillinManager;
    private AthleteContestRankService athleteContestRankManager;
    private AthletePercentOwnedService athletePercentOwnedManager;
    private ContestLiveAthleteService contestLiveAthleteManager;
    private ContestLiveProjectionGraphService contestLiveProjectionGraphManager;
    private ContestLiveLineupService contestLiveLineupManager;
    private AthleteExposureService athleteExposureManager;

    public EdgeCacheService() {
        contestLiveOverviewManager = DistributedServices.getContext().getBean("ContestLiveOverviewManager", ContestLiveOverviewService.class);
        contestLiveAthleteManager = (ContestLiveAthleteService) DistributedServices.getContext().getBean("ContestLiveAthleteManager");
        contestLiveRanksManager = (ContestLiveRanksService) DistributedServices.getContext().getBean("ContestLiveRanksManager");
        contestLiveTimelineManager = (ContestLiveTimelineService) DistributedServices.getContext().getBean("ContestLiveTimelineManager");
        contestLiveDrillinManager = (ContestLiveDrillinService) DistributedServices.getContext().getBean("ContestLiveDrillinManager");
        athleteContestRankManager = (AthleteContestRankService) DistributedServices.getContext().getBean("AthleteContestRankManager");
        athletePercentOwnedManager = (AthletePercentOwnedService) DistributedServices.getContext().getBean("AthletePercentOwnedManager");
        contestLiveProjectionGraphManager = (ContestLiveProjectionGraphService) DistributedServices.getContext().getBean("ContestLiveProjectionGraphManager");
        contestLiveLineupManager = (ContestLiveLineupService) DistributedServices.getContext().getBean("ContestLiveLineupManager");
        athleteExposureManager = (AthleteExposureService) DistributedServices.getContext().getBean("AthleteExposureManager");
    }

    /**
     * Perform necessary evictions.
     *
     * @param entry
     * @param athleteSportEventInfo
     */
    public void evict(Entry entry, AthleteSportEventInfo athleteSportEventInfo) {
        Lineup lineup = entry.getLineup();
        Contest contest = entry.getContest();
        User user = entry.getUser();

        /*
         * Perform evictions for the Contest Live Athletes.
         */
        contestLiveAthleteManager.updateContestLiveAthletesForContest(user);
        contestLiveAthleteManager.updateContestLiveAthletesForContest(user, contest.getUrlId());
        contestLiveAthleteManager.updateContestLiveAthletesForContest(user, contest.getUrlId(), athleteSportEventInfo.getId(), true);
        contestLiveAthleteManager.updateContestLiveAthletesForContest(user, contest.getUrlId(), athleteSportEventInfo.getId(), false);

        /*
         * Perform evictions for the Contest Live Overview.
         */
        contestLiveOverviewManager.updateOverview(entry.getUser());
        contestLiveOverviewManager.updateOverviewLineupAsJson(contest, lineup);

        /*
         * Perform evictions for the Contest Live Ranks.
         */
        contestLiveRanksManager.updateRanks(String.valueOf(contest.getUrlId()), entry.getUser());

        /*
         * Perform evictions for the Contest Live Team Feed.
         */
        contestLiveTimelineManager.removeLineupTimeline(lineup.getId());

        /*
         * Perform evictions for the Contest Live Drill-in.
         */
        contestLiveDrillinManager.updateDrillin(contest);
        contestLiveDrillinManager.updateDrillinEntry(entry);
        contestLiveDrillinManager.updateDrillinAthleteSportEventInfo(athleteSportEventInfo);

        /*
         * Perform evictions for the Athlete Contest Ranks.
         */
        athleteContestRankManager.updateAthleteContestRanks(entry.getUser(), athleteSportEventInfo.getId());

        /*
         * Perform evictions for the Athlete Percent Owned cache.
         */
        athletePercentOwnedManager.updatePercentOwned(contest.getUrlId(), athleteSportEventInfo.getId(), entry.getId());

        /*
         * Perform evictions for the Projection Graph cache
         */
        contestLiveProjectionGraphManager.updateGraph(contest, lineup);

        /*
         * Perform evictions for the Contest Live Lineup cache.
         */
        contestLiveLineupManager.updateLineup(lineup.getId());
    }

    public void evict(List<SportEvent> sportEvents) {
        sportEvents.forEach(contestLiveDrillinManager::updateDrillinSportEvent);
    }

    public void evictOnContestComplete() {
        athletePercentOwnedManager.flushAllCaches();

        DistributedServices.getInstance().getMap(GlobalConstants.NFL_ATHLETE_RANK_MAP).clear();
    }

    /**
     * Perform necessary evictions when a new entry is added to a contest.
     *
     * @param user  The user who entered the contest.
     * @param entry The entry into the contest.
     */
    public void evictOnContestEntry(User user, Entry entry) {
        /*
         * Perform evictions for the Contest Live Athletes.
         *
         * This entry may be a new lineup, so we need to clear the cache for this user so any
         * new athletes will show up.
         */
        contestLiveAthleteManager.updateContestLiveAthletesForContest(user);

        /*
         * When a contest goes into a locked state, its entries qualify for calculation of athlete exposure.  Therefore,
         * when this happens we want exposure cache to be dumped and values recalculated.
         */
        Contest contest = entry.getContest();
        if (contest.getContestState().equals(ContestState.locked) || contest.getContestState().equals(ContestState.rosterLocked)) {
            List<Entry> entries = DaoFactory.getContestDao().findEntries(contest);
            for (Entry contestEntry : entries) {
                for (LineupSpot lineupSpot : contestEntry.getLineup().getLineupSpots()) {
                    athleteExposureManager.updateAthleteExposure(contestEntry.getUser(), String.valueOf(lineupSpot.getAthleteSportEventInfo().getId()));
                }

                athleteExposureManager.updateAthleteExposure(contestEntry.getUser());

                // We need to call this because the exposure and ranks data usually comes along with this call, so
                // flushing the cache above could still leave stale data on the Athlete's page.
                contestLiveAthleteManager.updateContestLiveAthletesForContest(contestEntry.getUser());
            }
        }
    }

    public void evictOnLineupModification() {

    }

    public static void flushAllCaches() {
        IContestListService contestListManager = DistributedServices.getContext().getBean("ContestListManager", IContestListService.class);
        contestListManager.flushAllCaches();

        List<AbstractCachingService> caches = new ArrayList<>();
        caches.add((ContestEventsService) DistributedServices.getContext().getBean("ContestEventsManager"));
        caches.add((ContestEntriesService) DistributedServices.getContext().getBean("ContestEntriesManager"));
        caches.add((ContestAthletesService) DistributedServices.getContext().getBean("ContestAthletesManager"));
//        caches.add((ContestSuggestionManager) DistributedServices.getContext().getBean("ContestSuggestionManager"));
        caches.add((ScoringRulesService) DistributedServices.getContext().getBean("ScoringRulesManager"));
        caches.add((ContestFilterService) DistributedServices.getContext().getBean("ContestFilterManager"));
        caches.add((LineupRulesService) DistributedServices.getContext().getBean("LineupRulesManager"));
        caches.add((LineupService) DistributedServices.getContext().getBean("LineupManager"));
        caches.add((ContestLiveRanksService) DistributedServices.getContext().getBean("ContestLiveRanksManager"));
        caches.add((ContestLiveLineupService) DistributedServices.getContext().getBean("ContestLiveLineupManager"));
        caches.add((ContestLiveOverviewService) DistributedServices.getContext().getBean("ContestLiveOverviewManager"));
        caches.add((ContestLiveAthleteService) DistributedServices.getContext().getBean("ContestLiveAthleteManager"));
        caches.add((ContestLiveTimelineService) DistributedServices.getContext().getBean("ContestLiveTimelineManager"));
        caches.add((ContestLiveDrillinService) DistributedServices.getContext().getBean("ContestLiveDrillinManager"));
        caches.add((AthleteExposureService) DistributedServices.getContext().getBean("AthleteExposureManager"));
        caches.add((AthleteContestRankService) DistributedServices.getContext().getBean("AthleteContestRankManager"));
        caches.add((AthletePercentOwnedService) DistributedServices.getContext().getBean("AthletePercentOwnedManager"));
        caches.add((AthleteCompareService) DistributedServices.getContext().getBean("AthleteCompareManager"));
        //caches.add((ChatManager) DistributedServices.getContext().getBean("ChatManager"));

        Map<String, List<Integer>> probablePitchers = DistributedServices.getInstance().getMap(GlobalConstants.PROBABLE_PITCHERS_MAP);
        probablePitchers.clear();

        for (AbstractCachingService cache : caches) {
            cache.flushAllCaches();
        }
    }
}
