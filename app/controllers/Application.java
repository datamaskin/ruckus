package controllers;

import auth.AppEnvironment;
import common.SecureSocialCookie;
import play.mvc.Http;
import service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.GlobalConstants;
import dao.DaoFactory;
import dao.IUserDao;
import distributed.DistributedServices;
import models.contest.Contest;
import models.contest.Lineup;
import models.sports.AthleteSportEventInfo;
import models.sports.League;
import models.user.User;
import org.json.JSONException;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.F;
import play.mvc.Result;
import securesocial.core.java.SecureSocial;
import securesocial.core.java.SecuredAction;
import securesocial.core.java.UserAwareAction;
import stats.manager.IStatsDefenseVsPositionManager;
import stats.manager.nfl.DefenseVsPositionManager;
import stats.translator.IFantasyPointTranslator;
import stats.translator.nfl.FantasyPointTranslator;
import utils.UsernameValidator;
import utils.WordFilter;
import utils.email.EmailSender;
import utils.email.IEmailSender;
import utils.email.WelcomeEmail;
import views.html.app;
import views.html.jasmine;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;

public class Application extends AbstractController {

    private static IEmailSender emailSender = EmailSender.getInstance();

    @UserAwareAction
    public static Result index() {
        User user = getCurrentUser();
        if (user != null) {
            return redirect("/app");
        }
        return controllers.PublicController.landing();
    }

    @SecuredAction
    public static Result app() {
        User user = getCurrentUser();
        if (user != null && (user.getUserName().equals("") || user.getUserName().equals(user.getEmail()))) {
            return ok(views.html.auth.username.render(play.api.i18n.Lang.defaultLang(), AppEnvironment.getEnvironment()));
        }
        String ss_id = request().cookie(SecureSocialCookie.getName()).value();
        response().setCookie(SecureSocialCookie.getName(), ss_id, Integer.MAX_VALUE);

        return ok(app.render(user));
    }

    @UserAwareAction
    public static Result username() {
        User user = getCurrentUser();
        if (user != null && user.getUserName().equals(user.getEmail())) {
            return ok(views.html.auth.username.render(play.api.i18n.Lang.defaultLang(), AppEnvironment.getEnvironment()));
        }
        return redirect("/#lobby");
    }

    @UserAwareAction
    public static Result handleUsername() {
        DynamicForm requestData = Form.form().bindFromRequest();
        String username = requestData.get("username");
        if (username == null || username.equals("")) {
            flash("error", "Please enter a valid user name");
            return redirect("/username");
        }
        username = username.trim();

        if (!UsernameValidator.isValid(username)) {
            flash("error", "Username cannot contain spaces or special characters");
            return redirect("/username");
        }

        if (!WordFilter.isClean(username)) {
            flash("error", "That username is unavailable");
            return redirect("/username");
        }

        if (WordFilter.isReserved(username)) {
            flash("error", "That username is reserved. Please contact customer support if you would like to claim it");
            return redirect("/username");
        }

        IUserDao appUserDao = DaoFactory.getUserDao();
        User userByName = appUserDao.findUserByUsername(username);
        if (userByName != null) {
            flash("error", "That user name is unavailable");
            return redirect("/username");
        }
        final User user = getCurrentUser();
        if (user != null && user.getUserName().equals(user.getEmail())) {
            user.setUserName(username);
            AffiliateUtils.setUserAffiliate4Java(request(), user);
            appUserDao.updateUser(user);
            ctx().args.put(SecureSocial.USER_KEY, user);

            F.Promise.promise(() -> {
                emailSender.sendMail(user.getEmail(), "Welcome to Victiv.com", new WelcomeEmail(user));
                return null;
            });

            return redirect("/#lobby");
        }
        return redirect("/#lobby");
    }

    public static Result logout() {
        User user = getCurrentUser();
        if (user != null) {
            ctx().args.remove(SecureSocial.USER_KEY);
        }
        session().clear();
        flash().clear();
        response().discardCookie(SecureSocialCookie.getName());
        return redirect("/#lobby");
    }

