package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.GlobalConstants;
import controllers.LineupValidationDuplicateAthleteException;
import controllers.LineupValidationException;
import dao.DaoFactory;
import dao.IContestDao;
import dao.ISportsDao;
import distributed.DistributedServices;
import models.contest.*;
import models.sports.AthleteSalary;
import models.user.User;
import stats.translator.IFantasyPointTranslator;
import utils.ITimeService;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by mwalsh on 6/11/14.
 */
public class LineupService extends AbstractCachingService {

    private ObjectMapper mapper = new ObjectMapper();
    private TypeReference<Map<String, Object>> contestTypeReference = new TypeReference<Map<String, Object>>() {
    };

    private ISportsDao sportsDao;

    private IContestDao contestDao;

    private IContestListService contestListManager;

    private ITimeService timeService;

    private static List<ContestState> contestStates = Arrays.asList(ContestState.active, ContestState.open, ContestState.locked, ContestState.rosterLocked);

    public LineupService(ISportsDao sportsDao, IContestDao contestDao, IContestListService contestListManager, ITimeService timeService) {
        this.sportsDao = sportsDao;
        this.contestDao = contestDao;
        this.contestListManager = contestListManager;
        this.timeService = timeService;
    }

    public static Map<String, Object> processLineupValidationException(LineupValidationException e) {
        Map<String, Object> result = new HashMap<>();
        if (e.getMessage().equals(GlobalConstants.MINIMUM_SPORT_EVENTS_ERROR)) {
            result.put("code", GlobalConstants.CONTEST_ENTRY_ERROR_NOT_ENOUGH_SPORT_EVENTS);
        } else if (e.getMessage().equals(GlobalConstants.SALARY_CAP_EXCEEDED_ERROR)) {
            result.put("code", GlobalConstants.CONTEST_ENTRY_ERROR_OVER_SALARY_CAP);
        } else if (e.getMessage().equals(GlobalConstants.LINEUP_SIZE_INVALID_ERROR)) {
            result.put("code", GlobalConstants.CONTEST_ENTRY_ERROR_INVALID_LINEUP_SIZE);
        } else if (e instanceof LineupValidationDuplicateAthleteException) {
            result.put("code", GlobalConstants.CONTEST_ENTRY_ERROR_DUPLICATE_ATHLETES);
        } else {
            result.put("code", GlobalConstants.CONTEST_ENTRY_ERROR_OTHER);
        }
        result.put("description", e.getMessage());

        return result;
    }

    /**
     * Retrieve lineups for a user that are being used in contests that are in open, locked, or active status.
     *
     * @param user The user whose lineups we need to fetch.
     * @return
     * @throws JsonProcessingException
     */
    public List<Lineup> getLiveLineups(User user) throws IOException {
        List<Lineup> lineups = contestDao.findLineups(user, contestStates, true);

        return lineups;
    }

    /**
     * Retrieve the live lineups as a JSON string.
     *
     * @param user              The user whose lineups we need to fetch.
     * @return                  A JSON string of data representing the lineups.
     * @throws IOException
     */
    public String getLiveLineupsAsJson(User user) throws IOException {
        if (user == null) {
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("error", "The user's session has expired");
            return mapper.writeValueAsString(errorData);
        }

        return generateLiveLineupJson(getLiveLineups(user));
    }

