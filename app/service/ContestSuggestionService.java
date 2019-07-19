package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.IContestDao;
import models.contest.Contest;
import models.contest.ContestType;
import models.contest.Entry;
import models.contest.Lineup;
import models.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Interface to clients responsible for creating JSON representation of suggested contests based on the provided contest.
 */
public class ContestSuggestionService /*extends AbstractCachingManager*/ {

    ObjectMapper mapper = new ObjectMapper();
    IContestDao contestDao;

    public ContestSuggestionService(IContestDao contestDao) {
        this.contestDao = contestDao;
    }

    /**
     * Get the JSON representation of suggested contests.
     *
     * @param contestId The id of the contest that the user just attempted to enter.
     * @param lineupId  The id of the lineup we'd like to submit to additional contests.
     * @param status    The status code of a previous lineup submission attempt.
     * @return The JSON representation of suggested contests.
     * @throws JsonProcessingException
     */
//    @Cacheable(value = "contestSuggestions")
    public String getContestSuggestions(User user, String contestId, Integer lineupId, Integer status) throws JsonProcessingException {
        Contest contest = contestDao.findContest(contestId);
        Lineup lineup = lineupId == -1 ? null : contestDao.findLineup(lineupId);

        Map<String, Object> suggestedContests = contest.getSuggestedContests(user, status, lineup);

        Map<String, Object> result = new HashMap<>();

        if (suggestedContests.containsKey("duplicateContest")) {
            Contest duplicateContest = (Contest) suggestedContests.get("duplicateContest");
            Map<String, Object> duplicateContestMap = new HashMap<>();
            duplicateContestMap.put("contestId", duplicateContest.getUrlId());
            Map<String, String> contestTypeMap = new HashMap<>();
            contestTypeMap.put("name", duplicateContest.getContestType().getName());
            contestTypeMap.put("abbr", duplicateContest.getContestType().getAbbr());
            duplicateContestMap.put("contestType", contestTypeMap);
            duplicateContestMap.put("capacity", duplicateContest.getCapacity());
            duplicateContestMap.put("currentEntries", duplicateContest.getCurrentEntries());
            duplicateContestMap.put("entryFee", duplicateContest.getEntryFee());
            duplicateContestMap.put("prizePool", duplicateContest.calculatePrizePool());
            duplicateContestMap.put("league", duplicateContest.getLeague().getAbbreviation());
            duplicateContestMap.put("remainingAllowedEntries", duplicateContest.calculateRemainingAllowedEntries(user));

            result.put("duplicateContest", duplicateContestMap);
        }

        List<Map<String, Object>> data = new ArrayList<>();
        for (Contest suggestedContest : (List<Contest>) suggestedContests.get("additionalContests")) {
            Map<String, Object> contestData = new HashMap<>();
            contestData.put("contestId", suggestedContest.getUrlId());
            Map<String, String> contestTypeMap = new HashMap<>();
            contestTypeMap.put("name", suggestedContest.getContestType().getName());
            contestTypeMap.put("abbr", suggestedContest.getContestType().getAbbr());
            contestData.put("contestType", contestTypeMap);
            contestData.put("displayName", contest.getDisplayName());
            contestData.put("capacity", suggestedContest.getCapacity());
            contestData.put("currentEntries", suggestedContest.getCurrentEntries());
            contestData.put("entryFee", suggestedContest.getEntryFee());
            contestData.put("prizePool", suggestedContest.calculatePrizePool());
            contestData.put("league", suggestedContest.getLeague().getAbbreviation());
            contestData.put("remainingAllowedEntries", suggestedContest.calculateRemainingAllowedEntries(user));
            if (suggestedContest.getContestType().equals(ContestType.H2H)) {
                List<Entry> entries = contestDao.findEntries(contest);
                if (entries.size() == 1) {
                    contestData.put("opp", entries.get(0).getUser().getUserName());
                }
            }

            data.add(contestData);
        }
        result.put("additionalContests", data);

        return mapper.writeValueAsString(result);
    }

//    @CacheEvict(value = "contestSuggestions")
//    public void removeContestSuggestions(String contestId, boolean success) {
//    }

//    @Override
//    @CacheEvict(value = "contestSuggestions", allEntries = true)
//    public void flushAllCaches() {}
}
