package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.DaoFactory;
import models.contest.*;
import models.user.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import utils.ITimeService;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Created by mgiles on 6/21/14.
 */
public class ContestLiveOverviewService extends AbstractCachingService {

    Map<Contest, List<Entry>> entriesForContests = new HashMap<>();

    private User user;

    private ObjectMapper mapper = new ObjectMapper();

    private TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {
    };

    private ITimeService timeService;

    public ContestLiveOverviewService(ITimeService timeService) {
        this.timeService = timeService;
    }

    public String getHistoricalOverviewAsJson(User user, int numDays) throws IOException {
        /*
         * Create a map of Contests for each lineup.
         */
        Instant daysAgo = timeService.getNow().minus(numDays, ChronoUnit.DAYS);
        final List<Entry> entries = DaoFactory.getContestDao().findHistoricalEntries(user, ContestState.history, Date.from(daysAgo));
        final List<Map<String, Object>> lineupList = createLineupListFromEntries(entries);
        lineupList.sort((lhs, rhs) -> ((Long)rhs.get("startTime")).compareTo((Long)lhs.get("startTime"))); // most recent first
        return mapper.writeValueAsString(lineupList);
    }

    @Cacheable(value = "liveOverview")
    public String getOverviewAsJson(User user) throws IOException {
        List<ContestState> contestStates = new ArrayList<>();
        contestStates.add(ContestState.active);
        contestStates.add(ContestState.open);
        contestStates.add(ContestState.locked);
        contestStates.add(ContestState.rosterLocked);
        contestStates.add(ContestState.complete);

        /*
         * Create a map of Contests for each lineup.
         */
        final List<Entry> entries = DaoFactory.getContestDao().findEntries(user, contestStates);
        final List<Map<String, Object>> lineupList = createLineupListFromEntries(entries);
        lineupList.sort((lhs, rhs) -> ((Long)lhs.get("startTime")).compareTo((Long)rhs.get("startTime")));
        return mapper.writeValueAsString(lineupList);
    }

    private List<Map<String, Object>> createLineupListFromEntries(List<Entry> entries) throws IOException {
        Map<Contest, Set<Lineup>> lineupContestMap = new HashMap<>();
        for (Entry entry : entries) {
            Contest contest = entry.getContest();
            Set<Lineup> lineups = lineupContestMap.get(contest);
            if (lineups == null) {
                lineups = new HashSet<>();
            }
            lineups.add(entry.getLineup());
            lineupContestMap.put(contest, lineups);
        }

        List<Map<String, Object>> lineupList = new ArrayList<>();

        // Loop through contests
        for(ContestState state: Arrays.asList(
                ContestState.active, ContestState.complete,
                ContestState.open, ContestState.locked, ContestState.rosterLocked, ContestState.history)){
            for (Map.Entry<Contest, Set<Lineup>> entry : lineupContestMap.entrySet()) {
                if(entry.getKey().getContestState().equals(state)){
                    for (Lineup lineup : entry.getValue()) {
                        Map<String, Object> data = mapper.readValue(getOverviewLineupAsJson(entry.getKey(), lineup), typeRef);
                        lineupList.add(data);
                    }
                }
            }
        }

        return lineupList;
    }

    @Cacheable(value = "liveOverviewEntry")
    public String getOverviewLineupAsJson(Contest contest, Lineup lineup) throws JsonProcessingException {
        Map<String, Object> data = new HashMap<>();

        /*
         * Find the best position of this lineup.
         *
         * TODO: This can be really expensive to do for a big GPP.  Replace ASAP.
         */
        List<Entry> allEntries = DaoFactory.getContestDao().findEntriesAndSort(contest);
        int i = 0;
        Entry bestEntry = null;
        for (Entry entry : allEntries) {
            if (entry.getLineup().getId() == lineup.getId()) {
                bestEntry = entry;
                break;
            }
            i++;
        }

        int position = i + 1;
        data.put("position", position);

        BigDecimal fpp = new BigDecimal(bestEntry.getPoints());
        fpp = fpp.setScale(2, RoundingMode.HALF_EVEN);
        data.put("fpp", fpp);

        // Determine current payout
        List<ContestPayout> payouts = contest.getContestPayouts();
        int payoutAmount = 0;
        int prizePool = 0;
        for (ContestPayout payout : payouts) {
            if (payout.getLeadingPosition() <= position && position <= payout.getTrailingPosition()) {
                payoutAmount = payout.getPayoutAmount();
            }
            prizePool += (payout.getTrailingPosition() - payout.getLeadingPosition() + 1) * payout.getPayoutAmount();
        }
        data.put("payout", payoutAmount);
        data.put("projectedPayout", payoutAmount);      // TODO: Wire in with Mitch's analytics
        data.put("unitsRemaining", DaoFactory.getContestDao().calculateUnitsRemaining(lineup));
        data.put("league", contest.getLeague().getAbbreviation());
        data.put("entryFee", contest.getEntryFee());
        data.put("contestType", contest.getContestType());
        data.put("displayName", contest.getDisplayName());
        data.put("contestId", contest.getUrlId());
        data.put("currentEntries", contest.getCurrentEntries());
        data.put("multiplier", DaoFactory.getContestDao().findEntries(lineup, contest).size());
        data.put("prizePool", prizePool);
        data.put("payouts", payouts);
        data.put("contestState", contest.getContestState().getName());
        data.put("timeUntilStart", contest.getStartTime().getTime() - new Date().getTime());
        data.put("startTime", contest.getStartTime().getTime());
        data.put("lineupId", lineup.getId());
        data.put("capacity", contest.getCapacity());
        if (contest.getContestType().equals(ContestType.H2H)) {
            List<Entry> entries = DaoFactory.getContestDao().findEntries(contest);
            if (entries.get(0).getUser().equals(lineup.getUser())) {
                if (entries.size() > 1) {
                    data.put("opp", entries.get(1).getUser().getUserName());
                }
            } else {
                if (entries.size() > 0) {
                    data.put("opp", entries.get(0).getUser().getUserName());
                }
            }
        }

        return mapper.writeValueAsString(data);
    }

    @CacheEvict(value = "liveOverview")
    public void updateOverview(User user) {
        //TODO: need to update a topic somewhere. This call will invalidate the cache
    }

    @CacheEvict(value = "liveOverviewEntry")
    public void updateOverviewLineupAsJson(Contest contest, Lineup lineup) {

    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "liveOverviewEntry", allEntries = true),
            @CacheEvict(value = "liveOverview", allEntries = true)})
    public void flushAllCaches() {

    }
}