    /**
     * Generates a JSON string representing the lineups and associated athletes.
     *
     * @param lineups
     * @return
     * @throws IOException
     */
    public String generateLiveLineupJson(List<Lineup> lineups) throws IOException {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Lineup lineup : lineups) {
            Map<String, Object> data = new HashMap<>();
            data.put("lineupId", lineup.getId());
            data.put("lineupName", lineup.getName());
            data.put("startTime", lineup.getSportEventGrouping().getEventDate().getTime());

            /*
             * Calculate the total fantasy points and units remaining for all athletes to get a total
             * for the entire lineup.
             */
            BigDecimal fantasyPoints = BigDecimal.ZERO;
            int unitsRemaining = 0;
            for(LineupSpot lineupSpot: lineup.getLineupSpots()) {
                fantasyPoints = fantasyPoints.add(lineupSpot.getAthleteSportEventInfo().getFantasyPoints());
                unitsRemaining += lineupSpot.getAthleteSportEventInfo().getSportEvent().getUnitsRemaining();
            }
            data.put("fpp", fantasyPoints.setScale(2, RoundingMode.HALF_EVEN));
            data.put("unitsRemaining", unitsRemaining);


            List<Map<String, Object>> athleteList = new ArrayList<>();
            Contest currentContest = DaoFactory.getContestDao().findContests(lineup, contestStates).get(0);
            int remainingSalary = currentContest.getSalaryCap();
            for (LineupSpot lineupSpot : lineup.getLineupSpots()) {
                Map<String, Object> athleteData = new HashMap<>();
                athleteData.put("athleteId", lineupSpot.getAthlete().getStatProviderId());
                athleteData.put("athleteSportEventInfoId", lineupSpot.getAthleteSportEventInfo().getId());
                athleteData.put("firstName", lineupSpot.getAthlete().getFirstName());
                athleteData.put("lastName", lineupSpot.getAthlete().getLastName());
                athleteData.put("position", lineupSpot.getPosition().getAbbreviation());

                IFantasyPointTranslator translator = currentContest.getStatsFantasyPointTranslator(DistributedServices.getContext());
                if(currentContest.getContestState().getId() == ContestState.active.getId() || currentContest.getContestState().getId() == ContestState.complete.getId()) {
                    athleteData.put("fppg", lineupSpot.getAthleteSportEventInfo().getFantasyPoints().setScale(2, RoundingMode.HALF_EVEN));
                }
                else {
                    athleteData.put("fppg", sportsDao.calculateFantasyPointsPerGame(translator, timeService, lineupSpot.getAthleteSportEventInfo(), 17));
                }
                athleteData.put("timeRemaining", lineupSpot.getAthleteSportEventInfo().getSportEvent().getUnitsRemaining());

                AthleteSalary salary = DaoFactory.getSportsDao().findAthleteSalary(lineupSpot.getAthlete(), lineup.getSportEventGrouping());
                try {
                    athleteData.put("salary", salary.salary);
                    remainingSalary -= salary.salary;
                } catch (Exception e) {
                    e.printStackTrace();
                    athleteData.put("salary", -9999);
                }
                athleteList.add(athleteData);

            }
            data.put("remainingSalary", remainingSalary);
            data.put("athletes", athleteList);

            List<Map<String, Object>> contestDataList = new ArrayList<>();
            for (Contest contest : DaoFactory.getContestDao().findContests(lineup, contestStates)) {
                Map<String, Object> contestData = mapper.readValue(contestListManager.getContestAsJson(contest.getUrlId()), contestTypeReference);

                List<Entry> entries = DaoFactory.getContestDao().findEntries(lineup, contest);
                List<Integer> entryIds = new ArrayList<>();
                entryIds.addAll(entries.stream().map(Entry::getId).collect(Collectors.toList()));
                contestData.put("entries", entryIds);
                contestData.put("contestState", contest.getContestState().getName());

                if (contest.getContestType().equals(ContestType.H2H)) {
                    List<Entry> entryList = DaoFactory.getContestDao().findEntries(contest);
                    if (entryList.size() == 2) {
                        contestData.put("opp", entryList.get(0).getUser().equals(lineup.getUser()) ? entryList.get(1).getUser().getUserName() : entryList.get(0).getUser().getUserName());
                    } else {
                        contestData.put("opp", "H2H");
                    }
                }


                contestDataList.add(contestData);
            }
            data.put("contests", contestDataList);

            result.add(data);
        }

