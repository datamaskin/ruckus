package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.DaoFactory;
import models.contest.Contest;
import models.contest.ContestState;
import models.contest.Lineup;
import models.contest.LineupSpot;
import models.sports.AthleteSportEventInfo;
import models.sports.Team;
import models.user.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Manager for the data queries in the ContestLiveAthlete RESTful and socket endpoints.
 */
public class ContestLiveAthleteService extends AbstractCachingService {

    private ObjectMapper mapper = new ObjectMapper();
    private TypeReference<Map<String, Object>> mapTypeReference = new TypeReference<Map<String, Object>>() {
    };
    private TypeReference<List<Map<String, Object>>> listTypeReference = new TypeReference<List<Map<String, Object>>>() {
    };

    Comparator<Map<String, Object>> comparator = new Comparator<Map<String, Object>>() {
        @Override
        public int compare(Map<String, Object> o1, Map<String, Object> o2) {
            Map<String, Object> exposureData1 = (Map<String, Object>) o1.get("exposure");
            Map<String, Object> exposureData2 = (Map<String, Object>) o2.get("exposure");

            // if neither have exposure data, just say they're equal
            if(exposureData1 == null && exposureData2 == null) {
                return 0;
            }

            // if only one has exposure data, the other takes precedence.
            if(exposureData1 != null && exposureData2 == null) {
                return -1;
            }
            else if(exposureData1 == null && exposureData2 != null) {
                return 1;
            }
            else {
                // Both have exposure!  Now check to make sure the total exposure field isn't null (shouldn't be), and
                // figure out which one is larger.
                Integer totalExposure1 = (Integer) exposureData1.get("totalExposure");
                Integer totalExposure2 = (Integer) exposureData2.get("totalExposure");

                if(totalExposure1 == null && totalExposure2 == null) {
                    return 0;
                }
                else if(totalExposure1 == null) {
                    return 1;
                }
                else if(totalExposure2 == null) {
                    return -1;
                }
                else {
                    return totalExposure1 > totalExposure2 ? -1 : (totalExposure2 > totalExposure1) ? 1 : 0;
                }
            }
        }
    };

    private AthleteExposureService athleteExposureManager;
    private AthleteContestRankService athleteContestRankManager;

    /**
     * Retrieves AthleteSportEventInfo data for all athletes used by a user in a given contest.
     *
     * @param user      The user whose lineups we're interested in.
     * @param contestId The id of the contest of interest.
     * @return A JSON string of AthleteSportEventInfo data.
     * @throws IOException When Jackson processing goes wrong.
     */
    @Cacheable(value = "contestLiveAthletesForContest", key = "{#user.id, #contestId}")
    public String getAthletesForContestAsJson(User user, String contestId) throws IOException {
        Contest contest = DaoFactory.getContestDao().findContest(contestId);
        List<Lineup> lineupList = DaoFactory.getContestDao().findLineups(user, contest);

        return mapper.writeValueAsString(getJsonFromLineupList(user, lineupList));
    }

    /**
     * Retrieves AthleteSportEventInfo data for all athletes used by a user across all their contests.
     *
     * @param user The user whose lineups we're interested in.
     * @return A JSON string of AthleteSportEventInfo data.
     * @throws IOException When Jackson processing goes wrong.
     */
    @Cacheable(value = "contestLiveAthletesForUser", key = "#user.id")
    public String getAthletesForUserAsJson(User user) throws IOException {
        List<Lineup> lineups = DaoFactory.getContestDao().findLineups(user, Arrays.asList(ContestState.active, ContestState.complete));

        return mapper.writeValueAsString(getJsonFromLineupList(user, lineups));
    }

    /**
     * Retrieves AthleteSportEventInfo data for a single athlete/sport event combo.
     *
     * @param athleteSportEventInfoId The id of the AthleteSportEventInfo object we want to create JSON with.
     * @return A JSON string of AthleteSportEventInfo data.
     * @throws JsonProcessingException When Jackson processing goes wrong.
     */
    @Cacheable(value = "contestLiveAthletesForAthlete", key = "{#user.id, #contestId, #athleteSportEventInfoId, #includeExposureAndRanks}")
    public String getAthleteForContestAsJson(User user, String contestId, Integer athleteSportEventInfoId, boolean includeExposureAndRanks) throws IOException {
        AthleteSportEventInfo athleteSportEventInfo = DaoFactory.getSportsDao().findAthleteSportEventInfo(athleteSportEventInfoId);

        return createJsonForAthleteSportEventInfo(user, athleteSportEventInfo, includeExposureAndRanks);
    }

