package controllers.admin.mgmt;

import controllers.admin.AdminSecuredActionResponse;
import distributed.DistributedServices;
import models.user.UserRole;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecuredAction;
import stats.manager.nfl.AthleteManager;
import stats.manager.nfl.DepthChartManager;
import stats.manager.nfl.GameOddsManager;
import stats.predictive.nfl.DefenseProjector;
import stats.predictive.nfl.OffenseProjector;

/**
 * Created by mwalsh on 8/8/14.
 */
public class StatsController extends Controller {

    private static final AthleteManager ATHLETE_MANAGER = DistributedServices.getContext()
            .getBean("StatsNflAthleteManager", AthleteManager.class);

    private static final GameOddsManager oddsManager = DistributedServices.getContext()
            .getBean("StatsNflGameOddsManager", GameOddsManager.class);

    @SecuredAction(responses = AdminSecuredActionResponse.class, authorization = UserRole.class, params = {"admin"})
    public static Result index() throws Exception {
        return ok(views.html.mgmt.stats.render());
    }

    @SecuredAction(responses = AdminSecuredActionResponse.class, authorization = UserRole.class, params = {"admin"})
    public static Result updateNflGameOdds() throws Exception {
        oddsManager.process();
        return ok("Done updateNflGameOdds!");
    }

    @SecuredAction(responses = AdminSecuredActionResponse.class, authorization = UserRole.class, params = {"admin"})
    public static Result updateNflGameLogs() {
        try {
            ATHLETE_MANAGER.process();
            return ok("Done updateNflGameLogs!");
        } catch (Exception e) {
            return ok("Error updating NFL game logs: " + e.getMessage());
        }
    }

    @SecuredAction(responses = AdminSecuredActionResponse.class, authorization = UserRole.class, params = {"admin"})
    public static Result updateNflPredictions() throws Exception {
        String updateStats = request().getQueryString("updateStats");
        String updateOdds = request().getQueryString("updateOdds");
        if (updateStats != null && updateStats.equalsIgnoreCase("true")) {
            updateNflGameLogs();
        }
        if (updateOdds != null && updateOdds.equalsIgnoreCase("true")) {
            updateNflGameOdds();
        }
        new DepthChartManager().process();
        new DefenseProjector().predict();
        new OffenseProjector().predict();
        return ok("Done updateNflPredictions!");
    }

}