    @SecuredAction
    public static Result getContestEntries() throws JSONException, JsonProcessingException {
        String contestId = request().getQueryString("contestId");
        ContestEntriesService cache = (ContestEntriesService) DistributedServices.getContext().getBean("ContestEntriesManager");
        return ok(cache.getContestEntriesAsJson(contestId));
    }

    public static Result login() {
        session().put("SecureSocial.OriginalUrlKey", "/#lobby");
        return redirect(AuthenticationController.AUTH_LOGIN);
    }

    @SecuredAction
    public static Result getContestLiveRanks(String contestId) throws JSONException, JsonProcessingException {
        User user = getCurrentUser();
        ContestLiveRanksService cache = (ContestLiveRanksService) DistributedServices.getContext().getBean("ContestLiveRanksManager");
        return ok(cache.getRanksAsJson(contestId, user));
    }

    @SecuredAction
    public static Result getContest() throws JSONException, JsonProcessingException {
        String contestId = request().getQueryString("contestId");
        IContestListService cache = DistributedServices.getContext().getBean("ContestListManager", IContestListService.class);
        return ok(cache.getContestAsJson(contestId));
    }

    @SecuredAction
    public static Result getContestSuggestions(String contestId, Integer lineupId, Integer status) {
        ContestSuggestionService cache = (ContestSuggestionService) DistributedServices.getContext().getBean("ContestSuggestionManager");
        User user = getCurrentUser();
        try {
            return ok(cache.getContestSuggestions(user, contestId, lineupId, status));
        } catch (JsonProcessingException e) {
            return ok("{\"error\":\"An error occurred while generating contest suggestions - " + e.getMessage() + ".\"}");
        }
    }

    @SecuredAction
    public static Result getContestLiveOverview() throws IOException {
        User user = getCurrentUser();
        ContestLiveOverviewService cache = (ContestLiveOverviewService) DistributedServices.getContext().getBean("ContestLiveOverviewManager");
        cache.setUser(user);
        return ok(cache.getOverviewAsJson(user));
    }

    @SecuredAction
    public static Result getContestLiveLineup(String lineupId) throws JSONException, JsonProcessingException {
        int lineupIdInt = Integer.parseInt(lineupId);
        ContestLiveLineupService cache = (ContestLiveLineupService) DistributedServices.getContext().getBean("ContestLiveLineupManager");
        return ok(cache.getLinupAsJson(lineupIdInt));
    }

    @SecuredAction
    public static Result getAthletes() throws JSONException, JsonProcessingException {
        String contestId = request().getQueryString("contestId");
        ContestAthletesService cache = (ContestAthletesService) DistributedServices.getContext().getBean("ContestAthletesManager");

        Contest contest = DaoFactory.getContestDao().findContest(contestId);
        IFantasyPointTranslator translator = contest.getStatsFantasyPointTranslator(DistributedServices.getContext());
        cache.setTranslator(translator);
        return ok(cache.getContestAthletesAsJson(contestId));
    }

    @SecuredAction
    public static Result getAllAthleteExposure() throws IOException {
        User user = getCurrentUser();
        if (user == null) {
            ObjectMapper mapper = new ObjectMapper();
            Logger.error("User session has expired.");
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("error", "User session has expired.");
            return ok(mapper.writeValueAsString(errorData));
        }

        AthleteExposureService athleteExposureManager = DistributedServices.getContext().getBean("AthleteExposureManager", AthleteExposureService.class);
        return ok(athleteExposureManager.getAthleteExposure(user));
    }

