package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.DaoFactory;
import models.contest.*;
import models.sports.AthleteSportEventInfo;
import models.sports.Position;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Calculates the percent owned that an athlete is for a contest.
 */
public class AthletePercentOwnedService extends AbstractCachingService {

    private ObjectMapper mapper = new ObjectMapper();
    private TypeReference<Map<Integer, BigDecimal>> typeReference = new TypeReference<Map<Integer, BigDecimal>>() {
    };

    @Cacheable(value = "percentOwnedAllData", key = "{#contestId, #athleteSportEventInfoId}")
    public String getPercentOwned(String contestId, Integer athleteSportEventInfoId, Integer entryId) throws JsonProcessingException {
        // Send back an empty list of the contest is invalid.
        if (contestId == null || athleteSportEventInfoId == null || entryId == null) {
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("error", "Unable to parse id for AthleteSportEventInfo or Contest.");

            return mapper.writeValueAsString(errorData);
        }

        Contest contest = DaoFactory.getContestDao().findContest(contestId);
        if (contest != null) {
            if (!contest.getContestState().equals(ContestState.active)
                    && !contest.getContestState().equals(ContestState.complete)
                    && !contest.getContestState().equals(ContestState.history)) {
                return new ObjectMapper().writeValueAsString(new HashMap<>());
            }
        }

        AthleteSportEventInfo athleteSportEventInfo = DaoFactory.getSportsDao().findAthleteSportEventInfo(athleteSportEventInfoId);
        Entry entry = DaoFactory.getContestDao().findEntry(entryId);

        // Send back an empty list of the contest is invalid.
        if (contest == null || athleteSportEventInfo == null || entry == null) {
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("error", "Unable to parse id for AthleteSportEventInfo or Contest.");

            return mapper.writeValueAsString(errorData);
        }

        /*
         * Determine position of interest.
         */
        Position position = athleteSportEventInfo.getAthlete().getPositions().get(0);

        Map<String, Object> data = new HashMap<>();

        constructAllList(data, contest, position);

        List<Entry> entries = DaoFactory.getContestDao().findEntriesAndSort(contest);
//        Collections.sort(entries, (o1, o2) -> new Double(o2.getPoints()).compareTo(new Double(o1.getPoints())));

        constructAboveList(contest, entry, position, data, entries);
        constructTenPercentList(contest, entry, position, data, entries);

        data.put("position", athleteSportEventInfo.getAthlete().getPositions().get(0).getAbbreviation());

        return mapper.writeValueAsString(data);
    }

    private void constructTenPercentList(Contest contest, Entry entry, Position position, Map<String, Object> data, List<Entry> entries) {
        int tenPercent = entries.size() / 10;
        if (tenPercent == 0) {
            tenPercent = 1;
        }

        int entryIndex = entries.indexOf(entry);
        int startIndex = Math.max(entryIndex - tenPercent, 0);
        int endIndex = Math.min(entryIndex + tenPercent, entries.size() - 1);

        List<Entry> tenPercentEntries = entries.subList(startIndex, endIndex + 1);
        List<Map<String, Object>> tenPercentList = new ArrayList<>();
        if (!tenPercentEntries.isEmpty()) {
            Set<Lineup> lineupsWithinTenPercentOfMe = new HashSet<>();
            for (Entry e : tenPercentEntries) {
                lineupsWithinTenPercentOfMe.add(e.getLineup());
            }

            List<AthleteSportEventInfo> athleteSportEventInfoList = DaoFactory.getSportsDao().findAthleteSportEventInfos(contest, position, new ArrayList<>(lineupsWithinTenPercentOfMe));

            for (AthleteSportEventInfo asei : athleteSportEventInfoList) {
                Map<String, Object> allEntry = createPercentOwnedListEntry(contest, asei);
                tenPercentList.add(allEntry);
            }
        }
        data.put("tenPercent", tenPercentList);
    }

