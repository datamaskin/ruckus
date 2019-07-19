package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.DaoFactory;
import models.contest.*;
import models.sports.AthleteSportEventInfo;
import models.sports.League;
import models.user.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import play.Logger;

import java.io.IOException;
import java.util.*;

/**
 * Determines the exposure to a certain player that a user has.
 */
public class AthleteExposureService extends AbstractCachingService {

    private ObjectMapper mapper = new ObjectMapper();

    /**
     * Calculates the exposure the user has to all athletes that have been rostered in contests
     * that are currently in a locked or active state.
     *
     * @param user          The user we're calculating exposure for.
     * @return              A JSON string representing an array of objects containing athleteId and exposure attributes.
     * @throws IOException
     */
    @Cacheable(value = "allAthleteExposure", key = "#user.id")
    public String getAthleteExposure(User user) throws IOException {
        Map<Integer, Integer> exposure = DaoFactory.getSportsDao().calculateExposure(user);

        List<Map<String, Object>> results = new ArrayList<>();
        for(Map.Entry<Integer, Integer> entry: exposure.entrySet()) {
            Map<String, Object> resultEntry = new HashMap<>();
            resultEntry.put("athleteId", entry.getKey());
            resultEntry.put("exposure", entry.getValue());

            results.add(resultEntry);
        }

        return mapper.writeValueAsString(results);
    }

    @Cacheable(value = "athleteExposure", key = "{#user.id, #athleteSportEventInfoId}")
    public String getAthleteExposure(User user, String athleteSportEventInfoId) throws IOException {
        AthleteSportEventInfo athleteSportEventInfo = null;

        try {
            athleteSportEventInfo = DaoFactory.getSportsDao().findAthleteSportEventInfo(Integer.parseInt(athleteSportEventInfoId));
        } catch (NumberFormatException e) {
            Logger.error("Unable to parse id for AthleteSportEventInfo.");
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("error", "Unable to parse id for AthleteSportEventInfo.");
            return mapper.writeValueAsString(errorData);
        }

        League league = athleteSportEventInfo.getSportEvent().getLeague();

        List<ContestState> contestStates = new ArrayList<>();
//        contestStates.add(ContestState.open);
        contestStates.add(ContestState.rosterLocked);
        contestStates.add(ContestState.locked);
        contestStates.add(ContestState.active);
        List<Lineup> lineups = DaoFactory.getContestDao().findLineups(user, contestStates, true);

        /*
         * Determine all the entry fees paid.
         */
        int totalEntryFees = 0;
        for (Entry entry : DaoFactory.getContestDao().findEntries(user, contestStates)) {
            if(entry.getContest().getLeague().equals(league)) {
                totalEntryFees += entry.getContest().getEntryFee();
            }
        }

        int contestsEnteredWithAthleteCount = 0;
        int totalContestsEntered = 0;
        List<Contest> contests = DaoFactory.getContestDao().findContests(user, contestStates);
        for(Contest contest: contests) {
            if(contest.getLeague().equals(league)) {
                totalContestsEntered++;
            }
        }

        int totalExposure = 0;
        Map<ContestType, Set<Contest>> contestsEnteredWithAthlete = new HashMap<>();
        Map<ContestType, Integer> contestTypeExposure = new HashMap<>();
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
                if(!contestStates.contains(contest.getContestState())) {
                    continue;
                }

                Set<Contest> contestList = contestsEnteredWithAthlete.get(contest.getContestType());
                if (contestList == null) {
                    contestList = new HashSet<>();
                    contestsEnteredWithAthlete.put(contest.getContestType(), contestList);
                }
                contestList.add(contest);

                totalExposure += contest.getEntryFee();

                // Determine exposure by contest type
                Integer contestTypeExposureVal = contestTypeExposure.get(contest.getContestType());
                if (contestTypeExposureVal == null) {
                    contestTypeExposure.put(contest.getContestType(), contest.getEntryFee());
                } else {
                    contestTypeExposure.put(contest.getContestType(), contestTypeExposureVal + contest.getEntryFee());
                }
            }
        }

        /*
         * Determine how many of each contest type has been entered.
         */
        for(Map.Entry<ContestType, Set<Contest>> entry: contestsEnteredWithAthlete.entrySet()) {
            contestsEnteredWithAthleteCount += entry.getValue().size();
        }

        Map<String, Object> data = new HashMap<>();
        data.put("contestsEntered", contestsEnteredWithAthleteCount);
        data.put("totalContests", totalContestsEntered);
        data.put("totalEntryFees", totalEntryFees);
        data.put("totalExposure", totalExposure);

        List<Map<String, Object>> contestTypes = new ArrayList<>();
        for (Map.Entry<ContestType, Integer> entry : contestTypeExposure.entrySet()) {
            Map<String, Object> contestTypeData = new HashMap<>();

            contestTypeData.put("type", entry.getKey().getName());
            contestTypeData.put("abbr", entry.getKey().getAbbr());
            contestTypeData.put("entryFees", entry.getValue());
            contestTypeData.put("numEntered", contestsEnteredWithAthlete.get(entry.getKey()).size());
            contestTypes.add(contestTypeData);
        }

        data.put("contestTypes", contestTypes);

        return mapper.writeValueAsString(data);
    }

    @CacheEvict(value = "allAthleteExposure", key = "#user.id")
    public void updateAthleteExposure(User user) {
        Logger.info("Evicting athleteExposure cache for user " + user.getUserName());
    }

    @CacheEvict(value = "athleteExposure", key = "{#user.id, #athleteSportEventInfoId}")
    public void updateAthleteExposure(User user, String athleteSportEventInfoId) {
        Logger.info("Evicting athleteExposure cache for user " + user.getUserName() + " and AthleteSportEventInfo " + athleteSportEventInfoId);
    }

    @Override
    @CacheEvict(value = "athleteExposure", allEntries = true)
    public void flushAllCaches() {}
}