        return mapper.writeValueAsString(result);
    }

    /**
     * Retrieves lineups based on their compatibility with the provided contest (determined by SportEventGrouping)
     * and returns the JSON representation of the relevant data.
     *
     * @param user    The user asking for their compatible lineups.
     * @param contest The contest the user wants compatible lineups for.
     * @return A JSON representation of the compatible lineups (relevant data only).
     * @throws JsonProcessingException When Jackson breaks...
     */
    public String getLineupsByContest(User user, Contest contest) throws JsonProcessingException {
        List<Map<String, Object>> data = new ArrayList<>();
        List<Lineup> lineups = DaoFactory.getContestDao().findLineups(user, contest.getSportEventGrouping());

        for (Lineup lineup : lineups) {
            Map<String, Object> lineupData = new HashMap<>();
            lineupData.put("lineupId", lineup.getId());
            lineupData.put("lineupName", lineup.getName());

            List<Map<String, Object>> athleteList = new ArrayList<>();
            for (LineupSpot lineupSpot : lineup.getLineupSpots()) {
                Map<String, Object> athleteData = new HashMap<>();
                athleteData.put("athleteId", lineupSpot.getAthlete().getStatProviderId());
                athleteData.put("athleteSportEventInfoId", lineupSpot.getAthleteSportEventInfo().getId());
                athleteData.put("firstName", lineupSpot.getAthlete().getFirstName());
                athleteData.put("lastName", lineupSpot.getAthlete().getLastName());
                athleteData.put("position", lineupSpot.getPosition().getAbbreviation());

                AthleteSalary salary = DaoFactory.getSportsDao().findAthleteSalary(lineupSpot.getAthlete(), lineup.getSportEventGrouping());
                athleteData.put("salary", salary.salary);
                athleteList.add(athleteData);
            }

            lineupData.put("athletes", athleteList);

            // How many times has this lineup been entered in the contest?
            lineupData.put("numEntries", contestDao.findEntries(lineup, contest).size());

            data.add(lineupData);
        }

        data.sort((lhs, rhs) -> (-1 * ((Integer) lhs.get("numEntries")).compareTo((Integer) rhs.get("numEntries"))));

        return mapper.writeValueAsString(data);
    }

    /**
     * Executes removal of lineups from a contest.
     *
     * @param user    The user whose lineup entries we need to remove.
     * @param lineup  The lineup to look up entries with.
     * @param contest The contest to remove the entries from.
     * @return A string representing a map of data indicating success or failure and relevant details.
     * @throws JsonProcessingException
     */
    public String removeLineupFromContest(User user, Lineup lineup, Contest contest) throws JsonProcessingException {
        List<Entry> entries = contestDao.findEntries(lineup, contest);
        int status = contestDao.removeFromContest(user, contest, lineup, entries.size());

        Map<String, Object> result = new HashMap<>();
        result.put("contestId", contest.getUrlId());
        result.put("lineupId", lineup.getId());
        result.put("code", status);

        if (status == GlobalConstants.CONTEST_ENTRY_ERROR_NOT_OPEN) {
            result.put("description", "The specified contest is not open.");
        } else if (status == GlobalConstants.CONTEST_ENTRY_ERROR_OTHER) {
            result.put("description", "An error occurred while attempting to remove your lineup entries from the contest.");
        }

        return mapper.writeValueAsString(result);
    }

    /**
     * Update the lineup for the entries provided.  If the entries are a subset of the total entries for
     * this lineup, then create a new lineup for those.
     *
     * @param user        The user whose lineup we need to edit.
     * @param lineup      The lineup to edit.
     * @param lineupSpots The LineupSpots to edit the lineup with.
     * @param entries     The entries that the changed lineup will be applied to.
     * @return A string representing a map of data indicating success or failure and relevant details.
     * @throws JsonProcessingException
     */
    public String updateLineup(User user, Lineup lineup, List<LineupSpot> lineupSpots, List<Entry> entries) throws JsonProcessingException {
        Map<String, Object> result = new HashMap<>();
        List<ContestSalary> salaries = contestDao.findContestSalarys(lineup.getLeague());
        List<Entry> allEntriesForLineup = contestDao.findEntries(lineup);

        /*
         * Make sure the entries we are trying to edit lineups for are in a compatible state - open, locked.
         */
        if (!entries.isEmpty()) {
            ContestState contestState = entries.get(0).getContest().getContestState();
            if (!(contestState.equals(ContestState.open) || contestState.equals(ContestState.locked) || contestState.equals(ContestState.rosterLocked))) {
                result.put("code", GlobalConstants.CONTEST_ENTRY_ERROR_CONTEST_STARTED);
                result.put("description", "The lineup you are trying to edit has entries in contests that have already started.");

                return mapper.writeValueAsString(result);
            }
        }

        // Wholesale replace
        if (allEntriesForLineup.size() == entries.size()) {
            try {
                contestDao.validateLineup(lineup, salaries.get(0).getSalary(), lineupSpots);

                // Clear existing lineup spots
                contestDao.removeLineupSpots(lineup);

                lineup.setLineupSpots(lineupSpots);
                contestDao.saveLineup(lineup);

                result.put("code", GlobalConstants.CONTEST_ENTRY_SUCCESS);
            } catch (LineupValidationException e) {
                result = processLineupValidationException(e);
            }
        } else {
            try {
                Lineup newLineup = new Lineup("LINEUP-" + LocalDate.now().toString(), user, lineup.getLeague(), lineup.getSportEventGrouping());
                contestDao.validateLineup(newLineup, salaries.get(0).getSalary(), lineupSpots);
                newLineup.setLineupSpots(lineupSpots);
                contestDao.saveLineup(newLineup);

                for (Entry entry : entries) {
                    entry.setLineup(newLineup);
                    contestDao.saveEntry(entry);
                }
                result.put("code", GlobalConstants.CONTEST_ENTRY_SUCCESS);
            } catch (LineupValidationException e) {
                result = processLineupValidationException(e);
            }
        }

        return mapper.writeValueAsString(result);
    }

    @Override
    public void flushAllCaches() {
    }
}
