package controllers.admin.mgmt;

import com.avaje.ebean.Ebean;
import controllers.admin.AdminSecuredActionResponse;
import dao.IContestDao;
import distributed.DistributedServices;
import models.contest.Contest;
import models.user.UserRole;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecuredAction;

import java.util.List;


public class ContestCancelController extends Controller {

    private static final IContestDao contestDao = DistributedServices.getContext()
            .getBean("contestDao", IContestDao.class);

    @SecuredAction(responses = AdminSecuredActionResponse.class, authorization = UserRole.class, params = {"admin"})
    public static Result index() {
        List<Contest> cl = contestDao.findNonTerminalContests();

        return ok(views.html.mgmt.contestCancel.render(cl));
    }

    @SecuredAction(responses = AdminSecuredActionResponse.class, authorization = UserRole.class, params = {"admin"})
    public static Result cancelNonTerminalContests() {

        String[] cIds=request().body().asFormUrlEncoded().get("contestIds");

        for(String contestIdStr: cIds) {
            Integer contestId = Integer.parseInt(contestIdStr);
            Contest contest = Ebean.find(Contest.class).where().eq("id", contestId).findUnique();
            contestDao.cancelContest(contest);
        }

        return redirect(controllers.routes.AdminController.mgmt());
    }
}