    /**
     * Retrieve the list of athletes (and relevant data) that are in your active or completed contests.
     *
     * @param contestId                     The id of the contest we're interested in (can be null).
     * @param athleteSportEventInfoId       The id of the AthleteSportEventInfo we're interested in (can be null).
     * @return                              A JSON string representing data for those athletes.
     * @throws IOException
     */
    @SecuredAction
    public static Result getContestLiveAthletes(String contestId, String athleteSportEventInfoId) throws IOException {
        User user = getCurrentUser();
        ObjectMapper mapper = new ObjectMapper();
        ContestLiveAthleteService cache = (ContestLiveAthleteService) DistributedServices.getContext().getBean("ContestLiveAthleteManager");
        AthleteExposureService athleteExposureManager = DistributedServices.getContext().getBean("AthleteExposureManager", AthleteExposureService.class);
        AthleteContestRankService athleteContestRankManager = DistributedServices.getContext().getBean("AthleteContestRankManager", AthleteContestRankService.class);
        cache.setAthleteContestRankManager(athleteContestRankManager);
        cache.setAthleteExposureManager(athleteExposureManager);

        if (contestId != null) {
            if (athleteSportEventInfoId != null) {
                Integer athleteSportEventInfoIdInt;
                try {
                    athleteSportEventInfoIdInt = Integer.parseInt(athleteSportEventInfoId);
                } catch (NumberFormatException e) {
                    Logger.error("Unable to parse id for AthleteSportEventInfo.");
                    Map<String, Object> errorData = new HashMap<>();
                    errorData.put("error", "Unable to parse id for AthleteSportEventInfo.");
                    return ok(mapper.writeValueAsString(errorData));
                }
                return ok(cache.getAthleteForContestAsJson(user, contestId, athleteSportEventInfoIdInt, false));
            } else {
                return ok(cache.getAthletesForContestAsJson(user, contestId));
            }
        } else {
            return ok(cache.getAthletesForUserAsJson(user));
        }
    }

    @SecuredAction
    public static Result getContestLiveTeamFeed(Integer lineupId) throws IOException {
        User user = getCurrentUser();
        Lineup lineup = DaoFactory.getContestDao().findLineup(lineupId);

        Map<String, String> errorMap = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        if (lineup == null) {
            errorMap.put("error", "The specified lineup is invalid.");
            return ok(mapper.writeValueAsBytes(errorMap));
        } else if (!lineup.getUser().equals(user)) {
            errorMap.put("error", "The specified lineup does not belong to the logged-in user.");
            return ok(mapper.writeValueAsBytes(errorMap));
        }

        ContestLiveTimelineService cache = (ContestLiveTimelineService) DistributedServices.getContext().getBean("ContestLiveTimelineManager");
        return ok(cache.getLineupTimeline(lineupId));
    }

    @SecuredAction
    public static Result getAthleteExposure(String athleteSportEventInfoId) throws IOException {
        User user = getCurrentUser();
        AthleteExposureService cache = (AthleteExposureService) DistributedServices.getContext().getBean("AthleteExposureManager");

        ObjectMapper mapper = new ObjectMapper();
        TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>() {
        };

        List<Map<String, Object>> result = new ArrayList<>();
        String[] athleteSportEventInfoIds = athleteSportEventInfoId.split(",");
        for (int i = 0; i < athleteSportEventInfoIds.length; i++) {
            String data = cache.getAthleteExposure(user, athleteSportEventInfoIds[i]);
            result.add(mapper.readValue(data, typeReference));
        }

        return ok(mapper.writeValueAsBytes(result));
    }

    @SecuredAction
    public static Result getAthleteContestRanks(String athleteSportEventInfoId) throws IOException {
        User user = getCurrentUser();
        AthleteContestRankService cache = (AthleteContestRankService) DistributedServices.getContext().getBean("AthleteContestRankManager");

        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<Map<String, Object>>> typeReference = new TypeReference<List<Map<String, Object>>>() {
        };

        List<Map<String, Object>> result = new ArrayList<>();
        String[] athleteSportEventInfoIds = athleteSportEventInfoId.split(",");

        for (String athleteSportEventInfoId1 : athleteSportEventInfoIds) {
            Integer athleteSportEventInfoIdInt;
            try {
                athleteSportEventInfoIdInt = Integer.parseInt(athleteSportEventInfoId1);
            } catch (NumberFormatException e) {
                Logger.error("Unable to parse id for AthleteSportEventInfo.");
                Map<String, Object> errorData = new HashMap<>();
                errorData.put("error", "Unable to parse id for AthleteSportEventInfo.");
                return ok(mapper.writeValueAsString(errorData));
            }

            String data = cache.getAthleteContestRanks(user, athleteSportEventInfoIdInt);
            result.add(mapper.readValue(data, typeReference));
        }

        return ok(mapper.writeValueAsBytes(result));
    }