    /**
     * Convert a list of Lineup objects to the JSON we want to support this endpoint.
     *
     * @param user The user making the request.
     * @param lineups The list of Lineup objects for processing.
     * @return A JSON string of AthleteSportEventInfo data.
     * @throws IOException When Jackson processing goes wrong.
     */
    private List<Map<String, Object>> getJsonFromLineupList(User user, List<Lineup> lineups) throws IOException {
        Set<Integer> aseiIds = new HashSet<>();
        List<Map<String, Object>> finalList = new ArrayList<>();
        for(Lineup lineup: lineups) {
            for(LineupSpot lineupSpot: lineup.getLineupSpots()) {
                if(aseiIds.contains(lineupSpot.getAthleteSportEventInfo().getId())) {
                    continue;
                }

                String aseiData = createJsonForAthleteSportEventInfo(user, lineupSpot.getAthleteSportEventInfo(), true);
                Map<String, Object> aseiDataMap = mapper.readValue(aseiData, mapTypeReference);
                finalList.add(aseiDataMap);
                aseiIds.add(lineupSpot.getAthleteSportEventInfo().getId());
            }
        }

        Collections.sort(finalList, comparator);

        return finalList;
    }

    public String createJsonForAthleteSportEventInfo(User user, AthleteSportEventInfo athleteSportEventInfo, boolean includeExposureAndRanks) throws IOException {
        TypeReference<List<Map<String, Object>>> typeReference = new TypeReference<List<Map<String, Object>>>() {
        };

        Map<String, Object> data = new HashMap<>();
        data.put("athleteSportEventInfoId", athleteSportEventInfo.getId());
        data.put("fpp", new BigDecimal(athleteSportEventInfo.getFantasyPoints().doubleValue()).setScale(2, RoundingMode.HALF_EVEN));
        data.put("stats", mapper.readValue(athleteSportEventInfo.getStats(), typeReference));
        data.put("timeline", mapper.readValue(athleteSportEventInfo.getTimeline(), typeReference));
        data.put("position", athleteSportEventInfo.getAthlete().getPositions().get(0).getAbbreviation());
        data.put("firstName", athleteSportEventInfo.getAthlete().getFirstName());
        data.put("lastName", athleteSportEventInfo.getAthlete().getLastName());
        data.put("uniform", athleteSportEventInfo.getAthlete().getUniform());
        data.put("indicator", athleteSportEventInfo.getIndicator());
        data.put("unitsRemaining", athleteSportEventInfo.getSportEvent().getUnitsRemaining());
        data.put("athleteId", athleteSportEventInfo.getAthlete().getStatProviderId());
        data.put("image", "https://dm63aeeijtc75.cloudfront.net/" + athleteSportEventInfo.getSportEvent().getLeague().getAbbreviation().toLowerCase() +
                "/" + athleteSportEventInfo.getAthlete().getStatProviderId());

        if (includeExposureAndRanks) {
            Map<String, Object> exposureData = mapper.readValue(athleteExposureManager.getAthleteExposure(user, String.valueOf(athleteSportEventInfo.getId())), mapTypeReference);
            List<Map<String, Object>> ranksData = mapper.readValue(athleteContestRankManager.getAthleteContestRanks(user, athleteSportEventInfo.getId()), listTypeReference);
            data.put("exposure", exposureData);
            data.put("ranks", ranksData);
        }

        List<Team> teams = athleteSportEventInfo.getSportEvent().getTeams();

        /*
         * Set up matchup data.  This should include the following items:
         * - homeId
         * - homeTeam
         * - homeScore
         * - awayId
         * - awayTeam
         * - awayScore
         */
        Map<String, Object> shortDescMap = mapper.readValue(athleteSportEventInfo.getSportEvent().getShortDescription(), mapTypeReference);
        shortDescMap.put("sportEventId", athleteSportEventInfo.getSportEvent().getId());
        shortDescMap.put("athleteTeamId", athleteSportEventInfo.getAthlete().getTeam().getStatProviderId());
        data.put("matchup", shortDescMap);

        return mapper.writeValueAsString(data);
    }

    @CacheEvict(value = "contestLiveAthletesForContest", key = "{#user.id, #contestId}")
    public void updateContestLiveAthletesForContest(User user, String contestId) {
        //TODO: need to update a topic somewhere. This call will invalidate the cache
    }

    @CacheEvict(value = "contestLiveAthletesForUser", key = "#user.id")
    public void updateContestLiveAthletesForContest(User user) {
        //TODO: need to update a topic somewhere. This call will invalidate the cache
    }

    @CacheEvict(value = "contestLiveAthletesForAthlete", key = "{#user.id, #contestId, #athleteSportEventInfoId, #includeExposureAndRanks}")
    public void updateContestLiveAthletesForContest(User user, String contestId, Integer athleteSportEventInfoId, boolean includeExposureAndRank) {
        //TODO: need to update a topic somewhere. This call will invalidate the cache
    }

    public void setAthleteExposureManager(AthleteExposureService athleteExposureManager) {
        this.athleteExposureManager = athleteExposureManager;
    }

    public void setAthleteContestRankManager(AthleteContestRankService athleteContestRankManager) {
        this.athleteContestRankManager = athleteContestRankManager;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "contestLiveAthletesForContest", allEntries = true),
            @CacheEvict(value = "contestLiveAthletesForUser", allEntries = true),
            @CacheEvict(value = "contestLiveAthletesForAthlete", allEntries = true)})
    public void flushAllCaches() {}
}
