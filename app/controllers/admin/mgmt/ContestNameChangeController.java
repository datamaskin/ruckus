package controllers.admin.mgmt;

import com.avaje.ebean.Ebean;
import controllers.AbstractController;
import controllers.admin.AdminSecuredActionResponse;
import dao.IContestDao;
import distributed.DistributedServices;
import models.contest.Contest;
import models.user.UserRole;
import play.mvc.Result;
import securesocial.core.java.SecuredAction;

import java.util.List;

/**
 * Created by davidb on 8/26/14.
 */
public class ContestNameChangeController extends AbstractController {
    private static final IContestDao contestDao = DistributedServices.getContext().getBean("contestDao", IContestDao.class);

    @SecuredAction(responses = AdminSecuredActionResponse.class, authorization = UserRole.class, params = {"admin"})
    public static Result index() {
        List<Contest> cl = contestDao.findNonTerminalContests();
        return ok(views.html.mgmt.contestNameChange.render(cl));
    }

    public static Result changeContestName() {

        String[] cIds           = request().body().asFormUrlEncoded().get("contestIds");
        String[] contestName    = request().body().asFormUrlEncoded().get("contestname");

        for(String contestIdStr: cIds) {
            Integer contestId = Integer.parseInt(contestIdStr);
            Contest contest = Ebean.find(Contest.class).where().eq("id", contestId).findUnique();
            contest.setDisplayName(contestName[0]);
            contestDao.saveContest(contest);
        }

        return redirect(controllers.routes.AdminController.mgmt());
    }
}
