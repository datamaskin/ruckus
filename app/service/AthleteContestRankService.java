package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.DaoFactory;
import models.contest.*;
import models.sports.AthleteSportEventInfo;
import models.user.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Breaks down each contest that the user has a roster in that contains the athlete.
 */
public class AthleteContestRankService extends AbstractCachingService {
    private ObjectMapper mapper = new ObjectMapper();

    /**
     * Determines the highest rank of any lineup in each contest that the athlete is in.
     *
     * @param user                    The logged-in user whose lineups we're evaluating.
     * @param athleteSportEventInfoId The AthleteSportEventInfo representing the athlete we're focusing on.
     * @return A JSON string representing the ranks for each contest.
     * @throws IOException
     */
    @Cacheable(value = "athleteContestRanks")
    public String getAthleteContestRanks(User user, Integer athleteSportEventInfoId) throws IOException {
        if (athleteSportEventInfoId == null) {
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("error", "Unable to parse id for AthleteSportEventInfo.");
            return mapper.writeValueAsString(errorData);
        }

        AthleteSportEventInfo athleteSportEventInfo = DaoFactory.getSportsDao().findAthleteSportEventInfo(athleteSportEventInfoId);

        List<ContestState> contestStates = new ArrayList<>();
        contestStates.add(ContestState.open);
        contestStates.add(ContestState.locked);
        contestStates.add(ContestState.rosterLocked);
        contestStates.add(ContestState.active);
        List<Lineup> lineups = DaoFactory.getContestDao().findLineups(user, contestStates);

        Map<Contest, Integer> ranks = new HashMap<>();
        Map<Contest, String> h2hOpponents = new HashMap<>();
        for (Lineup lineup : lineups) {
            boolean foundAthlete = false;
            for (LineupSpot lineupSpot : lineup.getLineupSpots()) {
                if (lineupSpot.getAthleteSportEventInfo().equals(athleteSportEventInfo)) {
                    foundAthlete = true;
                    break;
                }
            }
            if (!foundAthlete) {
                continue;
            }

            for (Entry entry : lineup.getEntries()) {
                Contest contest = entry.getContest();
                List<Entry> entriesForContest = DaoFactory.getContestDao().findEntries(contest);

                int rank = entriesForContest.indexOf(entry) + 1;

                if (!ranks.containsKey(contest)) {
                    ranks.put(contest, rank);
                } else if (ranks.get(contest) > rank) {
                    ranks.put(contest, rank);
                }

                if (contest.getContestType().equals(ContestType.H2H)) {
                    List<Entry> entries = DaoFactory.getContestDao().findEntries(contest);
                    if (entries.size() > 1) {
                        Entry e = entries.get(0).getUser().getUserName().equals(user.getUserName()) ? entries.get(1) : entries.get(0);
                        h2hOpponents.put(contest, e.getUser().getUserName());
                    }
                }
            }
        }

        List<Map<String, Object>> data = new ArrayList<>();
        for (Map.Entry<Contest, Integer> entry : ranks.entrySet()) {
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("athleteSportEventInfoId", athleteSportEventInfoId);
            dataMap.put("league", entry.getKey().getLeague().getAbbreviation());
            dataMap.put("entryFee", entry.getKey().getEntryFee());
            dataMap.put("currentEntries", entry.getKey().getCurrentEntries());
            dataMap.put("capacity", entry.getKey().getCapacity());
            dataMap.put("rank", entry.getValue());
            dataMap.put("contestId", entry.getKey().getUrlId());

            Map<String, String> contestType = new HashMap<>();
            contestType.put("name", entry.getKey().getContestType().getName());
            contestType.put("abbr", entry.getKey().getContestType().getAbbr());
            dataMap.put("contestType", contestType);

            if (entry.getKey().getContestType().equals(ContestType.H2H)) {
                String opponent = h2hOpponents.get(entry.getKey());
                dataMap.put("opp", opponent);
            }

            data.add(dataMap);
        }

        return mapper.writeValueAsString(data);
    }

    @CacheEvict(value = "athleteContestRanks")
    public void updateAthleteContestRanks(User user, Integer athleteSportEventInfoId) {

    }

    @Override
    @CacheEvict(value = "athleteContestRanks", allEntries = true)
    public void flushAllCaches() {
    }
}
