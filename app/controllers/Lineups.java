package controllers;

import service.LineupService;
import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.GlobalConstants;
import dao.DaoFactory;
import dao.IContestDao;
import distributed.DistributedServices;
import models.contest.*;
import models.sports.Athlete;
import models.sports.AthleteSportEventInfo;
import models.sports.Position;
import models.user.User;
import org.json.JSONException;
import play.Logger;
import play.mvc.Result;
import securesocial.core.java.SecuredAction;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mwalsh on 6/6/14.
 */
public class Lineups extends AbstractController {

    private static final IContestDao contestDao = DaoFactory.getContestDao();

    private static ObjectMapper mapper = new ObjectMapper();
    private static TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>() {};

    private static final LineupForm getLineupForm() {
        try {
            String values = request().body().asFormUrlEncoded().get("data")[0];
            return new ObjectMapper().readValue(values, LineupForm.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error creating LineupForm.", e);
        }
    }

    @SecuredAction
    public static Result getLiveLineups() {
        User user = getCurrentUser();
        LineupService lineupManager = DistributedServices.getContext().getBean("LineupManager", LineupService.class);

        try {
            return ok(lineupManager.getLiveLineupsAsJson(user));
        } catch (IOException e) {
            return ok("{\"error\":\"An error occurred while parsing the lineup data.\"}");
        }
    }

    /**
     * Return a JSON encoding of the lineups and athletes that are compatible with the provided contest.
     *
     * @param contestId The URL id of the contest we want compatible lineups for.
     * @return A JSON encoding of compatible lineups.
     */
    @SecuredAction
    public static Result getQuickLineups(String contestId) {
        Contest contest = DaoFactory.getContestDao().findContest(contestId);
        if (contest == null) {
            Map<String, String> errorData = new HashMap<>();
            errorData.put("error", "The provided contest id " + contestId + " is invalid.");
            try {
                return ok(mapper.writeValueAsBytes(errorData));
            } catch (JsonProcessingException e) {
                return ok("{\"error\":\"An error occurred while parsing the lineup data.\"}");
            }
        }

        LineupService lineupManager = DistributedServices.getContext().getBean("LineupManager", LineupService.class);
        try {
            User user = getCurrentUser();
            String result = lineupManager.getLineupsByContest(user, contest);
            return ok(result);
        } catch (JsonProcessingException e) {
            return ok("{\"error\":\"An error occurred while parsing the lineup data.\"}");
        }
    }

//    public static Result create() {
//        LineupForm lineupForm = getLineupForm();
//
//        League league = Ebean.find(League.class, lineupForm.leagueId);
//
//        if (league == null) {
//            return jerr("You need to provide a league for the lineup");
//        }
//
//        if (lineupForm.lineupName == null) {
//            lineupForm.lineupName = "LINEUP-" + LocalDate.now().toString();
//        }
//
//        try {
//            List<LineupSpot> lineupSpots = mapLineupSpots(lineupForm);
//            Contest contest = DaoFactory.getContestDao().findContest(Integer.parseInt(lineupForm.contestId));
//            Lineup lineup = new Lineup(lineupForm.lineupName, getCurrentUser(), league, contest.getSportEventGrouping());
//            DaoFactory.getContestDao().validateLineup(lineup, contest.getSalaryCap(), lineupSpots);
//            lineup.setLineupSpots(lineupSpots);
//            DaoFactory.getContestDao().saveLineup(lineup);
//            return jok("success!");
//        } catch (LineupValidationException e) {
//            return jerr("LINEUP IS NOT VALID." + e.getMessage());
//        }
//
//    }

    @SecuredAction
    public static Result update() {
        User user = getCurrentUser();
        LineupService lineupManager = DistributedServices.getContext().getBean("LineupManager", LineupService.class);
        LineupForm lineupForm = getLineupForm();
        Map<String, Object> resultMap = new HashMap<>();


        /*
         * Create the lineup.
         */
        Lineup lineup = null;
        try {
            Integer lineupId = Integer.parseInt(lineupForm.lineupId);
            lineup = contestDao.findLineup(lineupId);
        }
        catch(NumberFormatException e) {
            Logger.error("Unable to create lineup from lineupId - " + e.getMessage());
        }

        if(lineup == null) {
            resultMap.put("code", GlobalConstants.CONTEST_ENTRY_ERROR_INVALID_ID);
            resultMap.put("description", "The provided lineup id " + lineupForm.lineupId + " is invalid.");
            return jerr(resultMap);
        }

        /*
         * Get lineup spots
         */
        List<LineupSpot> lineupSpots = null;
        try {
            lineupSpots = mapLineupSpots(lineupForm);
        }
        catch(LineupValidationException e) {
            return jerr(LineupService.processLineupValidationException(e));
        }

        /*
         * Get entries
         */
        List<Entry> entries = null;
        try {
            entries = contestDao.findEntries(lineupForm.entryIds);
        }
        catch(Exception e) {
            Logger.error("Unable to retrieve entries for provided ids - " + e.getMessage());
        }

        if(entries == null) {
            resultMap.put("code", GlobalConstants.CONTEST_ENTRY_ERROR_OTHER);
            resultMap.put("description", "Unable to retrieve entries for provided ids.");
            return jerr(resultMap);
        }

        try {
            String result = lineupManager.updateLineup(user, lineup, lineupSpots, entries);
            resultMap = mapper.readValue(result, typeReference);
        } catch (IOException e) {
            resultMap.put("code", GlobalConstants.CONTEST_ENTRY_ERROR_OTHER);
            resultMap.put("description", "Unable to process lineup update.");
            return jerr(resultMap);
        }

        return jok(resultMap);
    }

    /**
     * Enter a contest with a pre-existing lineup.
     *
     * @return A JSON response indicating the result of the action.
     * @throws JsonProcessingException
     * @throws JSONException
     */
    @SecuredAction
    public static Result quickEnter() {
        User user = getCurrentUser();
        if (user == null) {
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("code", GlobalConstants.CONTEST_ENTRY_ERROR_SESSION_EXPIRED);
            errorData.put("description", "The user's session has expired");
            return jerr(errorData);
        }

        LineupService lineupManager = DistributedServices.getContext().getBean("LineupManager", LineupService.class);

        /*
         * Parse the POST body for JSON.  It should look something like this:
         *
         * {
         *      lineupId : 5,
         *      entries : [
         *          {
         *              contestId : ‘fasfdsds’,
         *              multiple : 5
         *          },
         *          {
         *              contestId : ‘gdfgfd’,
         *              multiple : 2
         *          }
         *      ]
         *  }
         *
         *  SINGLE ENTRY REPLACE
         *  {
         *      lineupId : 5,
         *      entries : [
         *          {
         *              contestId : ‘fasfdsds’,
         *              multiple : 1,
         *              replace: true
         *          }
         *      ]
         *  }
         */
        String values = request().body().asFormUrlEncoded().get("data")[0];
        Map<String, Object> lineupData = null;
        try {
            lineupData = mapper.readValue(values, typeReference);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int lineupId = (Integer) lineupData.get("lineupId");
        List<Map<String, Object>> entries = (List<Map<String, Object>>) lineupData.get("entries");

        List<Map<String, Object>> result = new ArrayList<>();
        for(Map<String, Object> entry: entries) {
            String contestId = (String) entry.get("contestId");
            Integer multiplier = (Integer) entry.get("multiple");
            boolean replace = false;
            if(entry.containsKey("replace")) {
                replace = (Boolean) entry.get("replace");
            }

            /*
             * Verify the provided contest.
             */
            Contest contest = DaoFactory.getContestDao().findContest(contestId);
            if (contest == null) {
                Map<String, Object> errorData = new HashMap<>();
                errorData.put("contestId", contestId);
                errorData.put("code", GlobalConstants.CONTEST_ENTRY_ERROR_INVALID_ID);
                errorData.put("description", "The provided contest id " + contestId + " is invalid");
                result.add(errorData);
                continue;
            }

            /*
             * Verify the provided lineups.
             */
            Lineup lineup = DaoFactory.getContestDao().findLineup(lineupId);
            if (lineup == null) {
                Map<String, Object> errorData = new HashMap<>();
                errorData.put("contestId", contestId);
                errorData.put("code", GlobalConstants.CONTEST_ENTRY_ERROR_INVALID_ID);
                errorData.put("description", "The provided lineup id " + lineupId + " is invalid");
                result.add(errorData);
                continue;
            }

            // How many entries do we currently have for this lineup?
            List<Entry> entriesForLineup = contestDao.findEntries(lineup, contest);
            int numEntries = entriesForLineup.size();
            int adjustment = multiplier - numEntries;

            // No adjustment necessary and we're not replacing.
            if(adjustment == 0) {
                result.add(processContestJoinSuccess(contest, 0, lineup));
                continue;
            }
            // We are replacing
            else if(replace) {
                entriesForLineup = contestDao.findEntries(user, contest);

                /*
                 * Make sure there are entries that we can replace.  If not, just fall through into the
                 * other logic and treat this as a simple add.
                 */
                if(!entriesForLineup.isEmpty()) {
                    int numEntriesAffected = 0;
                    for (Entry e : entriesForLineup) {
                        e.setLineup(lineup);
                        contestDao.saveEntry(e);
                        numEntriesAffected++;
                    }
                    result.add(processContestJoinSuccess(contest, numEntriesAffected, lineup));
                    continue;
                }
            }

            // Need to add more entries to the contest
            int numEntriesAffected = 0;
            if(adjustment > 0) {

                int status = -1;
                for(int i=0; i<adjustment; i++) {
                    status = contestDao.joinContest(user, contest, lineup);
                    if(status != GlobalConstants.CONTEST_ENTRY_SUCCESS) {
                        result.add(processContestJoinPartialSuccess(contest, numEntriesAffected, status, lineup));
                        break;
                    }
                    else {
                        numEntriesAffected++;
                    }
                }
                if(status == GlobalConstants.CONTEST_ENTRY_SUCCESS) {
                    result.add(processContestJoinSuccess(contest, numEntriesAffected, lineup));
                }
            }
            // Need to remove at least one entry (if not locked/active).
            else {
                int status = -1;
                for(int i=0; i<adjustment*-1; i++) {
                    status = contestDao.removeFromContest(user, contest, lineup, 1);
                    if(status == GlobalConstants.CONTEST_ENTRY_SUCCESS) {
                        numEntriesAffected++;
                    }
                    else {
                        result.add(processContestJoinPartialSuccess(contest, numEntriesAffected, status, lineup));
                    }
                }
                if(status == GlobalConstants.CONTEST_ENTRY_SUCCESS) {
                    result.add(processContestJoinSuccess(contest, numEntriesAffected, lineup));
                }
            }
        }

        return jok(result);
    }

    /**
     * @return
     * @throws JsonProcessingException
     * @throws JSONException
     */
    @SecuredAction
    public static Result enter() throws JsonProcessingException, JSONException {
        IContestDao contestDao = DaoFactory.getContestDao();
        LineupForm lineupForm = getLineupForm();

        List<Map<String, Object>> result = new ArrayList<>();
        for(LineupEntryForm lineupEntryForm: lineupForm.entries) {
            if (lineupEntryForm.contestId == null) {
                Map<String, Object> errorData = new HashMap<>();
                errorData.put("contestId", lineupEntryForm.contestId);
                errorData.put("code", GlobalConstants.CONTEST_ENTRY_ERROR_INVALID_ID);
                errorData.put("description", "The provided contest id " + lineupEntryForm.contestId + " is invalid");
                result.add(errorData);
                continue;
            }

            Contest contest = contestDao.findContest(lineupEntryForm.contestId);

            if (contest == null) {
                Map<String, Object> errorData = new HashMap<>();
                errorData.put("contestId", lineupEntryForm.contestId);
                errorData.put("code", GlobalConstants.CONTEST_ENTRY_ERROR_INVALID_ID);
                errorData.put("description", "The provided contest id " + lineupEntryForm.contestId + " is invalid");
                result.add(errorData);
                continue;
            }

            Lineup lineup = null;
            if (lineupForm.lineupId != null) {
                lineup = contestDao.findLineup(Integer.parseInt(lineupForm.lineupId));
            }

            User user = getCurrentUser();
            if (user == null) {
                result.add(processContestJoinError(contest, GlobalConstants.CONTEST_ENTRY_ERROR_SESSION_EXPIRED, null));
                continue;
            }

            if (lineup == null) {
                try {
                    List<LineupSpot> lineupSpots = mapLineupSpots(lineupForm);
                    if (lineupForm.lineupName == null) {
                        lineupForm.lineupName = "LINEUP-" + LocalDate.now().toString();
                    }
                    lineup = new Lineup(lineupForm.lineupName, user, contest.getLeague(), contest.getSportEventGrouping());
                    contestDao.validateLineup(lineup, contest.getSalaryCap(), lineupSpots);
                    lineup.setLineupSpots(lineupSpots);

                    // If we got here the lineup is valid, so save it.
                    contestDao.saveLineup(lineup);
                } catch (LineupValidationException e) {
                    Map<String, Object> errorData;

                    if (e.getMessage().equals(GlobalConstants.MINIMUM_SPORT_EVENTS_ERROR)) {
                        errorData = processContestJoinError(contest, GlobalConstants.CONTEST_ENTRY_ERROR_NOT_ENOUGH_SPORT_EVENTS, null);
                    } else if (e.getMessage().equals(GlobalConstants.SALARY_CAP_EXCEEDED_ERROR)) {
                        errorData = processContestJoinError(contest, GlobalConstants.CONTEST_ENTRY_ERROR_OVER_SALARY_CAP, null);
                    } else if (e.getMessage().equals(GlobalConstants.LINEUP_SIZE_INVALID_ERROR)) {
                        errorData = processContestJoinError(contest, GlobalConstants.CONTEST_ENTRY_ERROR_INVALID_LINEUP_SIZE, null);
                    }else if(e instanceof LineupValidationDuplicateAthleteException) {
                        errorData = processContestJoinError(contest, GlobalConstants.CONTEST_ENTRY_ERROR_DUPLICATE_ATHLETES, null);
                    } else {
                        errorData = processContestJoinError(contest, GlobalConstants.CONTEST_ENTRY_ERROR_OTHER, null);
                    }
                    result.add(errorData);
                }
            }

            int status = contestDao.joinContest(user, contest, lineup);
            if (status == GlobalConstants.CONTEST_ENTRY_SUCCESS) {
                result.add(processContestJoinSuccess(contest, 1, lineup));
            } else {
                result.add(processContestJoinError(contest, status, lineup));
            }
        }

        return jok(result);
    }

    @SecuredAction
    public static Result remove(Integer lineupId, String contestId) {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>() {};
        User user = getCurrentUser();

        Map<String, Object> resultMap = new HashMap<>();
        if (user == null) {
            resultMap.put("lineupId", lineupId);
            resultMap.put("contestId", contestId);
            resultMap.put("code", GlobalConstants.CONTEST_ENTRY_ERROR_SESSION_EXPIRED);
            resultMap.put("description", "The user's session has expired");
            return jerr(resultMap);
        }

        Contest contest = contestDao.findContest(contestId);
        if(contest == null) {
            resultMap.put("lineupId", lineupId);
            resultMap.put("contestId", contestId);
            resultMap.put("code", GlobalConstants.CONTEST_ENTRY_ERROR_INVALID_ID);
            resultMap.put("description", "The provided contest id is invalid.");
            return jerr(resultMap);
        }

        Lineup lineup = contestDao.findLineup(lineupId);
        if(lineup == null || !lineup.getUser().equals(user)) {
            resultMap.put("lineupId", lineupId);
            resultMap.put("contestId", contestId);
            resultMap.put("code", GlobalConstants.CONTEST_ENTRY_ERROR_INVALID_ID);
            resultMap.put("description", "The provided lineup id is invalid.");
            return jerr(resultMap);
        }

        LineupService lineupManager = DistributedServices.getContext().getBean("LineupManager", LineupService.class);
        try {
            String result = lineupManager.removeLineupFromContest(user, lineup, contest);
            return jok(mapper.readValue(result, typeReference));
        } catch (IOException e) {
            resultMap.put("lineupId", lineupId);
            resultMap.put("contestId", contestId);
            resultMap.put("code", GlobalConstants.CONTEST_ENTRY_ERROR_OTHER);
            resultMap.put("description", "An error occurred while attempting to remove entries from the contest.");
            return jerr(resultMap);
        }
    }

    /**
     * Construct map structure that is expected as a response for a successful call.
     *
     * @param contest               The contest that the user joined.
     * @param numEntriesEffected    The number of entries that have been either added or removed.
     * @param lineup                The lineup that the user joined the contest with.
     * @return
     */
    private static Map<String, Object> processContestJoinSuccess(Contest contest, int numEntriesEffected, Lineup lineup) {
        Map<String, Object> successData = new HashMap<>();
        successData.put("contestId", contest.getUrlId());
        successData.put("code", GlobalConstants.CONTEST_ENTRY_SUCCESS);
        successData.put("stopCode", GlobalConstants.CONTEST_ENTRY_SUCCESS);
        successData.put("entries", numEntriesEffected);
        successData.put("lineupId", lineup.getId());

        return successData;
    }

    /**
     * Construct map structure that is expected as a response for a semi-successful call.  This will be used in
     * the event that the number of added or removed entries is less than the number desired.
     *
     * @param contest            The contest to join.
     * @param numEntriesEffected The number of entries that have been either added or removed.
     * @param status             The error message status explaining why not all entries could be added or removed.
     * @return
     */
    private static Map<String, Object> processContestJoinPartialSuccess(Contest contest, int numEntriesEffected, int status, Lineup lineup) {
        Map<String, Object> partialSuccessData = new HashMap<>();
        Map<String, Object> errorData = processContestJoinError(contest, status, lineup);

        partialSuccessData.put("contestId", contest.getUrlId());
        partialSuccessData.put("lineupId", lineup == null ? -1 : lineup.getId());
        partialSuccessData.put("code", GlobalConstants.CONTEST_ENTRY_SUCCESS);
        partialSuccessData.put("numEntries", numEntriesEffected);
        partialSuccessData.put("stopCode", errorData.get("code"));
        partialSuccessData.put("stopDescription", errorData.get("description"));

        return partialSuccessData;
    }

    /**
     * Construct map structure that is expected as a response for a failed call.
     *
     * @param contest The contest to join.
     * @param status  The reason for failure.
     * @return
     */
    private static Map<String, Object> processContestJoinError(Contest contest, int status, Lineup lineup) {
        Map<String, Object> errorData = new HashMap<>();

        errorData.put("contestId", contest.getUrlId());
        errorData.put("lineupId", lineup == null ? -1 : lineup.getId());
        if (status == GlobalConstants.CONTEST_ENTRY_ERROR_INVALID_ID) {
            errorData.put("code", GlobalConstants.CONTEST_ENTRY_ERROR_INVALID_ID);
            errorData.put("description", "The provided contest id " + contest.getUrlId() + " is invalid.");
        } else if (status == GlobalConstants.CONTEST_ENTRY_ERROR_CONTEST_FULL) {
            errorData.put("code", GlobalConstants.CONTEST_ENTRY_ERROR_CONTEST_FULL);
            errorData.put("description", "The provided contest id " + contest.getUrlId() + " is full.");
        } else if (status == GlobalConstants.CONTEST_ENTRY_ERROR_CONTEST_STARTED) {
            errorData.put("code", GlobalConstants.CONTEST_ENTRY_ERROR_CONTEST_STARTED);
            errorData.put("description", "The provided contest id " + contest.getUrlId() + " has started.");
        } else if (status == GlobalConstants.CONTEST_ENTRY_ERROR_NOT_OPEN) {
            errorData.put("code", GlobalConstants.CONTEST_ENTRY_ERROR_NOT_OPEN);
            errorData.put("description", "The contest you are trying to enter is no longer open (" + contest.getContestState().getName() + ")");
        } else if (status == GlobalConstants.CONTEST_ENTRY_ERROR_SINGLE_ENTRY_DUPE) {
            errorData.put("code", GlobalConstants.CONTEST_ENTRY_ERROR_SINGLE_ENTRY_DUPE);
            errorData.put("description", "The contest you are trying to enter is single-entry and you are already entered.");
        } else if (status == GlobalConstants.CONTEST_ENTRY_ERROR_INCOMPATIBLE_LINEUP) {
            errorData.put("code", GlobalConstants.CONTEST_ENTRY_ERROR_INCOMPATIBLE_LINEUP);
            errorData.put("description", "The contest you are trying to enter is not compatible with the provided lineup.");
        } else if (status == GlobalConstants.CONTEST_ENTRY_ERROR_OVER_SALARY_CAP) {
            errorData.put("code", GlobalConstants.CONTEST_ENTRY_ERROR_OVER_SALARY_CAP);
            errorData.put("description", GlobalConstants.SALARY_CAP_EXCEEDED_ERROR);
        } else if (status == GlobalConstants.CONTEST_ENTRY_ERROR_INSUFFICIENT_FUNDS) {
            errorData.put("code", GlobalConstants.CONTEST_ENTRY_ERROR_INSUFFICIENT_FUNDS);
            errorData.put("description", "Insufficient funds.");
        } else if(status == GlobalConstants.CONTEST_ENTRY_ERROR_SESSION_EXPIRED) {
            errorData.put("code", GlobalConstants.CONTEST_ENTRY_ERROR_SESSION_EXPIRED);
            errorData.put("description", "Your session has expired.");
        } else if(status == GlobalConstants.CONTEST_ENTRY_ERROR_DUPLICATE_ATHLETES) {
            errorData.put("code", GlobalConstants.CONTEST_ENTRY_ERROR_DUPLICATE_ATHLETES);
            errorData.put("description", "The provided lineup contains the same athlete in multiple lineup spots.");
        } else if(status == GlobalConstants.CONTEST_ENTRY_ERROR_INVALID_LINEUP_SIZE) {
            errorData.put("code", GlobalConstants.CONTEST_ENTRY_ERROR_INVALID_LINEUP_SIZE);
            errorData.put("description", "The provided lineup does not contain the expected number of lineup spots.");
        } else {
            errorData.put("code", GlobalConstants.CONTEST_ENTRY_ERROR_OTHER);
            errorData.put("description", "An error occurred while attempting to enter your lineup.");
        }
        return errorData;
    }

    private static List<LineupSpot> mapLineupSpots(LineupForm lineupForm) throws LineupValidationException {
        List<LineupSpot> lineupSpots = new ArrayList<>();
        for (LineupAthleteForm form : lineupForm.athletes) {
            Athlete athlete = Ebean.find(Athlete.class, form.id);
            AthleteSportEventInfo athleteSportEventInfo = DaoFactory.getSportsDao().findAthleteSportEventInfo(form.athleteSportEventInfoId);
            if (athlete == null) {
                throw new LineupValidationException("error finding athlete with id: " + form.id);
            }
            if (athleteSportEventInfo == null) {
                throw new LineupValidationException("error finding AthleteSportEventInfo with id: " + form.athleteSportEventInfoId);
            }

            Position position = Ebean.find(Position.class, form.pos);
            if (position == null) {
                throw new LineupValidationException("error finding position with id: " + form.pos);
            }
            lineupSpots.add(new LineupSpot(athlete, position, athleteSportEventInfo));
        }
        return lineupSpots;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LineupForm {
        public String lineupName;
        public String lineupId;
        public List<LineupAthleteForm> athletes;

        /*
         * Used with contest entry and quick entry
         */
        public List<LineupEntryForm> entries;

        /*
         * Used with lineup update.
         */
        public List<Integer> entryIds;
    }

    public static class LineupEntryForm {
        public String contestId;
        public int multiple;
        public boolean replace;
    }

    public static class LineupAthleteForm {
        public int id;
        public int pos;
        public int athleteSportEventInfoId;
    }
}