    @SecuredAction
    public static Result getAthletePercentOwned(String contestId, Integer athleteSportEventInfoId, Integer entryId) throws IOException {
        AthletePercentOwnedService cache = (AthletePercentOwnedService) DistributedServices.getContext().getBean("AthletePercentOwnedManager");
        return ok(cache.getPercentOwned(contestId, athleteSportEventInfoId, entryId));
    }

    @SecuredAction
    public static Result getAthleteComparison(String contestId, Integer athleteSportEventInfoId) throws IOException {
        AthleteCompareService cache = (AthleteCompareService) DistributedServices.getContext().getBean("AthleteCompareManager");

        AthleteSportEventInfo athleteSportEventInfo = DaoFactory.getSportsDao().findAthleteSportEventInfo(athleteSportEventInfoId);
        League league = athleteSportEventInfo.getSportEvent().getLeague();
        IFantasyPointTranslator translator = null;
        IStatsDefenseVsPositionManager defenseVsPositionManager = null;
        if (league.equals(League.MLB)) {
            translator = (stats.translator.mlb.FantasyPointTranslator) DistributedServices.getContext().getBean("MLBFantasyPointTranslator");
            defenseVsPositionManager = (stats.manager.mlb.DefenseVsPositionManager) DistributedServices.getContext().getBean("MlbDefenseVsPositionManager");
            ((stats.manager.mlb.DefenseVsPositionManager) defenseVsPositionManager).setDvpCache(DistributedServices.getInstance().getMap(GlobalConstants.MLB_DEFENSE_VS_POSITION_MAP));
        } else if (league.equals(League.NFL)) {
            translator = (FantasyPointTranslator) DistributedServices.getContext().getBean("NFLFantasyPointTranslator");
            defenseVsPositionManager = (DefenseVsPositionManager) DistributedServices.getContext().getBean("NflDefenseVsPositionManager");
            ((DefenseVsPositionManager) defenseVsPositionManager).setDvpCache(DistributedServices.getInstance().getMap(GlobalConstants.NFL_DEFENSE_VS_POSITION_MAP));
        }

        cache.setTranslator(translator);
        cache.setDefenseVsPositionManager(defenseVsPositionManager);
        return ok(cache.getComparison(contestId, athleteSportEventInfoId));
    }

    @SecuredAction
    public static Result getContestEvents() throws JSONException, JsonProcessingException {
        String contestId = request().getQueryString("contestId");
        ContestEventsService cache = (ContestEventsService) DistributedServices.getContext().getBean("ContestEventsManager");
        return ok(cache.getContestEventsAsJson(contestId));
    }

    public static Result contestFilter() throws JsonProcessingException {
        ContestFilterService cache = (ContestFilterService) DistributedServices.getContext().getBean("ContestFilterManager");
        return ok(cache.retrieveFiltersAsJson());
    }

    public static Result scoringRules() throws JsonProcessingException {
        ScoringRulesService cache = (ScoringRulesService) DistributedServices.getContext().getBean("ScoringRulesManager");
        return ok(cache.retrieveScoringRulesAsJson());
    }

    public static Result getTimestamp() {
        Instant instant = Instant.now().atZone(ZoneId.systemDefault()).toInstant();
        return ok(String.valueOf(Date.from(instant).getTime()));
    }

    public static Result jasmine() {
        return ok(jasmine.render());
    }

    public static Result lineupRules() throws JsonProcessingException {
        String leagueParam = request().getQueryString("league").toUpperCase();
        LineupRulesService cache = (LineupRulesService) DistributedServices.getContext().getBean("LineupRulesManager");
        return ok(cache.getLineupRulesAsJson(leagueParam));
    }

}
