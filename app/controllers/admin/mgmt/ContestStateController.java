package controllers.admin.mgmt;

import com.avaje.ebean.Ebean;
import controllers.*;
import dao.IContestDao;
import distributed.DistributedServices;
import models.contest.Contest;
import models.contest.ContestState;
import models.user.UserRole;
import play.mvc.Result;
import securesocial.core.java.SecuredAction;

import java.util.List;

/**
 * Created by davidb on 8/27/14.
 */
public class ContestStateController extends AbstractController {
    private static final IContestDao contestDao = DistributedServices.getContext().getBean("contestDao", IContestDao.class);

    @SecuredAction(authorization = UserRole.class, params = {"admin"})
    public static Result index() {
        List<Contest> cl = contestDao.findContests(ContestState.uninitialized);
        List<ContestState> states =  contestDao.findContestStates();
        return ok(views.html.mgmt.contestState.render(cl, states));
    }

    @SecuredAction(authorization = UserRole.class, params = {"admin"})
    public static Result contestChangeState() {
        String[] cIds = request().body().asFormUrlEncoded().get("contestIds");
        String[] state = request().body().asFormUrlEncoded().get("contestState");

        for (String contestIdStr: cIds) {
            Integer contestId = Integer.parseInt(contestIdStr);
            Contest contest = Ebean.find(Contest.class).where().eq("id", contestId).findUnique();
            List<ContestState> states = contestDao.findContestStates();
            Integer stateId = Integer.parseInt(state[0]);
            int stateInt = stateId.intValue();

            for (ContestState contestStates: states) {
                int id = contestStates.getId();
                if (id == stateInt) {
                    contest.setContestState(contestStates);
                    break;
                }
            }
            contestDao.saveContest(contest);
        }

        return redirect(controllers.routes.AdminController.mgmt());
    }
}
