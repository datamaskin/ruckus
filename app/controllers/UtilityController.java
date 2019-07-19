package controllers;

import com.avaje.ebean.Ebean;
import dao.DaoFactory;
import models.sports.League;
import models.sports.SportEvent;
import models.sports.SportEventGrouping;
import models.user.UserRole;
import play.mvc.Result;
import securesocial.core.java.SecuredAction;
import simulator.ContestSimulationManager;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

/**
 * Created by mgiles on 8/4/14.
 */
public class UtilityController extends AbstractController {
    @SecuredAction(authorization = UserRole.class, params = {"admin"})
    public static Result createTestContest(String leagueAbbreviation, String date) {
        League league = DaoFactory.getSportsDao().findLeague(leagueAbbreviation);
        if (league == null) {
            ok("ERROR - The specified league is invalid.  Please choose from " + League.ALL_LEAGUES.toString());
        }

        ZonedDateTime instant = ZonedDateTime.parse(date + "T00:00:00+00:00[America/New_York]");
        if (instant == null) {
            ok("ERROR - The specified date is invalid.");
        }

        instant = instant.withZoneSameInstant(ZoneId.of("America/New_York"));

        ZonedDateTime early = instant.withHour(0).withMinute(0).withSecond(0);
        ZonedDateTime late = instant.withHour(23).withMinute(59).withSecond(59);

        List<SportEventGrouping> sportEventGroupings = Ebean.find(SportEventGrouping.class)
                .where()
                .eq("sportEventGroupingType.league", league)
                .between("eventDate", Date.from(early.toInstant()), Date.from(late.toInstant()))
                .findList();

        SportEventGrouping sportEventGrouping = null;
        if (!sportEventGroupings.isEmpty()) {
            sportEventGrouping = sportEventGroupings.get(0);
        } else {
            return ok("ERROR - No appropriate SportEventGroupings found!");
        }

        List<SportEvent> sportEvents = DaoFactory.getSportsDao().findSportEvents(league, Date.from(early.toInstant()), Date.from(late.toInstant()));

        /*
         * Create contest that starts in 10 minutes.
         */
        ContestSimulationManager contestSimulationManager = new ContestSimulationManager();
        contestSimulationManager.createContest(league, sportEvents, sportEventGrouping);

//        contestSimulationManager.setUpSocket(sportEventId, socketPort);
//        contestSimulationManager.configureDistributedSocket(sportEventId, socketPort);

        contestSimulationManager.startSimulatorThread(sportEvents);

        StringBuilder sb = new StringBuilder("Started simulator thread for sport events ");
        for (SportEvent sportEvent : sportEvents) {
            sb.append(String.format("%s vs %s @ %s\n", sportEvent.getTeams().get(0).getAbbreviation(), sportEvent.getTeams().get(1).getAbbreviation(), sportEvent.getStartTime()));
        }

        return ok(sb.toString());
    }

    @SecuredAction(authorization = UserRole.class, params = {"admin"})
    public static Result stopSimulator(Integer sportEventId) {
//        ContestSimulationManager.stopSimulatorThread(sportEventId);

        return ok("Stopping simulator thread for sport event " + sportEventId);
    }

    @SecuredAction(authorization = UserRole.class, params = {"admin"})
    public static Result initData() throws Exception {
        DaoFactory.getSportsDao().init();

        return jok("Done!");
    }
}
