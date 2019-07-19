package controllers.admin.mgmt;

import com.avaje.ebean.Ebean;
import controllers.admin.AdminSecuredActionResponse;
import dao.IContestDao;
import dao.ISportsDao;
import distributed.DistributedServices;
import distributed.tasks.ContestCreatorTask;
import models.contest.ContestPayout;
import models.contest.ContestType;
import models.sports.League;
import models.sports.SportEvent;
import models.sports.SportEventGrouping;
import models.sports.SportEventGroupingType;
import models.user.UserRole;
import play.data.Form;
import play.data.validation.Constraints;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import securesocial.core.java.SecuredAction;
import utils.ITimeService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by mwalsh on 8/7/14.
 */
public class ContestCreatorController extends Controller {

    private static final IContestDao contestDao = DistributedServices.getContext()
            .getBean("contestDao", IContestDao.class);
    private static final ISportsDao sportsDao = DistributedServices.getContext()
            .getBean("sportsDao", ISportsDao.class);
    private static final ITimeService timeService = DistributedServices.getContext()
            .getBean("timeService", ITimeService.class);

    @SecuredAction(responses = AdminSecuredActionResponse.class, authorization = UserRole.class, params = {"admin"})
    public static Result index() {
        List<ContestType> contestTypes = Ebean.find(ContestType.class).findList();
        List<SportEvent> sportEvents = Ebean.find(SportEvent.class)
                .fetch("league")
                .where()
                .gt(SportEvent.START_TIME, new Date())
                .eq("is_active", true)
                .orderBy(SportEvent.START_TIME)
                .findList();
        List<SportEventGroupingType> sportEventGroupingTypes = Ebean.find(SportEventGroupingType.class)
                .fetch("league")
                .where().eq("is_active", true)
                .findList();
        List<SportEventGrouping> sportEventGroupings = Ebean.find(SportEventGrouping.class)
                .where()
                .gt("event_date", Date.from(timeService.getNow()))
                .findList();
        return ok(views.html.mgmt.contestCreator.render(contestTypes, sportEventGroupingTypes, sportEventGroupings, sportEvents));
    }

    @SecuredAction(responses = AdminSecuredActionResponse.class, authorization = UserRole.class, params = {"admin"})
    public static Result createSportEventGrouping() {
        int sportEventGroupingTypeInt = Integer.parseInt(request().body().asFormUrlEncoded().get("sportEventGroupingTypeId")[0]);
        String[] sportEventIds = request().body().asFormUrlEncoded().get("sportEventIds[]");
        List<SportEvent> sportEvents = new ArrayList<>();
        League league = null;
        for (String sportEventId : sportEventIds) {
            SportEvent sportEvent = sportsDao.findSportEvent(Integer.parseInt(sportEventId));
            if (league != null && sportEvent.getLeague().equals(league) == false) {
                throw new IllegalArgumentException("Can't create sport event groupings between different leagues. Asshole.");
            }
            league = sportEvent.getLeague();
            sportEvents.add(sportEvent);
        }

        SportEventGroupingType sportEventGroupingType;
        for (SportEventGroupingType type : sportsDao.findAllSportEventGroupingTypes()) {
            if (type.getId() == sportEventGroupingTypeInt) {
                sportEventGroupingType = type;
                SportEventGrouping sportEventGrouping = new SportEventGrouping(sportEvents, sportEventGroupingType);
                contestDao.saveSportEventGrouping(sportEventGrouping);
                return redirect(controllers.admin.mgmt.routes.ContestCreatorController.index());
            }
        }

        throw new IllegalArgumentException("Invalid sport event grouping type.");
    }

    @SecuredAction(responses = AdminSecuredActionResponse.class, authorization = UserRole.class, params = {"admin"})
    public static Result createStandardContests() {
        int sportEventGroupingId = Integer.parseInt(request().body().asFormUrlEncoded().get("sportEventGroupingId")[0]);

        SportEventGrouping sportEventGrouping = Ebean
                .find(SportEventGrouping.class).where()
                .eq("id", sportEventGroupingId)
                .findUnique();

        new ContestCreatorTask().createContests(sportEventGrouping);
        return redirect(controllers.routes.AdminController.index());
    }

    public static final Form<CreateContestForm> createContestForm = Form.form(CreateContestForm.class);

    public static class CreateContestForm {
        public String displayName;
        @Constraints.Required
        public Integer contestTypeId;
        @Constraints.Required
        public Integer sportEventGroupingId;
        @Constraints.Required
        public Integer capacity;
        @Constraints.Required
        public Integer entryFee;
        @Constraints.Required
        public Integer allowedEntries;
        @Constraints.Required
        public Integer salaryCap;
        public Boolean isOpen;
    }

    @SecuredAction(responses = AdminSecuredActionResponse.class, authorization = UserRole.class, params = {"admin"})
    public static Result createSingleContest() {
        Http.RequestBody bod = request().body();

        CreateContestForm form = createContestForm.bindFromRequest().get();
        ContestType contestType = Ebean.find(ContestType.class, form.contestTypeId);

        SportEventGrouping sportEventGrouping = Ebean
                .find(SportEventGrouping.class).where()
                .eq("id", form.sportEventGroupingId)
                .findUnique();

        League league = sportEventGrouping.getSportEventGroupingType().getLeague();

        List<ContestPayout> payouts = new ArrayList<>();
        Map<String, String[]> encodedForm = request().body().asFormUrlEncoded();
        String[] leadingPositionArray = encodedForm.get("leadingPosition");
        String[] trailingArray = encodedForm.get("trailingPosition");
        String[] payoutArray = encodedForm.get("payoutAmount");
        for (int index = 0; index < payoutArray.length; index++) {
            if ("".equals(leadingPositionArray[index])
                    || "".equals(trailingArray[index])
                    || "".equals(payoutArray[index])) {
                break;
            }
            payouts.add(new ContestPayout(
                    Integer.parseInt(leadingPositionArray[index]),
                    Integer.parseInt(trailingArray[index]),
                    Integer.parseInt(payoutArray[index])));
        }

        if(form.isOpen != null && form.isOpen){
            contestDao.createNewOpenContest(
                    contestType, league, form.displayName,
                    form.capacity, true,
                    form.entryFee, form.allowedEntries,
                    form.salaryCap,
                    sportEventGrouping,
                    payouts,
                    null);
        } else {
            contestDao.createNewContest(
                    contestType, league, form.displayName,
                    form.capacity, true,
                    form.entryFee, form.allowedEntries,
                    form.salaryCap,
                    sportEventGrouping,
                    payouts,
                    null);
        }

        return redirect(controllers.routes.AdminController.index());
    }
}