    private void constructAboveList(Contest contest, Entry entry, Position position, Map<String, Object> data, List<Entry> entries) {
        int entryIndex = entries.indexOf(entry);
        List<Entry> betterThanMe = entries;

        // Sanity check.  If we can't find the entry passed in, use all entries.
        if (entryIndex > -1) {
            betterThanMe = entries.subList(0, entryIndex);
        }
        List<Map<String, Object>> above = new ArrayList<>();
        if (!betterThanMe.isEmpty()) {
            Set<Lineup> lineupsBetterThanMe = new HashSet<>();
            for (Entry e : betterThanMe) {
                if (e.equals(entry)) {
                    continue;
                }
                lineupsBetterThanMe.add(e.getLineup());
            }

            List<AthleteSportEventInfo> athleteSportEventInfoList = DaoFactory.getSportsDao().findAthleteSportEventInfos(contest, position, new ArrayList<>(lineupsBetterThanMe));

            for (AthleteSportEventInfo asei : athleteSportEventInfoList) {
                Map<String, Object> allEntry = createPercentOwnedListEntry(contest, asei);
                above.add(allEntry);
            }
        }
        data.put("above", above);
    }

    private void constructAllList(Map<String, Object> data, Contest contest, Position position) {
        List<AthleteSportEventInfo> athleteSportEventInfoList = DaoFactory.getSportsDao().findAthleteSportEventInfos(contest, position, null);

        List<Map<String, Object>> all = new ArrayList<>();
        for (AthleteSportEventInfo asei : athleteSportEventInfoList) {
            Map<String, Object> allEntry = createPercentOwnedListEntry(contest, asei);
            all.add(allEntry);
        }

        data.put("all", all);
    }

    /**
     * Creates a single entry in the list for data to be sent back to the client.
     *
     * @param contest The contest of interest.
     * @param asei    The AthleteSportEventInfo of interest.
     * @return A map of fields necessary for rendering percent owned data.
     */
    private Map<String, Object> createPercentOwnedListEntry(Contest contest, AthleteSportEventInfo asei) {
        Map<String, Object> allEntry = new HashMap<>();
        allEntry.put("fpp", asei.getFantasyPoints());
        allEntry.put("firstName", asei.getAthlete().getFirstName());
        allEntry.put("lastName", asei.getAthlete().getLastName());
        allEntry.put("percentOwned", getPercentOwnedForAthlete(contest, asei));
        allEntry.put("athleteSportEventInfoId", asei.getId());
        return allEntry;
    }

    @CacheEvict(value = "percentOwnedAllData", key = "{#contestId, #athleteSportEventInfoId}")
    public void updatePercentOwned(String contestId, Integer athleteSportEventInfoId, Integer entryId) {
    }

    /**
     * Calculates the percent ownership for the specified athlete across the contest.
     *
     * @param contest               The contest of interest.
     * @param athleteSportEventInfo The athlete of interest.
     * @return A number (as BigDecimal) specifying the percentage ownership.
     */
    @Cacheable(value = "percentOwned")
    public BigDecimal getPercentOwnedForAthlete(Contest contest, AthleteSportEventInfo athleteSportEventInfo) {
        List<Entry> entryList = DaoFactory.getContestDao().findEntries(contest);
        if (entryList.size() == 0) {
            return BigDecimal.ZERO;
        }

        double numOwned = 0;
        for (Entry entry : entryList) {
            for (LineupSpot lineupSpot : entry.getLineup().getLineupSpots()) {
                if (lineupSpot.getAthleteSportEventInfo().getId() == athleteSportEventInfo.getId()) {
                    numOwned++;
                    break;
                }
            }
        }

        BigDecimal value = new BigDecimal(numOwned / entryList.size());
        value = value.setScale(2, RoundingMode.HALF_EVEN);

        return value;
    }

    @CacheEvict(value = "percentOwned", key = "{#contestId, #athleteSportEventInfoId}")
    public void updatePercentOwnedForAthlete(String contestId, Integer athleteSportEventInfoId, Integer entryId) {
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "percentOwned", allEntries = true),
            @CacheEvict(value = "percentOwnedAllData", allEntries = true)
    })
    public void flushAllCaches() {
    }
}
