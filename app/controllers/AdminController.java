package controllers;

import com.avaje.ebean.Ebean;
import controllers.admin.AdminSecuredActionResponse;
import distributed.DistributedServices;
import models.contest.ContestType;
import models.sports.SportEvent;
import models.sports.SportEventGrouping;
import models.sports.SportEventGroupingType;
import models.user.UserRole;
import play.mvc.Result;
import securesocial.core.java.SecuredAction;
import stats.manager.mlb.MlbEventManager;
import stats.manager.nfl.AthleteManager;
import stats.manager.nfl.GameOddsManager;
import utils.ITimeService;
import views.html.adminIndex;

import java.util.Date;
import java.util.List;

/**
 * Created by mwalsh on 7/14/14.
 */
public class AdminController extends AbstractController {

    private static final ITimeService timeService = DistributedServices.getContext()
            .getBean("timeService", ITimeService.class);
    private static final MlbEventManager EVENT_MANAGER = DistributedServices.getContext()
            .getBean("StatsMlbEventManager", MlbEventManager.class);
    private static final AthleteManager ATHLETE_MANAGER = DistributedServices.getContext()
            .getBean("StatsNflAthleteManager", AthleteManager.class);
    private static final GameOddsManager oddsManager = DistributedServices.getContext()
            .getBean("StatsNflGameOddsManager", GameOddsManager.class);

    @SecuredAction(responses = AdminSecuredActionResponse.class, authorization = UserRole.class, params = {"admin"})
    public static Result index() {
        return ok(adminIndex.render());
    }
    @SecuredAction(responses = AdminSecuredActionResponse.class, authorization = UserRole.class, params = {"admin"})
    public static Result devops() {
        return ok(views.html.devops.index.render());
    }
    @SecuredAction(responses = AdminSecuredActionResponse.class, authorization = UserRole.class, params = {"admin"})
    public static Result mgmt() {
        return ok(views.html.mgmt.index.render());
    }
    @SecuredAction(responses = AdminSecuredActionResponse.class, authorization = UserRole.class, params = {"admin"})
    public static Result cs() {
        return ok(views.html.cs.index.render());
    }

    @SecuredAction(responses = AdminSecuredActionResponse.class, authorization = UserRole.class, params = {"admin"})
    public static Result lobby() {
        return getResult();
    }

    private static Result getResult() {
        List<ContestType> contestTypes = Ebean.find(ContestType.class).findList();
        List<SportEvent> sportEvents = Ebean.find(SportEvent.class)
                .where().gt(SportEvent.START_TIME, new Date())
                .orderBy(SportEvent.START_TIME)
                .findList();
        List<SportEventGroupingType> sportEventGroupingTypes = Ebean.find(SportEventGroupingType.class).findList();
        List<SportEventGrouping> sportEventGroupings = Ebean.find(SportEventGrouping.class)
                .where().gt("event_date", Date.from(timeService.getNow())).findList();
        return ok(views.html.admin.lobby.render(contestTypes, sportEventGroupingTypes, sportEventGroupings, sportEvents));
    }

    @SecuredAction(responses = AdminSecuredActionResponse.class, authorization = UserRole.class, params = {"admin"})
    public static Result updateMlbGamelogs() {
        try {
            EVENT_MANAGER.process();
            return ok("Done!");
        } catch (Exception e) {
            return ok("Error updating MLB game logs: " + e.getMessage());
        }
    }

    @SecuredAction(responses = AdminSecuredActionResponse.class, authorization = UserRole.class, params = {"admin"})
    public static Result updateNflGamelogs() {
        try {
            ATHLETE_MANAGER.process();
            return ok("Done!");
        } catch (Exception e) {
            return ok("Error updating NFL game logs: " + e.getMessage());
        }
    }

    @SecuredAction(responses = AdminSecuredActionResponse.class, authorization = UserRole.class, params = {"admin"})
    public static Result updateNflGameOdds() throws Exception {
        oddsManager.process();
        return jok("Done!");
    }

}
