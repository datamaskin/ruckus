package dao;


import com.avaje.ebean.*;
import com.avaje.ebeaninternal.server.lib.util.NotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.GlobalConstants;
import controllers.ContestTemplatePopulator;
import distributed.DistributedServices;
import models.contest.*;
import models.sports.*;
import models.stats.*;
import models.stats.mlb.StatsMlbBatting;
import models.stats.mlb.StatsMlbPitching;
import models.stats.nfl.StatsNflAthleteByEvent;
import models.stats.nfl.StatsNflDefenseByEvent;
import models.user.User;
import models.user.UserBonusType;
import models.user.UserRole;
import org.json.JSONException;
import org.json.JSONObject;
import play.Logger;
import stats.translator.IFantasyPointTranslator;
import utils.ITimeService;
import utils.TimeService;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 * Created by mwalsh on 6/6/14.
 */
public class SportsDao extends AbstractDao implements ISportsDao {
    private static final ITimeService timeService = new TimeService();
    private static final String default_ebean_server = "default";

    @Override
    public void init() {
        if (Ebean.find(UserRole.class).findRowCount() == 0) {
            forceInsert(UserRole.ADMIN_ROLE);
            forceInsert(UserRole.CS_ROLE);
            forceInsert(UserRole.DEVOPS_ROLE);
            forceInsert(UserRole.MGMT_ROLE);
        }

        if (Ebean.find(User.class).where().eq(User.EMAIL, GlobalConstants.ADMIN_EMAIL).findUnique() == null) {
            User admin = new User();
            admin.setId(1L);
            admin.setEmail(GlobalConstants.ADMIN_EMAIL);
            admin.setFirstName("Application");
            admin.setLastName("Administrator");
            //the password is "ruckus235"
            admin.setPassword("$2a$10$8UiXDYUQv8LDvNin3j6hjuHeQNVHA14EG7HLQI1t.4yBdGLpkBOX6");
            admin.setProviderId("userpass");
            admin.setUserName("admin");
            admin.setUserRoles(Arrays.asList(UserRole.ADMIN_ROLE));
            admin.setVerified(new Date());
            forceInsert(admin);
        }

        if (Ebean.find(UserBonusType.class).findRowCount() == 0) {
            for (UserBonusType bonusType : UserBonusType.ALL) {
                forceInsert(bonusType);
            }
        }

        if (Ebean.find(Sport.class).findRowCount() == 0) {
            for (Sport sport : Sport.ALL_SPORTS) {
                forceInsert(sport);
            }
        }

        if (Ebean.find(League.class).findRowCount() == 0) {
            for (League league : League.ALL_LEAGUES) {
                forceInsert(league);
            }
        }

        if (Ebean.find(SportEventGroupingType.class).findRowCount() == 0) {
            League NFL = findLeague("NFL");
            SportEventGroupingType nflFull = new SportEventGroupingType(NFL, "NFL Full",
                    Arrays.asList(new SportEventDateRangeSelector(DayOfWeek.THURSDAY, 0, 0, DayOfWeek.TUESDAY, 0, 0)));

            SportEventGroupingType nflStandard = new SportEventGroupingType(NFL, "NFL Standard",
                    Arrays.asList(new SportEventDateRangeSelector(DayOfWeek.SUNDAY, 0, 0, DayOfWeek.MONDAY, 0, 0)));

            League MLB = findLeague("MLB");
            SportEventGroupingType mlbMon = new SportEventGroupingType(MLB, "MLB Monday",
                    Arrays.asList(new SportEventDateRangeSelector(DayOfWeek.MONDAY, 0, 0, DayOfWeek.TUESDAY, 0, 0)));

            SportEventGroupingType mlbTue = new SportEventGroupingType(MLB, "MLB Tuesday",
                    Arrays.asList(new SportEventDateRangeSelector(DayOfWeek.TUESDAY, 0, 0, DayOfWeek.WEDNESDAY, 0, 0)));

            SportEventGroupingType mlbWed = new SportEventGroupingType(MLB, "MLB Wednesday",
                    Arrays.asList(new SportEventDateRangeSelector(DayOfWeek.WEDNESDAY, 0, 0, DayOfWeek.THURSDAY, 0, 0)));

            SportEventGroupingType mlbThu = new SportEventGroupingType(MLB, "MLB Thursday",
                    Arrays.asList(new SportEventDateRangeSelector(DayOfWeek.THURSDAY, 0, 0, DayOfWeek.FRIDAY, 0, 0)));

            SportEventGroupingType mlbFri = new SportEventGroupingType(MLB, "MLB Friday",
                    Arrays.asList(new SportEventDateRangeSelector(DayOfWeek.FRIDAY, 0, 0, DayOfWeek.SATURDAY, 0, 0)));

            SportEventGroupingType mlbSat = new SportEventGroupingType(MLB, "MLB Saturday",
                    Arrays.asList(new SportEventDateRangeSelector(DayOfWeek.SATURDAY, 0, 0, DayOfWeek.SUNDAY, 0, 0)));

            SportEventGroupingType mlbSun = new SportEventGroupingType(MLB, "MLB Sunday",
                    Arrays.asList(new SportEventDateRangeSelector(DayOfWeek.SUNDAY, 0, 0, DayOfWeek.MONDAY, 0, 0)));

            Arrays.asList(mlbMon, mlbTue, mlbWed, mlbThu, mlbFri, mlbSat, mlbSun, nflFull, nflStandard).forEach(this::forceInsert);
        }


        if (findAllPositions().size() == 0) {
            for (Position pos : Position.ALL_BASEBALL) {
                forceInsert(pos);
            }

            for (Position pos : Position.ALL_FOOTBALL) {
                forceInsert(pos);
            }

            for (Position pos : Position.ALL_BASKETBALL) {
                forceInsert(pos);
            }
        }

        for (LineupTemplate lineupTemplate : Ebean.find(LineupTemplate.class).findList()) {
            Ebean.delete(lineupTemplate);
        }
        forceInsert(new LineupTemplate(League.NFL, Position.FB_DEFENSE, 1));
        forceInsert(new LineupTemplate(League.NFL, Position.FB_FLEX, 2));
//        forceInsert(new LineupTemplate(League.NFL, Position.FB_KICKER, 1));
        forceInsert(new LineupTemplate(League.NFL, Position.FB_QUARTERBACK, 1));
        forceInsert(new LineupTemplate(League.NFL, Position.FB_RUNNINGBACK, 2));
        forceInsert(new LineupTemplate(League.NFL, Position.FB_TIGHT_END, 1));
        forceInsert(new LineupTemplate(League.NFL, Position.FB_WIDE_RECEIVER, 2));

        forceInsert(new LineupTemplate(League.MLB, Position.BS_CATCHER, 1));
        forceInsert(new LineupTemplate(League.MLB, Position.BS_FIRST_BASE, 1));
        forceInsert(new LineupTemplate(League.MLB, Position.BS_FLEX, 1));
        forceInsert(new LineupTemplate(League.MLB, Position.BS_OUTFIELD, 3));
        forceInsert(new LineupTemplate(League.MLB, Position.BS_PITCHER, 2));
        forceInsert(new LineupTemplate(League.MLB, Position.BS_SECOND_BASE, 1));
        forceInsert(new LineupTemplate(League.MLB, Position.BS_SHORT_STOP, 1));
        forceInsert(new LineupTemplate(League.MLB, Position.BS_THIRD_BASE, 1));

        if (Ebean.find(ContestType.class).findRowCount() == 0) {
            for (ContestType type : ContestType.ALL) {
                forceInsert(type);
            }
        }

        if (Ebean.find(ContestGrouping.class).findRowCount() == 0) {
            for (ContestGrouping grouping : ContestGrouping.ALL) {
                forceInsert(grouping);
            }
        }

        /*
         * Scoring rules.
         */
        Map<String, BigDecimal> scoringRules = new LinkedHashMap<>();
        scoringRules.put(GlobalConstants.SCORING_NFL_RUSHING_YARDS_LABEL, GlobalConstants.SCORING_NFL_RUSHING_YARDS_FACTOR);
        scoringRules.put(GlobalConstants.SCORING_NFL_PASSING_YARDS_LABEL, GlobalConstants.SCORING_NFL_PASSING_YARDS_FACTOR);
        scoringRules.put(GlobalConstants.SCORING_NFL_RECEIVING_YARDS_LABEL, GlobalConstants.SCORING_NFL_RECEIVING_YARDS_FACTOR);
        scoringRules.put(GlobalConstants.SCORING_NFL_RUSHING_TOUCHDOWN_LABEL, GlobalConstants.SCORING_NFL_RUSHING_TOUCHDOWN_FACTOR);
        scoringRules.put(GlobalConstants.SCORING_NFL_PASSING_TOUCHDOWN_LABEL, GlobalConstants.SCORING_NFL_PASSING_TOUCHDOWN_FACTOR);
        scoringRules.put(GlobalConstants.SCORING_NFL_RECEIVING_TOUCHDOWN_LABEL, GlobalConstants.SCORING_NFL_RECEIVING_TOUCHDOWN_FACTOR);
        scoringRules.put(GlobalConstants.SCORING_NFL_RECEPTION_LABEL, GlobalConstants.SCORING_NFL_RECEPTION_FACTOR);
        scoringRules.put(GlobalConstants.SCORING_NFL_LOST_FUMBLE_LABEL, GlobalConstants.SCORING_NFL_LOST_FUMBLE_FACTOR);
        scoringRules.put(GlobalConstants.SCORING_NFL_INTERCEPTION_LABEL, GlobalConstants.SCORING_NFL_INTERCEPTION_FACTOR);
        scoringRules.put(GlobalConstants.SCORING_NFL_PUNT_RETURN_TOUCHDOWN_LABEL, GlobalConstants.SCORING_NFL_PUNT_RETURN_TOUCHDOWN_FACTOR);
        scoringRules.put(GlobalConstants.SCORING_NFL_KICK_RETURN_TOUCHDOWN_LABEL, GlobalConstants.SCORING_NFL_KICK_RETURN_TOUCHDOWN_FACTOR);
        scoringRules.put(GlobalConstants.SCORING_NFL_TWO_POINT_CONVERSION_LABEL, GlobalConstants.SCORING_NFL_TWO_POINT_CONVERSION_FACTOR);
        scoringRules.put(GlobalConstants.SCORING_NFL_SACK_LABEL, GlobalConstants.SCORING_NFL_SACK_FACTOR);
        scoringRules.put(GlobalConstants.SCORING_NFL_DEF_INTERCEPTION_LABEL, GlobalConstants.SCORING_NFL_DEF_INTERCEPTION_FACTOR);
        scoringRules.put(GlobalConstants.SCORING_NFL_FUMBLE_RECOVERY_LABEL, GlobalConstants.SCORING_NFL_FUMBLE_RECOVERY_FACTOR);
        scoringRules.put(GlobalConstants.SCORING_NFL_INTERCEPTION_RETURN_TD_LABEL, GlobalConstants.SCORING_NFL_INTERCEPTION_RETURN_TD_FACTOR);
        scoringRules.put(GlobalConstants.SCORING_NFL_FUMBLE_RECOVERY_TD_LABEL, GlobalConstants.SCORING_NFL_FUMBLE_RECOVERY_TD_FACTOR);
        scoringRules.put(GlobalConstants.SCORING_NFL_BLOCKED_PUNT_FG_RETURN_TD_LABEL, GlobalConstants.SCORING_NFL_BLOCKED_PUNT_FG_RETURN_TD_FACTOR);
        scoringRules.put(GlobalConstants.SCORING_NFL_SAFETY_LABEL, GlobalConstants.SCORING_NFL_SAFETY_FACTOR);
        scoringRules.put(GlobalConstants.SCORING_NFL_BLOCKED_KICK_LABEL, GlobalConstants.SCORING_NFL_BLOCKED_KICK_FACTOR);
        scoringRules.put(GlobalConstants.SCORING_NFL_POINTS_ALLOWED_LABEL, GlobalConstants.SCORING_NFL_POINTS_ALLOWED_FACTOR);

        // Reconcile differences between what's in the database currently and what we want in there, based on the map above.
        // We start by grabbing the list of what's already in the database, and then iterate through the map of desired
        // scoring rules, deleting the corresponding list entry when it's found.  At the end of the loop, any remaining
        // list items can be considered candidates for deleting.
        // Additionally, if we don't find a corresponding list entry, that means we want to put something in the database, so
        // we'll just do an insert on the spot.
        List<ScoringRule> nflRules = Ebean.find(ScoringRule.class).where().eq("league", League.NFL).findList();
        for (Map.Entry<String, BigDecimal> entry : scoringRules.entrySet()) {
            int index = 0;
            boolean found = false;
            while (index < nflRules.size()) {
                ScoringRule scoringRule = nflRules.get(index);
                if (entry.getKey().equals(scoringRule.getRuleName())) {
                    nflRules.remove(index);
                    found = true;
                    continue;
                }
                index++;
            }

            if (!found) {
                forceInsert(new ScoringRule(entry.getKey(), League.NFL, entry.getValue()));
            }
        }

        for (ScoringRule scoringRule : nflRules) {
            Ebean.delete(scoringRule);
        }

	    /*
         * Populate scoring rules table with MLB rules.
	     */
        if (Ebean.find(ScoringRule.class).where().eq("league", League.MLB).findList().isEmpty()) {
            forceInsert(new ScoringRule(GlobalConstants.SCORING_MLB_SINGLE_LABEL, League.MLB, GlobalConstants.SCORING_MLB_SINGLE_FACTOR));
            forceInsert(new ScoringRule(GlobalConstants.SCORING_MLB_DOUBLE_LABEL, League.MLB, GlobalConstants.SCORING_MLB_DOUBLE_FACTOR));
            forceInsert(new ScoringRule(GlobalConstants.SCORING_MLB_TRIPLE_LABEL, League.MLB, GlobalConstants.SCORING_MLB_TRIPLE_FACTOR));
            forceInsert(new ScoringRule(GlobalConstants.SCORING_MLB_HOMERUN_LABEL, League.MLB, GlobalConstants.SCORING_MLB_HOMERUN_FACTOR));
            forceInsert(new ScoringRule(GlobalConstants.SCORING_MLB_RUN_BATTED_IN_LABEL, League.MLB, GlobalConstants.SCORING_MLB_RUN_BATTED_IN_FACTOR));
            forceInsert(new ScoringRule(GlobalConstants.SCORING_MLB_RUN_LABEL, League.MLB, GlobalConstants.SCORING_MLB_RUN_FACTOR));
            forceInsert(new ScoringRule(GlobalConstants.SCORING_MLB_WALK_LABEL, League.MLB, GlobalConstants.SCORING_MLB_WALK_FACTOR));
            forceInsert(new ScoringRule(GlobalConstants.SCORING_MLB_HIT_BY_PITCH_LABEL, League.MLB, GlobalConstants.SCORING_MLB_HIT_BY_PITCH_FACTOR));
            forceInsert(new ScoringRule(GlobalConstants.SCORING_MLB_STOLEN_BASE_LABEL, League.MLB, GlobalConstants.SCORING_MLB_STOLEN_BASE_FACTOR));
            forceInsert(new ScoringRule(GlobalConstants.SCORING_MLB_CAUGHT_STEALING_LABEL, League.MLB, GlobalConstants.SCORING_MLB_CAUGHT_STEALING_FACTOR));

            forceInsert(new ScoringRule(GlobalConstants.SCORING_MLB_INNING_PITCHED_LABEL, League.MLB, GlobalConstants.SCORING_MLB_INNING_PITCHED_FACTOR));
            forceInsert(new ScoringRule(GlobalConstants.SCORING_MLB_STRIKEOUT_LABEL, League.MLB, GlobalConstants.SCORING_MLB_STRIKEOUT_FACTOR));
            forceInsert(new ScoringRule(GlobalConstants.SCORING_MLB_WIN_LABEL, League.MLB, GlobalConstants.SCORING_MLB_WIN_FACTOR));
            forceInsert(new ScoringRule(GlobalConstants.SCORING_MLB_EARNED_RUN_LABEL, League.MLB, GlobalConstants.SCORING_MLB_EARNED_RUN_FACTOR));
            forceInsert(new ScoringRule(GlobalConstants.SCORING_MLB_PITCHER_HIT_LABEL, League.MLB, GlobalConstants.SCORING_MLB_PITCHER_HIT_FACTOR));
            forceInsert(new ScoringRule(GlobalConstants.SCORING_MLB_PITCHER_WALK_LABEL, League.MLB, GlobalConstants.SCORING_MLB_PITCHER_WALK_FACTOR));
            forceInsert(new ScoringRule(GlobalConstants.SCORING_MLB_PITCHER_HIT_BY_PITCH_LABEL, League.MLB, GlobalConstants.SCORING_MLB_PITCHER_HIT_BY_PITCH_FACTOR));
        }

        if (Ebean.find(ContestTemplate.class).findRowCount() == 0) {
            List<ContestTemplate> contestTemplatePopulators = new ContestTemplatePopulator().populate();
            for (ContestTemplate contestTemplate : contestTemplatePopulators) {
                forceInsert(contestTemplate);
            }
        }

        for (League league : League.ALL_LEAGUES) {

            List<ContestEntryFee> contestEntryFeeList = Ebean.find(ContestEntryFee.class).where().eq("league", league).findList();
            for (ContestEntryFee contestEntryFee : contestEntryFeeList) {
                Ebean.delete(contestEntryFee);
            }

            // All entry fees as cents
            int[] entryFees = new int[]{0, 200, 500, 1000, 2000, 5000, 10000, 20000};

            for (int entryFee : entryFees) {
                ContestEntryFee contestEntryFee = new ContestEntryFee(league, entryFee);
                forceInsert(contestEntryFee);
            }

            /*
             * Contest Number of Users.
             */
            List<ContestNumberOfUsers> contestNumberOfUsersList = Ebean.find(ContestNumberOfUsers.class).where().eq("league", league).findList();
            for (ContestNumberOfUsers contestNumberOfUsers : contestNumberOfUsersList) {
                Ebean.delete(contestNumberOfUsers);
            }

            int[] minUsers = new int[]{0, 2, 6, 10, 20, 21};
            int[] maxUsers = new int[]{100000, 2, 6, 10, 20, 100000};

            for (int i = 0; i < minUsers.length; i++) {
                ContestNumberOfUsers contestNumberOfUsers = new ContestNumberOfUsers(league, minUsers[i], maxUsers[i]);
                forceInsert(contestNumberOfUsers);
            }

            /*
             * Contest Salary Cap
             */
            if (Ebean.find(ContestSalary.class).where().eq("league", league).findRowCount() == 0) {
                ContestSalary contestSalary = new ContestSalary(league, 5000000);
                forceInsert(contestSalary);
            }

            /**
             * Contest states
             */
            if (Ebean.find(ContestState.class).where().eq("id", new ContestStateUninitialized().getId()).findUnique() == null) {
                forceInsert(new ContestStateUninitialized());
            }
            if (Ebean.find(ContestState.class).where().eq("id", new ContestStateOpen().getId()).findUnique() == null) {
                forceInsert(new ContestStateOpen());
            }
            if (Ebean.find(ContestState.class).where().eq("id", new ContestStateEntriesLocked().getId()).findUnique() == null) {
                forceInsert(new ContestStateEntriesLocked());
            }
            if (Ebean.find(ContestState.class).where().eq("id", new ContestStateRosterLocked().getId()).findUnique() == null) {
                forceInsert(new ContestStateRosterLocked());
            }
            if (Ebean.find(ContestState.class).where().eq("id", new ContestStateActive().getId()).findUnique() == null) {
                forceInsert(new ContestStateActive());
            }
            if (Ebean.find(ContestState.class).where().eq("id", new ContestStateCancelled().getId()).findUnique() == null) {
                forceInsert(new ContestStateCancelled());
            }
            if (Ebean.find(ContestState.class).where().eq("id", new ContestStateComplete().getId()).findUnique() == null) {
                forceInsert(new ContestStateComplete());
            }
            if (Ebean.find(ContestState.class).where().eq("id", new ContestStateHistory().getId()).findUnique() == null) {
                forceInsert(new ContestStateHistory());
            }


            /*
             * Contest Suggestions
             */
            for (ContestSuggestion suggestion : Ebean.find(ContestSuggestion.class).findList()) {
                Ebean.delete(suggestion);
            }

            forceInsert(new ContestSuggestion(ContestType.H2H, 2, ContestType.NORMAL, 6));
            forceInsert(new ContestSuggestion(ContestType.H2H, 2, ContestType.DOUBLE_UP, 6));
            forceInsert(new ContestSuggestion(ContestType.H2H, 2, ContestType.DOUBLE_UP, 10));
            forceInsert(new ContestSuggestion(ContestType.H2H, 2, ContestType.GPP, 6));

            forceInsert(new ContestSuggestion(ContestType.NORMAL, 6, ContestType.NORMAL, 6));
            forceInsert(new ContestSuggestion(ContestType.NORMAL, 6, ContestType.DOUBLE_UP, 6));
            forceInsert(new ContestSuggestion(ContestType.NORMAL, 6, ContestType.NORMAL, 10));
            forceInsert(new ContestSuggestion(ContestType.NORMAL, 6, ContestType.DOUBLE_UP, 10));
            forceInsert(new ContestSuggestion(ContestType.NORMAL, 6, ContestType.GPP, 6));

            forceInsert(new ContestSuggestion(ContestType.NORMAL, 10, ContestType.NORMAL, 10));
            forceInsert(new ContestSuggestion(ContestType.NORMAL, 10, ContestType.DOUBLE_UP, 10));
            forceInsert(new ContestSuggestion(ContestType.NORMAL, 10, ContestType.NORMAL, 6));
            forceInsert(new ContestSuggestion(ContestType.NORMAL, 10, ContestType.DOUBLE_UP, 6));
            forceInsert(new ContestSuggestion(ContestType.NORMAL, 10, ContestType.GPP, 6));

            forceInsert(new ContestSuggestion(ContestType.NORMAL, 20, ContestType.NORMAL, 10));
            forceInsert(new ContestSuggestion(ContestType.NORMAL, 20, ContestType.NORMAL, 6));
            forceInsert(new ContestSuggestion(ContestType.NORMAL, 20, ContestType.DOUBLE_UP, 20));
            forceInsert(new ContestSuggestion(ContestType.NORMAL, 20, ContestType.DOUBLE_UP, 10));
            forceInsert(new ContestSuggestion(ContestType.NORMAL, 20, ContestType.GPP, 6));

            forceInsert(new ContestSuggestion(ContestType.DOUBLE_UP, 6, ContestType.NORMAL, 6));
            forceInsert(new ContestSuggestion(ContestType.DOUBLE_UP, 6, ContestType.DOUBLE_UP, 6));
            forceInsert(new ContestSuggestion(ContestType.DOUBLE_UP, 6, ContestType.NORMAL, 10));
            forceInsert(new ContestSuggestion(ContestType.DOUBLE_UP, 6, ContestType.DOUBLE_UP, 10));
            forceInsert(new ContestSuggestion(ContestType.DOUBLE_UP, 6, ContestType.GPP, 6));

            forceInsert(new ContestSuggestion(ContestType.DOUBLE_UP, 10, ContestType.NORMAL, 6));
            forceInsert(new ContestSuggestion(ContestType.DOUBLE_UP, 10, ContestType.DOUBLE_UP, 6));
            forceInsert(new ContestSuggestion(ContestType.DOUBLE_UP, 10, ContestType.NORMAL, 10));
            forceInsert(new ContestSuggestion(ContestType.DOUBLE_UP, 10, ContestType.DOUBLE_UP, 10));
            forceInsert(new ContestSuggestion(ContestType.DOUBLE_UP, 10, ContestType.GPP, 6));

            forceInsert(new ContestSuggestion(ContestType.DOUBLE_UP, 20, ContestType.NORMAL, 10));
            forceInsert(new ContestSuggestion(ContestType.DOUBLE_UP, 20, ContestType.DOUBLE_UP, 6));
            forceInsert(new ContestSuggestion(ContestType.DOUBLE_UP, 20, ContestType.NORMAL, 20));
            forceInsert(new ContestSuggestion(ContestType.DOUBLE_UP, 20, ContestType.DOUBLE_UP, 10));
            forceInsert(new ContestSuggestion(ContestType.DOUBLE_UP, 20, ContestType.GPP, 6));

            forceInsert(new ContestSuggestion(ContestType.GPP, 20, ContestType.NORMAL, 10));
            forceInsert(new ContestSuggestion(ContestType.GPP, 20, ContestType.DOUBLE_UP, 6));
            forceInsert(new ContestSuggestion(ContestType.GPP, 20, ContestType.NORMAL, 20));
            forceInsert(new ContestSuggestion(ContestType.GPP, 20, ContestType.DOUBLE_UP, 10));
            forceInsert(new ContestSuggestion(ContestType.GPP, 20, ContestType.DOUBLE_UP, 20));

            forceInsert(new ContestSuggestion(ContestType.SATELLITE, 20, ContestType.NORMAL, 10));
            forceInsert(new ContestSuggestion(ContestType.SATELLITE, 20, ContestType.DOUBLE_UP, 6));
            forceInsert(new ContestSuggestion(ContestType.SATELLITE, 20, ContestType.NORMAL, 20));
            forceInsert(new ContestSuggestion(ContestType.SATELLITE, 20, ContestType.DOUBLE_UP, 10));
            forceInsert(new ContestSuggestion(ContestType.SATELLITE, 20, ContestType.DOUBLE_UP, 20));
        }

        if (Ebean.find(UserBonusType.class).findRowCount() == 0) {
            Arrays.asList(UserBonusType.ALL).forEach(this::forceInsert);
        }

    }

    @Override
    public void updateLiveFeedData(StatsLiveFeedData statsLiveFeedData) {
        update(statsLiveFeedData);
    }

    @Override
    public void saveLiveFeedData(StatsLiveFeedData statsLiveFeedData) {
        save(statsLiveFeedData);
    }

    @Override
    public void updateTeam(Team team) {
        update(team);
    }

    @Override
    public void saveTeam(Team team) {
        save(team);
    }

    @Override
    public void updateAthleteSportEventInfo(AthleteSportEventInfo athleteSportEventInfo) {
        update(athleteSportEventInfo);
    }

    @Override
    public void saveAthleteSportEventInfo(AthleteSportEventInfo athleteSportEventInfo) {
        save(athleteSportEventInfo);
    }

    @Override
    public void updateSportEvent(SportEvent sportEvent) {
        update(sportEvent);
    }

    @Override
    public void saveSportEvent(SportEvent sportEvent) {
        save(sportEvent);
    }

    @Override
    public void updateAthlete(Athlete athlete) {
        update(athlete);
    }

    @Override
    public void saveAthlete(Athlete athlete) {
        save(athlete);
    }

    @Override
    public void savePosition(Position position) {
        save(position);
    }

    @Override
    public List<SportEvent> findSportEvents(SportEventGroupingType sportEventGroupingType,
                                            ZonedDateTime localDate) {
        validateParameters(sportEventGroupingType, localDate);

        List<SportEvent> allEvents = new ArrayList<>();

        for (SportEventDateRangeSelector range : sportEventGroupingType.getDateCriteria()) {

            DayOfWeek startDayOfWeek = range.getStartDayOfWeek();
            DayOfWeek endDayOfWeek = range.getEndDayOfWeek();
            ZonedDateTime from = localDate.with(TemporalAdjusters.nextOrSame(startDayOfWeek))
                    .withHour(range.getStartHourOfDay())
                    .withMinute(range.getStartMinuteOfHour())
                    .truncatedTo(ChronoUnit.MINUTES);
            ZonedDateTime until = from.with(TemporalAdjusters.nextOrSame(endDayOfWeek))
                    .withHour(range.getEndHourOfDay())
                    .withMinute(range.getEndMinuteOfHour())
                    .truncatedTo(ChronoUnit.MINUTES);

            List<SportEvent> events = Ebean.find(SportEvent.class).where()
                    .between(SportEvent.START_TIME,
                            Date.from(from.toInstant()),
                            Date.from(until.toInstant()))
                    .eq(SportEvent.LEAGUE_ID, sportEventGroupingType.getLeague().getId())
                    .findList();

            allEvents.addAll(events);
        }
//        if(league.equals(League.NFL)){
//            ZonedDateTime from = null;
//            ZonedDateTime until = null;
//
//            //FULL Thursday - Monday
//            if(contestGrouping.equals(ContestGrouping.NFL_FULL)){
//                from = localDate.with(TemporalAdjusters.next(DayOfWeek.THURSDAY)).truncatedTo(ChronoUnit.DAYS);
//                until = from.with(TemporalAdjusters.next(DayOfWeek.MONDAY)).withHour(23).withMinute(59).withSecond(59);
//            }
//            //LATE Sunday Afternoon - Monday
//            else if(contestGrouping.equals(ContestGrouping.NFL_LATE)){
//                from = localDate.with(TemporalAdjusters.next(DayOfWeek.SUNDAY)).truncatedTo(ChronoUnit.DAYS);
//                until = from.with(TemporalAdjusters.next(DayOfWeek.MONDAY)).withHour(23).withMinute(59).withSecond(59);
//            }
//            //STANDARD Sunday - Monday
//            else if(contestGrouping.equals(ContestGrouping.NFL_STANDARD)){
//                from = localDate.with(TemporalAdjusters.next(DayOfWeek.SUNDAY)).truncatedTo(ChronoUnit.DAYS);
//                until = from.with(TemporalAdjusters.next(DayOfWeek.MONDAY)).withHour(23).withMinute(59).withSecond(59);
//            }
//
//            List<SportEvent> events = Ebean.find(SportEvent.class).where()
//                    .between(SportEvent.START_TIME,
//                            Date.from(from.toInstant()),
//                            Date.from(until.toInstant()))
//                    .eq(SportEvent.LEAGUE_ID, league.getId())
//                    .findList();
//            return events;
//        } else if(league.equals(League.MLB)){
//            ZonedDateTime from = localDate.truncatedTo(ChronoUnit.DAYS);
//            ZonedDateTime until = from.plus(1, ChronoUnit.DAYS);
//
//            List<SportEvent> events = Ebean.find(SportEvent.class).where()
//                    .between(SportEvent.START_TIME,
//                            Date.from(from.toInstant()),
//                            Date.from(until.toInstant()))
//                    .eq(SportEvent.LEAGUE_ID, league.getId())
//                    .findList();
//            return events;
//        }
        return allEvents;
    }

    @Override
    public List<SportEvent> findSportEvents(Contest contest) {
        List<SportEvent> contests = Ebean.find(SportEvent.class).fetch("contest")
                .where().eq(Contest.CONTEST_ID, contest.getId()).findList();
        return contests;
    }

    @Override
    public List<Athlete> findAthletes(SportEvent sportEvent) {
        List<Athlete> athletes = new ArrayList<>();

        if (sportEvent.getTeams() != null) {
            for (Team team : sportEvent.getTeams()) {
                athletes.addAll(Ebean.find(Athlete.class).where().eq(Athlete.TEAM_ID, team.getId()).findList());
            }
        } else {
            //TODO: We could potentially get here when we start running games with athletes that have no team like PGA or NASCAR
            //TODO: We would need to add a league_id to Athletes so we could query by league_id
        }

        return athletes;
    }

    @Override
    public List<Athlete> findAthletes(League league) {
        List<Athlete> athletes = new ArrayList<>();
        List<Team> teams = findTeams(league);
        for (Team team : teams) {
            athletes.addAll(findAthletes(team));
        }
        return athletes;
    }

    @Override
    public List<Athlete> findAthletes(Position position, boolean active) {
        return Ebean.find(Athlete.class).where().in("positions", Arrays.asList(position)).eq("active", active).findList();
    }

    @Override
    public Map<String, BigDecimal> calculateStatAverages(AthleteSportEventInfo athleteSportEventInfo, int pastNGames) {

        Map<String, BigDecimal> data = new LinkedHashMap<>();

        if (athleteSportEventInfo.getSportEvent().getLeague().equals(League.MLB)) {
            if (!athleteSportEventInfo.getAthlete().getPositions().get(0).getAbbreviation().equals(Position.BS_PITCHER.getAbbreviation())) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String sql = "SELECT avg(hits_extra_base_hits), " +
                        "avg(at_bats), " +
                        "avg(on_base_plus_slugging_percentage), " +
                        "avg(runs_batted_in_total), " +
                        "avg(strike_outs), " +
                        "avg(walks_total) " +
                        "FROM stats_mlb_batting b inner join sport_event se on b.event_id = se.stat_provider_id " +
                        "where se.start_time < :start_time and b.stat_provider_id = :stat_provider_id " +
                        "order by se.start_time desc " +
                        "limit :past_n_games";

                SqlQuery sqlQuery = Ebean.createSqlQuery(sql);
                sqlQuery.setParameter("start_time", simpleDateFormat.format(athleteSportEventInfo.getSportEvent().getStartTime()));
                sqlQuery.setParameter("stat_provider_id", athleteSportEventInfo.getAthlete().getStatProviderId());
                sqlQuery.setParameter("past_n_games", pastNGames);

                List<SqlRow> list = sqlQuery.findList();
                for (SqlRow row : list) {
                    Double avgExtraBaseHitsDouble = row.getDouble("avg(hits_extra_base_hits)");
                    BigDecimal avgExtraBaseHits = null;
                    if (avgExtraBaseHitsDouble != null) {
                        avgExtraBaseHits = new BigDecimal(avgExtraBaseHitsDouble);
                    } else {
                        avgExtraBaseHits = new BigDecimal(0);
                    }
                    avgExtraBaseHits = avgExtraBaseHits.setScale(2, RoundingMode.HALF_EVEN);
                    data.put(GlobalConstants.STATS_MLB_EXTRA_BASE_HITS, avgExtraBaseHits);

                    Double avgAtBatsDouble = row.getDouble("avg(at_bats)");
                    BigDecimal avgAtBats = null;
                    if (avgAtBatsDouble != null) {
                        avgAtBats = new BigDecimal(avgAtBatsDouble);
                    } else {
                        avgAtBats = new BigDecimal(0);
                    }
                    avgAtBats = avgAtBats.setScale(2, RoundingMode.HALF_EVEN);
                    data.put(GlobalConstants.STATS_MLB_AT_BATS, avgAtBats);

                    Double avgOPSDouble = row.getDouble("avg(on_base_plus_slugging_percentage)");
                    BigDecimal avgOPS = null;
                    if (avgOPSDouble != null) {
                        avgOPS = new BigDecimal(avgOPSDouble);
                    } else {
                        avgOPS = new BigDecimal(0);
                    }
                    avgOPS = avgOPS.setScale(2, RoundingMode.HALF_EVEN);
                    data.put(GlobalConstants.STATS_MLB_ON_BASE_PLUS_SLUGGING, avgOPS);

                    Double avgRBIsDouble = row.getDouble("avg(runs_batted_in_total)");
                    BigDecimal avgRBIs = null;
                    if (avgRBIsDouble != null) {
                        avgRBIs = new BigDecimal(avgRBIsDouble);
                    } else {
                        avgRBIs = new BigDecimal(0);
                    }
                    avgRBIs = avgRBIs.setScale(2, RoundingMode.HALF_EVEN);
                    data.put(GlobalConstants.STATS_MLB_RBIS, avgRBIs);

                    Double avgStrikeoutsDouble = row.getDouble("avg(strike_outs)");
                    BigDecimal avgStrikeouts = null;
                    if (avgStrikeoutsDouble != null) {
                        avgStrikeouts = new BigDecimal(avgStrikeoutsDouble);
                    } else {
                        avgStrikeouts = new BigDecimal(0);
                    }
                    avgStrikeouts = avgStrikeouts.setScale(2, RoundingMode.HALF_EVEN);
                    data.put(GlobalConstants.STATS_MLB_STRIKEOUTS, avgStrikeouts);

                    Double avgWalksDouble = row.getDouble("avg(walks_total)");
                    BigDecimal avgWalks = null;
                    if (avgWalksDouble != null) {
                        avgWalks = new BigDecimal(avgWalksDouble);
                    } else {
                        avgWalks = new BigDecimal(0);
                    }
                    avgWalks = avgWalks.setScale(2, RoundingMode.HALF_EVEN);
                    data.put(GlobalConstants.STATS_MLB_WALKS, avgWalks);
                }
            } else {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String sql = "SELECT avg(innings_pitched), " +
                        "avg(walks_total), " +
                        "avg(strikeout_walk_ratio), " +
                        "avg(opponent_batting_average), " +
                        "avg(opponent_on_base_percentage) " +
                        "FROM stats_mlb_pitching b inner join sport_event se on b.event_id = se.stat_provider_id " +
                        "where se.start_time < :start_time and b.stat_provider_id = :stat_provider_id " +
                        "order by se.start_time desc " +
                        "limit :past_n_games";

                SqlQuery sqlQuery = Ebean.createSqlQuery(sql);
                sqlQuery.setParameter("start_time", simpleDateFormat.format(athleteSportEventInfo.getSportEvent().getStartTime()));
                sqlQuery.setParameter("stat_provider_id", athleteSportEventInfo.getAthlete().getStatProviderId());
                sqlQuery.setParameter("past_n_games", pastNGames);

                List<SqlRow> list = sqlQuery.findList();
                for (SqlRow row : list) {
                    Double avgInningsPitchedDouble = row.getDouble("avg(innings_pitched)");
                    BigDecimal avgInningsPitched = null;
                    if (avgInningsPitchedDouble != null) {
                        avgInningsPitched = new BigDecimal(avgInningsPitchedDouble);
                    } else {
                        avgInningsPitched = new BigDecimal(0);
                    }
                    avgInningsPitched = avgInningsPitched.setScale(2, RoundingMode.HALF_EVEN);
                    data.put(GlobalConstants.STATS_MLB_INNINGS_PITCHED, avgInningsPitched);

                    Double avgWalksDouble = row.getDouble("avg(walks_total)");
                    BigDecimal avgWalks = null;
                    if (avgWalksDouble != null) {
                        avgWalks = new BigDecimal(avgWalksDouble);
                    } else {
                        avgWalks = new BigDecimal(0);
                    }
                    avgWalks = avgWalks.setScale(2, RoundingMode.HALF_EVEN);
                    data.put(GlobalConstants.STATS_MLB_WALKS, avgWalks);

                    Double avgStrikeoutWalkRatioDouble = row.getDouble("avg(strikeout_walk_ratio)");
                    BigDecimal avgStrikeoutWalkRatio = null;
                    if (avgStrikeoutWalkRatioDouble != null) {
                        avgStrikeoutWalkRatio = new BigDecimal(avgStrikeoutWalkRatioDouble);
                    } else {
                        avgStrikeoutWalkRatio = new BigDecimal(0);
                    }
                    avgStrikeoutWalkRatio = avgStrikeoutWalkRatio.setScale(2, RoundingMode.HALF_EVEN);
                    data.put(GlobalConstants.STATS_MLB_STRIKEOUT_TO_WALK_RATIO, avgStrikeoutWalkRatio);

                    Double avgOpponentBattingAverageDouble = row.getDouble("avg(opponent_batting_average)");
                    BigDecimal avgOpponentBattingAverage = null;
                    if (avgOpponentBattingAverageDouble != null) {
                        avgOpponentBattingAverage = new BigDecimal(avgOpponentBattingAverageDouble);
                    } else {
                        avgOpponentBattingAverage = new BigDecimal(0);
                    }
                    avgOpponentBattingAverage = avgOpponentBattingAverage.setScale(2, RoundingMode.HALF_EVEN);
                    data.put(GlobalConstants.STATS_MLB_OPP_BATTING_AVG, avgOpponentBattingAverage);

                    Double avgOpponentOBADouble = row.getDouble("avg(opponent_on_base_percentage)");
                    BigDecimal avgOpponentOBA = null;
                    if (avgOpponentOBADouble != null) {
                        avgOpponentOBA = new BigDecimal(avgOpponentOBADouble);
                    } else {
                        avgOpponentOBA = new BigDecimal(0);
                    }
                    avgOpponentOBA = avgOpponentOBA.setScale(2, RoundingMode.HALF_EVEN);
                    data.put(GlobalConstants.STATS_MLB_OPPONENT_OBA, avgOpponentOBA);
                }
            }
            return data;
        } else if (athleteSportEventInfo.getSportEvent().getLeague().equals(League.NFL)) {
            if (athleteSportEventInfo.getAthlete().getPositions().get(0).equals(Position.FB_RUNNINGBACK) ||
                    athleteSportEventInfo.getAthlete().getPositions().get(0).equals(Position.FB_WIDE_RECEIVER) ||
                    athleteSportEventInfo.getAthlete().getPositions().get(0).equals(Position.FB_TIGHT_END)) {

                List<StatsNflAthleteByEvent> statsNflAthleteByEvents = DaoFactory.getStatsDao().findStatsNflAthleteByEvents(athleteSportEventInfo.getAthlete(),
                        athleteSportEventInfo.getSportEvent().getStartTime(), pastNGames, new Integer[]{GlobalConstants.EVENT_TYPE_NFL_POST_SEASON, GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON});

                double receivingTouchdownsTotal = 0;
                double receivingYardsTotal = 0;
                double targetsTotal = 0;
                double receptionsTotal = 0;
                double rushingTouchdownsTotal = 0;
                double rushingYardsTotal = 0;
                double rushingAttemptsTotal = 0;
                double fumblesTotal = 0;

                int count = 0;
                for (StatsNflAthleteByEvent statsNflAthleteByEvent : statsNflAthleteByEvents) {
                    receivingTouchdownsTotal += statsNflAthleteByEvent.getReceivingTouchdowns();
                    receivingYardsTotal += statsNflAthleteByEvent.getReceivingYards();
                    targetsTotal += statsNflAthleteByEvent.getReceivingTargets();
                    receptionsTotal += statsNflAthleteByEvent.getReceivingReceptions();
                    rushingTouchdownsTotal += statsNflAthleteByEvent.getRushingTouchdowns();
                    rushingYardsTotal += statsNflAthleteByEvent.getRushingYards();
                    rushingAttemptsTotal += statsNflAthleteByEvent.getRushingAttempts();
                    fumblesTotal += statsNflAthleteByEvent.getFumblesLostTotal();

                    count++;
                }

                if (athleteSportEventInfo.getAthlete().getPositions().contains(Position.FB_RUNNINGBACK)) {
                    data.put(GlobalConstants.STATS_NFL_RUSHING_TOUCHDOWNS, count == 0 ? BigDecimal.ZERO : new BigDecimal(rushingTouchdownsTotal / count).setScale(2, RoundingMode.HALF_EVEN));
                    data.put(GlobalConstants.STATS_NFL_RUSHING_YARDS, count == 0 ? BigDecimal.ZERO : new BigDecimal(rushingYardsTotal / count).setScale(2, RoundingMode.HALF_EVEN));
                    data.put(GlobalConstants.STATS_NFL_RUSHING_ATTEMPTS, count == 0 ? BigDecimal.ZERO : new BigDecimal(rushingAttemptsTotal / count).setScale(2, RoundingMode.HALF_EVEN));
                    data.put(GlobalConstants.STATS_NFL_RECEIVING_TOUCHDOWNS, count == 0 ? BigDecimal.ZERO : new BigDecimal(receivingTouchdownsTotal / count).setScale(2, RoundingMode.HALF_EVEN));
                    data.put(GlobalConstants.STATS_NFL_RECEIVING_YARDS, count == 0 ? BigDecimal.ZERO : new BigDecimal(receivingYardsTotal / count).setScale(2, RoundingMode.HALF_EVEN));
                    data.put(GlobalConstants.STATS_NFL_RECEIVING_TARGETS, count == 0 ? BigDecimal.ZERO : new BigDecimal(targetsTotal / count).setScale(2, RoundingMode.HALF_EVEN));
                    data.put(GlobalConstants.STATS_NFL_RECEPTIONS, count == 0 ? BigDecimal.ZERO : new BigDecimal(receptionsTotal / count).setScale(2, RoundingMode.HALF_EVEN));
                    data.put(GlobalConstants.STATS_NFL_FUMBLES, count == 0 ? BigDecimal.ZERO : new BigDecimal(fumblesTotal / count).setScale(2, RoundingMode.HALF_EVEN));
                } else {
                    data.put(GlobalConstants.STATS_NFL_RECEIVING_TOUCHDOWNS, count == 0 ? BigDecimal.ZERO : new BigDecimal(receivingTouchdownsTotal / count).setScale(2, RoundingMode.HALF_EVEN));
                    data.put(GlobalConstants.STATS_NFL_RECEIVING_YARDS, count == 0 ? BigDecimal.ZERO : new BigDecimal(receivingYardsTotal / count).setScale(2, RoundingMode.HALF_EVEN));
                    data.put(GlobalConstants.STATS_NFL_RECEIVING_TARGETS, count == 0 ? BigDecimal.ZERO : new BigDecimal(targetsTotal / count).setScale(2, RoundingMode.HALF_EVEN));
                    data.put(GlobalConstants.STATS_NFL_RECEPTIONS, count == 0 ? BigDecimal.ZERO : new BigDecimal(receptionsTotal / count).setScale(2, RoundingMode.HALF_EVEN));
                    data.put(GlobalConstants.STATS_NFL_RUSHING_TOUCHDOWNS, count == 0 ? BigDecimal.ZERO : new BigDecimal(rushingTouchdownsTotal / count).setScale(2, RoundingMode.HALF_EVEN));
                    data.put(GlobalConstants.STATS_NFL_RUSHING_YARDS, count == 0 ? BigDecimal.ZERO : new BigDecimal(rushingYardsTotal / count).setScale(2, RoundingMode.HALF_EVEN));
                    data.put(GlobalConstants.STATS_NFL_RUSHING_ATTEMPTS, count == 0 ? BigDecimal.ZERO : new BigDecimal(rushingAttemptsTotal / count).setScale(2, RoundingMode.HALF_EVEN));
                    data.put(GlobalConstants.STATS_NFL_FUMBLES, count == 0 ? BigDecimal.ZERO : new BigDecimal(fumblesTotal / count).setScale(2, RoundingMode.HALF_EVEN));
                }
            } else if (athleteSportEventInfo.getAthlete().getPositions().get(0).equals(Position.FB_QUARTERBACK)) {
                List<StatsNflAthleteByEvent> statsNflAthleteByEvents = DaoFactory.getStatsDao().findStatsNflAthleteByEvents(athleteSportEventInfo.getAthlete(),
                        athleteSportEventInfo.getSportEvent().getStartTime(), pastNGames, new Integer[]{GlobalConstants.EVENT_TYPE_NFL_POST_SEASON, GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON});

                double passingTouchdownsTotal = 0;
                double passingYardsTotal = 0;
                double passingAttemptsTotal = 0;
                double rushingTouchdownsTotal = 0;
                double rushingYardsTotal = 0;
                double rushingAttemptsTotal = 0;
                double sacksTotal = 0;
                double interceptionsTotal = 0;
                double fumblesTotal = 0;

                int count = 0;
                for (StatsNflAthleteByEvent statsNflAthleteByEvent : statsNflAthleteByEvents) {
                    passingTouchdownsTotal += statsNflAthleteByEvent.getPassingTouchdowns();
                    passingYardsTotal += statsNflAthleteByEvent.getPassingYards();
                    passingAttemptsTotal += statsNflAthleteByEvent.getPassingAttempts();
                    rushingTouchdownsTotal += statsNflAthleteByEvent.getRushingTouchdowns();
                    rushingYardsTotal += statsNflAthleteByEvent.getRushingYards();
                    rushingAttemptsTotal += statsNflAthleteByEvent.getRushingAttempts();
                    sacksTotal += statsNflAthleteByEvent.getPassingSacked();
                    interceptionsTotal += statsNflAthleteByEvent.getPassingInterceptions();
                    fumblesTotal += statsNflAthleteByEvent.getFumblesLostTotal();

                    count++;
                }

                data.put(GlobalConstants.STATS_NFL_PASSING_TOUCHDOWNS, count == 0 ? BigDecimal.ZERO : new BigDecimal(passingTouchdownsTotal / count).setScale(2, RoundingMode.HALF_EVEN));
                data.put(GlobalConstants.STATS_NFL_PASSING_YARDS, count == 0 ? BigDecimal.ZERO : new BigDecimal(passingYardsTotal / count).setScale(2, RoundingMode.HALF_EVEN));
                data.put(GlobalConstants.STATS_NFL_PASSING_ATTEMPTS, count == 0 ? BigDecimal.ZERO : new BigDecimal(passingAttemptsTotal / count).setScale(2, RoundingMode.HALF_EVEN));
                data.put(GlobalConstants.STATS_NFL_RUSHING_TOUCHDOWNS, count == 0 ? BigDecimal.ZERO : new BigDecimal(rushingTouchdownsTotal / count).setScale(2, RoundingMode.HALF_EVEN));
                data.put(GlobalConstants.STATS_NFL_RUSHING_YARDS, count == 0 ? BigDecimal.ZERO : new BigDecimal(rushingYardsTotal / count).setScale(2, RoundingMode.HALF_EVEN));
                data.put(GlobalConstants.STATS_NFL_RUSHING_ATTEMPTS, count == 0 ? BigDecimal.ZERO : new BigDecimal(rushingAttemptsTotal / count).setScale(2, RoundingMode.HALF_EVEN));
                data.put(GlobalConstants.STATS_NFL_SACKS, count == 0 ? BigDecimal.ZERO : new BigDecimal(sacksTotal / count).setScale(2, RoundingMode.HALF_EVEN));
                data.put(GlobalConstants.STATS_NFL_INTERCEPTIONS, count == 0 ? BigDecimal.ZERO : new BigDecimal(interceptionsTotal / count).setScale(2, RoundingMode.HALF_EVEN));
                data.put(GlobalConstants.STATS_NFL_FUMBLES, count == 0 ? BigDecimal.ZERO : new BigDecimal(fumblesTotal / count).setScale(2, RoundingMode.HALF_EVEN));
            } else if (athleteSportEventInfo.getAthlete().getPositions().contains(Position.FB_DEFENSE)) {
                List<StatsNflDefenseByEvent> statsNflDefenseByEvents = DaoFactory.getStatsDao().findStatsNflDefenseByEvent(athleteSportEventInfo.getAthlete(),
                        athleteSportEventInfo.getSportEvent().getStartTime(), pastNGames, new Integer[]{GlobalConstants.EVENT_TYPE_NFL_POST_SEASON, GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON});

                double pointsAllowedTotal = 0;
                double defensiveTouchdownsTotal = 0;
                double safetiesTotal = 0;
                double interceptionsTotal = 0;
                double fumbleRecoveriesTotal = 0;
                double blockedKicksTotal = 0;
                double sacksTotal = 0;

                int count = 0;
                for (StatsNflDefenseByEvent statsNflDefenseByEvent : statsNflDefenseByEvents) {
                    pointsAllowedTotal += statsNflDefenseByEvent.getPointsAllowed();
                    defensiveTouchdownsTotal += statsNflDefenseByEvent.getBlockedPuntOrFieldGoalReturnTouchdowns() + statsNflDefenseByEvent.getFumbleRecoveryTouchdowns() +
                            statsNflDefenseByEvent.getInterceptionReturnTouchdowns() + statsNflDefenseByEvent.getKickReturnTouchdowns() + statsNflDefenseByEvent.getPuntReturnTouchdowns();
                    safetiesTotal += statsNflDefenseByEvent.getSafeties();
                    interceptionsTotal += statsNflDefenseByEvent.getInterceptions();
                    fumbleRecoveriesTotal += statsNflDefenseByEvent.getFumbleRecoveries();
                    blockedKicksTotal += statsNflDefenseByEvent.getBlockedKicks();
                    sacksTotal += statsNflDefenseByEvent.getSacks();

                    count++;
                }

                data.put(GlobalConstants.STATS_NFL_POINTS_ALLOWED, count == 0 ? BigDecimal.ZERO : new BigDecimal(pointsAllowedTotal / count).setScale(2, RoundingMode.HALF_EVEN));
                data.put(GlobalConstants.STATS_NFL_DEFENSIVE_TOUCHDOWNS, count == 0 ? BigDecimal.ZERO : new BigDecimal(defensiveTouchdownsTotal / count).setScale(2, RoundingMode.HALF_EVEN));
                data.put(GlobalConstants.STATS_NFL_SAFETIES, count == 0 ? BigDecimal.ZERO : new BigDecimal(safetiesTotal / count).setScale(2, RoundingMode.HALF_EVEN));
                data.put(GlobalConstants.STATS_NFL_INTERCEPTIONS, count == 0 ? BigDecimal.ZERO : new BigDecimal(interceptionsTotal / count).setScale(2, RoundingMode.HALF_EVEN));
                data.put(GlobalConstants.STATS_NFL_FUMBLE_RECOVERIES, count == 0 ? BigDecimal.ZERO : new BigDecimal(fumbleRecoveriesTotal / count).setScale(2, RoundingMode.HALF_EVEN));
                data.put(GlobalConstants.STATS_NFL_BLOCKED_KICKS, count == 0 ? BigDecimal.ZERO : new BigDecimal(blockedKicksTotal / count).setScale(2, RoundingMode.HALF_EVEN));
                data.put(GlobalConstants.STATS_NFL_SACKS, count == 0 ? BigDecimal.ZERO : new BigDecimal(sacksTotal / count).setScale(2, RoundingMode.HALF_EVEN));
            }
            return data;
        }
        throw new IllegalArgumentException("Scoring rules for League " + athleteSportEventInfo.getSportEvent().getLeague().getAbbreviation() + " not implemented.");
    }

    @Override
    public Position findPosition(String abbreviation, Sport sport) {

        if (sport.equals(Sport.FOOTBALL)) {
            switch (abbreviation) {
                case "QB":
                    return Position.FB_QUARTERBACK;
                case "RB":
                    return Position.FB_RUNNINGBACK;
                case "WR":
                    return Position.FB_WIDE_RECEIVER;
                case "TE":
                    return Position.FB_TIGHT_END;
                case "K":
                    return Position.FB_KICKER;
            }
        } else if (sport.equals(Sport.BASEBALL)) {
            switch (abbreviation) {
                case "RP":
                case "SP":
                    return Position.BS_PITCHER;
                case "C":
                    return Position.BS_CATCHER;
                case "1B":
                    return Position.BS_FIRST_BASE;
                case "2B":
                    return Position.BS_SECOND_BASE;
                case "3B":
                    return Position.BS_THIRD_BASE;
                case "SS":
                    return Position.BS_SHORT_STOP;
                case "LF":
                case "CF":
                case "RF":
                    return Position.BS_OUTFIELD;
                //case "DH":
                //return Position.BS_DESIGNATED_HITTER;
            }
        }
        return null;
    }

    @Override
    public Team findTeam(int teamId) {
        return Ebean.find(Team.class).where().eq(Team.STAT_PROVIDER_ID, teamId).findUnique();
    }

    @Override
    public SportEvent findSportEvent(int id) {
        return Ebean.find(SportEvent.class).where().eq(SportEvent.STAT_PROVIDER_ID, id).findUnique();
    }

    @Override
    public SportEvent findNextFutureSportEvent(Athlete athlete, List<Integer> eventTypeIds) {
        ZonedDateTime season = timeService.getNowAsZonedDateTimeEST();
        Team team = athlete.getTeam();
        List<SportEvent> events;
        if (eventTypeIds.size() > 0) {
            events = Ebean.find(SportEvent.class).where().eq(SportEvent.LEAGUE_ID, team
                    .getLeague().getId()).eq("complete", false).in("eventTypeId", eventTypeIds)
                    .orderBy("start_time asc").findList();
        } else {
            events = Ebean.find(SportEvent.class).where().eq(SportEvent.LEAGUE_ID, team
                    .getLeague().getId()).eq("complete", false)
                    .orderBy("start_time asc").findList();
        }

        for (SportEvent event : events) {
            //if you want incomplete events and this event is in a prior season, it's complete
            if (event.getSeason() < season.getYear()) {
                continue;
            }
            if (event.getTeams().contains(team)) {
                return event;
            }
        }
        return null;
    }

    @Override
    public List<SportEvent> findSportEventsSorted(Team team, boolean complete) {
        ZonedDateTime season = timeService.getNowAsZonedDateTimeEST();
        List<SportEvent> events = Ebean.find(SportEvent.class).where().eq(SportEvent.LEAGUE_ID, team.getLeague()
                .getId()).eq("complete", complete).orderBy(SportEvent.START_TIME).findList();
        List<SportEvent> filteredList = new LinkedList<>();
        for (SportEvent event : events) {
            //if you want incomplete events and this event is in a prior season, it's complete
            if (!complete && (event.getSeason() < season.getYear())) {
                continue;
            }
            try {
                JSONObject desc = new JSONObject(event.getShortDescription());
                if (desc.getString("homeTeam").equalsIgnoreCase(team.getAbbreviation())
                        || desc.getString("awayTeam").equalsIgnoreCase(team.getAbbreviation())) {
                    filteredList.add(event);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return filteredList;
    }

    @Override
    public List<SportEvent> findSportEvents(League league, Date early, Date late) {
        return Ebean.find(SportEvent.class).where().eq(SportEvent.LEAGUE_ID, league.getId()).between("startTime", early, late).findList();
    }

    public List<SportEvent> findSportEventsInFuture(League league, Date date, Team team) {
        return Ebean.find(SportEvent.class).where().eq("league", league).ge("startTime", date).in("teams", Arrays.asList(team)).findList();
    }

    @Override
    public List<Position> findAllPositions() {
        return Ebean.find(Position.class).findList();
    }

    @Override
    public void updateGameScore(int[] gameScore, SportEvent sportEvent) {
        if (sportEvent == null) {
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>() {
        };

        try {
            Map<String, Object> data = mapper.readValue(sportEvent.getShortDescription(), typeReference);
            data.put("homeScore", gameScore[0]);
            data.put("awayScore", gameScore[1]);
            sportEvent.setShortDescription(mapper.writeValueAsString(data));
            save(sportEvent);
        } catch (IOException e) {
            Logger.error("Unable to parse the SportEvent short description for " + sportEvent.getId() + ": " + e.getMessage());
        }
    }

    private void forceInsert(Object object) {
        Ebean.getServer(default_ebean_server).insert(object);
    }

    @Override
    public Position findPosition(int positionId) {
        return Ebean.find(Position.class).where().eq(Position.POSITION_ID, positionId).findUnique();
    }

    @Override
    public League findLeague(String abbreviation) {
        return Ebean.find(League.class).where().eq(League.ABBREVIATION, abbreviation.toUpperCase()).findUnique();
    }

    @Override
    public List<SportEventGroupingType> findAllSportEventGroupingTypes() {
        return Ebean.find(SportEventGroupingType.class).findList();
    }

    @Override
    public void saveAthleteSalary(AthleteSalary salary) {
        save(salary);
    }

    private void validateParameters(SportEventGroupingType sportEventGroupingType,
                                    ZonedDateTime localDate) {
        if (sportEventGroupingType == null) {
            throw new IllegalArgumentException("SportEventGroupingType cannot be null");
        }

        if (sportEventGroupingType.getDateCriteria() == null
                || sportEventGroupingType.getLeague() == null) {
            throw new IllegalArgumentException("SportEventGroupingType members cannot be null" +
                    "\ndateCriteria = " + sportEventGroupingType.getDateCriteria()
                    + "\nleague = " + sportEventGroupingType.getLeague());
        }

        if (localDate == null) {
            throw new IllegalArgumentException("ZonedLocalDate cannot be null");
        }
    }

    @Override
    public BigDecimal calculateFantasyPointsPerGameNFL(IFantasyPointTranslator translator, ITimeService timeService, AthleteSportEventInfo athleteSportEventInfo,
                                                       int pastNGames, Map<String, BigDecimal> fppgCache) {
        String key = athleteSportEventInfo.getId() + "_" + pastNGames;
        BigDecimal cachedResult = fppgCache.get(key);
        if (cachedResult != null) {
            return cachedResult;
        }

        BigDecimal total = BigDecimal.ZERO;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        SportEvent sportEvent = athleteSportEventInfo.getSportEvent();
        Athlete athlete = athleteSportEventInfo.getAthlete();
        Integer season = Integer.parseInt(simpleDateFormat.format(sportEvent.getStartTime()));

        IStatsDao statsDao = DaoFactory.getStatsDao();

        /*
         * Defense
         */
        if (athlete.getPositions().get(0).equals(Position.FB_DEFENSE)) {
            Integer[] iSeasons = {GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON, GlobalConstants.EVENT_TYPE_NFL_POST_SEASON};
            List<StatsNflDefenseByEvent> statsNflDefenseByEvents = statsDao.findStatsNflDefenseByEvent(athlete, sportEvent.getStartTime(), pastNGames, iSeasons);

            if (!statsNflDefenseByEvents.isEmpty()) {
                for (StatsNflDefenseByEvent statsNflDefenseByEvent : statsNflDefenseByEvents) {
                    total = total.add(statsNflDefenseByEvent.getFppInThisEvent());
                }

                total = total.divide(new BigDecimal(statsNflDefenseByEvents.size()), RoundingMode.HALF_UP);
            }
        } else {
            Integer[] iSeasons = {GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON, GlobalConstants.EVENT_TYPE_NFL_POST_SEASON};
            List<StatsNflAthleteByEvent> statsNflAthleteByEvents = statsDao.findStatsNflAthleteByEvents(athlete, sportEvent.getStartTime(), pastNGames, iSeasons);

            if (!statsNflAthleteByEvents.isEmpty()) {
                for (StatsNflAthleteByEvent statsNflAthleteByEvent : statsNflAthleteByEvents) {
                    total = total.add(statsNflAthleteByEvent.getFppInThisEvent());
                }

                total = total.divide(new BigDecimal(statsNflAthleteByEvents.size()), RoundingMode.HALF_EVEN);
            }
        }

        total = total.setScale(2, RoundingMode.HALF_EVEN);
        fppgCache.put(key, total);
        return total;
    }

    /**
     * Calculate FPPG for MLB athlete.
     *
     * @param translator The fantasy point translator we want to use.
     * @return A BigDecimal representing the athlete's FPPG for the season.
     */
    @Override
    public BigDecimal calculateFantasyPointsPerGameMLB(IFantasyPointTranslator translator, ITimeService timeService, AthleteSportEventInfo athleteSportEventInfo,
                                                       int pastNGames, Map<String, BigDecimal> fppgCache) {
        String key = athleteSportEventInfo.getId() + "_" + pastNGames;
        BigDecimal cachedResult = fppgCache.get(key);
        if (cachedResult != null) {
            return cachedResult;
        }

        BigDecimal total = BigDecimal.ZERO;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        SportEvent sportEvent = athleteSportEventInfo.getSportEvent();
        Athlete athlete = athleteSportEventInfo.getAthlete();
        Integer season = Integer.parseInt(simpleDateFormat.format(sportEvent.getStartTime()));

        if (athlete.getPositions().get(0).getAbbreviation().equals(Position.BS_PITCHER.getAbbreviation())) {
            List<StatsMlbPitching> statsMlbPitchingList = DaoFactory.getStatsDao().findMlbPitchingStats(athlete.getStatProviderId(), season);
            if (!statsMlbPitchingList.isEmpty()) {
                for (StatsMlbPitching statsMlbPitching : statsMlbPitchingList) {
                    total = total.add(translator.calculateFantasyPoints(DaoFactory.getStatsDao().generateMlbPitchingMap(statsMlbPitching)));
                }

                total = total.divide(new BigDecimal(statsMlbPitchingList.size()), RoundingMode.HALF_EVEN);
            }
        } else {
            List<StatsMlbBatting> statsMlbBattingList = DaoFactory.getStatsDao().findMlbBattingStats(athlete.getStatProviderId(), season);
            if (!statsMlbBattingList.isEmpty()) {
                for (StatsMlbBatting statsMlbBatting : statsMlbBattingList) {
                    total = total.add(translator.calculateFantasyPoints(DaoFactory.getStatsDao().generateMlbBattingMap(statsMlbBatting)));
                }

                total = total.divide(new BigDecimal(statsMlbBattingList.size()), RoundingMode.HALF_EVEN);
            }
        }

        total = total.setScale(2, RoundingMode.HALF_EVEN);
        fppgCache.put(key, total);
        return total;
    }

    /**
     * Calculates the athlete's FPPG for the current season.
     *
     * @param translator            The fantasy point translator we want to use.
     * @param timeService           The implementation of time service to use.
     * @param athleteSportEventInfo The athlete for a given sport event to calculate the average for.
     * @param pastNGames            The number of previous games to consider in the calculation.
     * @return A BigDecimal representing the athlete's FPPG.
     */
    @Override
    public BigDecimal calculateFantasyPointsPerGame(IFantasyPointTranslator translator, ITimeService timeService, AthleteSportEventInfo athleteSportEventInfo,
                                                    int pastNGames) {
        Map<String, BigDecimal> fppgCache = DistributedServices.getInstance().getMap(GlobalConstants.ATHLETE_FPPG_MAP);

        SportEvent sportEvent = athleteSportEventInfo.getSportEvent();
        if (sportEvent.getLeague().getAbbreviation().equals(League.MLB.getAbbreviation())) {
            return calculateFantasyPointsPerGameMLB(translator, timeService, athleteSportEventInfo, pastNGames, fppgCache);
        } else if (sportEvent.getLeague().equals(League.NFL)) {
            return calculateFantasyPointsPerGameNFL(translator, timeService, athleteSportEventInfo, pastNGames, fppgCache);
        }

        return null;
    }

    @Override
    public Map<Integer, Integer> calculateExposure(User user) {
        String sql = "select ls.athlete_id, sum(entry_fee) as athlete_exposure " +
                "from lineup l inner join lineup_spot ls on l.id = ls.lineup_id inner join entry e on e.lineup_id = l.id " +
                "inner join contest c on c.id = e.contest_id where l.user_id = :user_id and c.contest_state_id in (:entry_locked, :roster_locked, :active) " +
                "group by ls.athlete_id";

        SqlQuery sqlQuery = Ebean.createSqlQuery(sql);
        sqlQuery.setParameter("user_id", user.getId());
        sqlQuery.setParameter("entry_locked", ContestState.locked.getId());
        sqlQuery.setParameter("roster_locked", ContestState.rosterLocked.getId());
        sqlQuery.setParameter("active", ContestState.active.getId());

        Map<Integer, Integer> results = new HashMap<>();
        List<SqlRow> list = sqlQuery.findList();
        for (SqlRow row : list) {
            int athleteId = row.getInteger("athlete_id");
            int athleteExposure = row.getInteger("athlete_exposure");

            results.put(athleteId, athleteExposure);
        }

        return results;
    }


    /**
     * Performs the actual rank calculation for MLB players.
     *
     * @param position   The position that the athlete is playing.
     * @param translator The concrete implementation of IFantasyPointTranslator to use.
     * @return An integer array with the first element being the athlete's rank, and the second being the total number of athletes.
     */
    @Override
    public int[] calculateRankMlb(Position position, IFantasyPointTranslator translator, Athlete athlete, int season, int pastNGames) {
        Comparator c = new Comparator<BigDecimal>() {
            @Override
            public int compare(BigDecimal o1, BigDecimal o2) {
                return (o1.compareTo(o2) == 1) ? -1 : (o1.compareTo(o2) == -1) ? 1 : 0;
            }
        };

        int[] result = {0, 0};

        BigDecimal myTotal = null;
        List<BigDecimal> totals = new ArrayList<>();

        if (athlete.getPositions().get(0).getAbbreviation().equals(Position.BS_PITCHER.getAbbreviation())) {

            Map<Integer, List<StatsMlbPitching>> statsMlbPitchingMap = DaoFactory.getStatsDao().findMlbPitchingStats(season);
            for (Map.Entry<Integer, List<StatsMlbPitching>> entry : statsMlbPitchingMap.entrySet()) {
                BigDecimal total = new BigDecimal(0);
                for (StatsMlbPitching p : entry.getValue()) {
                    total = total.add(translator.calculateFantasyPoints(DaoFactory.getStatsDao().generateMlbPitchingMap(p)));
                }

                if (!entry.getValue().isEmpty()) {
                    total = total.divide(new BigDecimal(entry.getValue().size()), RoundingMode.HALF_UP);
                }

                totals.add(total);
                if (entry.getKey() == athlete.getStatProviderId()) {
                    myTotal = total;
                }
            }
        } else {
            Map<Integer, List<StatsMlbBatting>> statsMlbBattingMap = DaoFactory.getStatsDao().findMlbBattingStatsAsMap(position, season);
            for (Map.Entry<Integer, List<StatsMlbBatting>> entry : statsMlbBattingMap.entrySet()) {
                BigDecimal total = new BigDecimal(0);
                for (StatsMlbBatting p : entry.getValue()) {
                    total = total.add(translator.calculateFantasyPoints(DaoFactory.getStatsDao().generateMlbBattingMap(p)));
                }

                if (!entry.getValue().isEmpty()) {
                    total = total.divide(new BigDecimal(entry.getValue().size()), RoundingMode.HALF_UP);
                }

                totals.add(total);
                if (entry.getKey() == athlete.getStatProviderId()) {
                    myTotal = total;
                }
            }
        }

        Collections.sort(totals, c);
        if (myTotal == null) {
            result[0] = totals.size();
        } else {
            result[0] = Collections.binarySearch(totals, myTotal, c) + 1;
        }
        result[1] = totals.size();

        return result;
    }

    @Override
    public int[] calculateRankNfl(Position position, IFantasyPointTranslator translator, AthleteSportEventInfo athleteSportEventInfo, Map<String, int[]> rankMap, int pastNGames) {
        String key = athleteSportEventInfo.getId() + "_" + athleteSportEventInfo.getSportEvent().getStartTime().getTime() + "_" + pastNGames;
        int[] cachedResult = rankMap.get(key);
        if (cachedResult != null) {
            return cachedResult;
        }

        Map<String, Map<Integer, Double>> rankCalculationsMap = DistributedServices.getInstance().getMap(GlobalConstants.NFL_ATHLETE_RANK_CALCULATIONS_MAP);

        /*
         * This comparator puts BigDecimals in order from largest to smallest.
         */
        Comparator c = new Comparator<BigDecimal>() {
            @Override
            public int compare(BigDecimal o1, BigDecimal o2) {
                return (o1.compareTo(o2) == 1) ? -1 : (o1.compareTo(o2) == -1) ? 1 : 0;
            }
        };

        int[] result = {0, 0};
        Integer[] eventIdTypes = {
                GlobalConstants.EVENT_TYPE_NFL_POST_SEASON,
                GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON
        };

        BigDecimal myTotal = null;
        List<BigDecimal> totals = new ArrayList<>();

        Athlete athlete = athleteSportEventInfo.getAthlete();

        /*
         * We keep a Map in cache to keep track of each athlete's total fantasy points, organized by position.
         *
         * The outer map is simply a container of maps keyed by position abbreviation.
         * Each inner map (value of the outer map) is a K/V pair of athlete ids and fantasy point totals for the 17-week window.
         */
        Map<Integer, Double> rankMapForPosition = rankCalculationsMap.get(position.getAbbreviation());
        if (rankMapForPosition == null) {
            rankMapForPosition = new HashMap<>();
            rankCalculationsMap.put(position.getAbbreviation(), rankMapForPosition);
        }

        if (!athlete.getPositions().get(0).equals(Position.FB_DEFENSE)) {
            if (rankMapForPosition.isEmpty()) {
                List<Athlete> athletes = findAthletes(athlete.getPositions().get(0), true);
                List<StatsNflAthleteByEvent> statsNflAthleteByEvents = new ArrayList<>();

                for (Athlete currAthlete : athletes) {
                    rankMapForPosition.put(currAthlete.getStatProviderId(), 0.0);
                    List<StatsNflAthleteByEvent> s = DaoFactory.getStatsDao().findStatsNflAthleteByEvents(currAthlete,
                            athleteSportEventInfo.getSportEvent().getStartTime(), pastNGames, eventIdTypes);
                    statsNflAthleteByEvents.addAll(s);
                }

                Map<Integer, List<StatsNflAthleteByEvent>> statsNflAthleteByEventMap = new HashMap<>();
                for (StatsNflAthleteByEvent statsNflAthleteByEvent : statsNflAthleteByEvents) {
                    List<StatsNflAthleteByEvent> l = statsNflAthleteByEventMap.get(statsNflAthleteByEvent.getAthlete().getStatProviderId());
                    if (l == null) {
                        l = new ArrayList<>();
                        statsNflAthleteByEventMap.put(statsNflAthleteByEvent.getAthlete().getStatProviderId(), l);
                    }

                    l.add(statsNflAthleteByEvent);
                }

                for (Map.Entry<Integer, List<StatsNflAthleteByEvent>> entry : statsNflAthleteByEventMap.entrySet()) {
                    BigDecimal total = BigDecimal.ZERO;
                    for (StatsNflAthleteByEvent p : entry.getValue()) {
                        total = total.add(p.getFppInThisEvent());
                    }

                    if (!entry.getValue().isEmpty()) {
                        total = total.divide(new BigDecimal(entry.getValue().size()), RoundingMode.HALF_UP);
                        rankMapForPosition.put(entry.getValue().get(0).getAthlete().getStatProviderId(), total.doubleValue());
                    }

                    totals.add(total);
                    if (entry.getKey() == athlete.getStatProviderId()) {
                        myTotal = total;
                    }
                }
                rankCalculationsMap.put(position.getAbbreviation(), rankMapForPosition);
            } else {
                for (Map.Entry<Integer, Double> entry : rankMapForPosition.entrySet()) {
                    totals.add(new BigDecimal(entry.getValue()));
                    if (entry.getKey() == athlete.getStatProviderId()) {
                        myTotal = new BigDecimal(entry.getValue());
                    }
                }
            }
        } else {
            if (rankMapForPosition.isEmpty()) {
                List<Team> teams = findTeams(League.NFL);
                List<StatsNflDefenseByEvent> statsNflDefenseByEvents = new ArrayList<>();

                for (Team team : teams) {
                    Athlete teamAthlete = DaoFactory.getSportsDao().findAthlete(team.getStatProviderId());
                    rankMapForPosition.put(teamAthlete.getStatProviderId(), 0.0);
                    List<StatsNflDefenseByEvent> statsNflDefenseByEventsForAthlete = DaoFactory.getStatsDao().findStatsNflDefenseByEvent(teamAthlete,
                            athleteSportEventInfo.getSportEvent().getStartTime(), pastNGames, eventIdTypes);
                    statsNflDefenseByEvents.addAll(statsNflDefenseByEventsForAthlete);
                }

                Map<Integer, List<StatsNflDefenseByEvent>> statsNflDefenseByEventMap = new HashMap<>();
                for (StatsNflDefenseByEvent statsNflDefenseByEvent : statsNflDefenseByEvents) {
                    List<StatsNflDefenseByEvent> l = statsNflDefenseByEventMap.get(statsNflDefenseByEvent.getAthlete().getStatProviderId());
                    if (l == null) {
                        l = new ArrayList<>();
                        statsNflDefenseByEventMap.put(statsNflDefenseByEvent.getAthlete().getStatProviderId(), l);
                    }

                    l.add(statsNflDefenseByEvent);
                }

                for (Map.Entry<Integer, List<StatsNflDefenseByEvent>> entry : statsNflDefenseByEventMap.entrySet()) {
                    BigDecimal total = BigDecimal.ZERO;
                    for (StatsNflDefenseByEvent p : entry.getValue()) {
                        total = total.add(p.getFppInThisEvent());
                    }

                    if (!entry.getValue().isEmpty()) {
                        total = total.divide(new BigDecimal(entry.getValue().size()), RoundingMode.HALF_UP);
                        rankMapForPosition.put(entry.getValue().get(0).getAthlete().getStatProviderId(), total.doubleValue());
                    }

                    totals.add(total);
                    if (entry.getKey() == athlete.getStatProviderId()) {
                        myTotal = total;
                    }
                }
                rankCalculationsMap.put(position.getAbbreviation(), rankMapForPosition);
            } else {
                for (Map.Entry<Integer, Double> entry : rankMapForPosition.entrySet()) {
                    totals.add(new BigDecimal(entry.getValue()));
                    if (entry.getKey() == athlete.getStatProviderId()) {
                        myTotal = new BigDecimal(entry.getValue());
                    }
                }
            }
        }

        Collections.sort(totals, c);
        if (myTotal == null) {
            result[0] = totals.size();
        } else {
            result[0] = Collections.binarySearch(totals, myTotal, c) + 1;
        }
        result[1] = totals.size();

        rankMap.put(key, result);
        return result;
    }

    /**
     * Determine the athlete's rank in terms of fantasy points among others at the specified position.
     *
     * @param position The position that the athlete is playing.
     * @return An integer array with the first element being the athlete's rank, and the second being the total number of athletes.
     */
    @Override
    public int[] calculateRank(Position position, IFantasyPointTranslator translator, AthleteSportEventInfo athleteSportEventInfo, int season, League league, int pastNGames) {
        if (league.equals(League.MLB)) {
            return calculateRankMlb(position, translator, athleteSportEventInfo.getAthlete(), season, pastNGames);
        } else if (league.equals(League.NFL)) {
            Map<String, int[]> rankMap = DistributedServices.getInstance().getMap(GlobalConstants.NFL_ATHLETE_RANK_MAP + "_" + pastNGames);
            return calculateRankNfl(position, translator, athleteSportEventInfo, rankMap, pastNGames);
        }

        return null;
    }

    /**
     * Finds a list of AthleteSportEventInfo objects associated with the lineup containing the provided id.
     *
     * @param lineup The id of the lineup we want to find AthleteSportEventInfo objects for.
     * @return A list of relevant AthleteSportEventInfo objects.
     */
    @Override
    public List<AthleteSportEventInfo> findAthleteSportEventInfos(Lineup lineup) {
        String sql = "select a.id, a.sport_event_id, a.athlete_id, a.fantasy_points, a.stats, a.timeline " +
                "from athlete_sport_event_info a inner join lineup_spot ls on a.id = ls.athlete_sport_event_info_id " +
                "inner join lineup l on ls.lineup_id = l.id " +
                "where l.id = " + lineup.getId();

        RawSql rawSql = RawSqlBuilder.parse(sql)
                .columnMapping("a.id", "id")
                .columnMapping("a.sport_event_id", "sportEvent.id")
                .columnMapping("a.athlete_id", "athlete.id")
                .columnMapping("a.fantasy_points", "fantasyPoints")
                .columnMapping("a.stats", "stats")
                .columnMapping("a.timeline", "timeline")
                .create();

        com.avaje.ebean.Query<AthleteSportEventInfo> query = Ebean.find(AthleteSportEventInfo.class);
        query.setRawSql(rawSql);
        return query.findList();
    }

    /**
     * Convenience method for retrieving all AthleteSportEventInfo objects for a contest and position.
     *
     * @param contest  The contest we want AthleteSportEventInfo objects for.
     * @param position The position we're interested in.
     * @return A list of AthleteSportEventInfo objects for athletes on lineups in the contest at the specified position.
     */
    @Override
    public List<AthleteSportEventInfo> findAthleteSportEventInfos(Contest contest, Position position, List<Lineup> lineups) {
        String sql = "select distinct a.id, a.sport_event_id, a.athlete_id, a.fantasy_points, a.stats, a.timeline " +
                "from athlete_sport_event_info a " +
                "inner join sport_event se on se.id = a.sport_event_id " +
                "inner join sport_event_grouping_x_sport_event segx on a.sport_event_id = segx.sport_event_id " +
                "inner join sport_event_grouping seg on seg.id = segx.sport_event_grouping_id " +
                "inner join contest c on c.sport_event_grouping_id = seg.id " +
                "inner join entry e on e.contest_id = c.id " +
                "inner join lineup_spot ls on ls.lineup_id = e.lineup_id " +
                "where ls.athlete_sport_event_info_id = a.id and c.id = " + contest.getId() + " and ls.position_id = " + position.getId();
        if (lineups != null && lineups.size() > 0) {
            sql += " and ls.lineup_id in (";
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (Lineup lineup : lineups) {
                if (first) {
                    sb.append(lineup.getId());
                    first = false;
                } else {
                    sb.append(",").append(lineup.getId());
                }
            }
            sql += sb.toString() + ") ";
        }
        sql += " order by a.fantasy_points desc";

        RawSql rawSql = RawSqlBuilder.parse(sql)
                .columnMapping("a.id", "id")
                .columnMapping("a.sport_event_id", "sportEvent.id")
                .columnMapping("a.athlete_id", "athlete.id")
                .columnMapping("a.fantasy_points", "fantasyPoints")
                .columnMapping("a.stats", "stats")
                .columnMapping("a.timeline", "timeline")
                .create();

        com.avaje.ebean.Query<AthleteSportEventInfo> query = Ebean.find(AthleteSportEventInfo.class);
        query.setRawSql(rawSql);
        return query.findList();
    }

    /**
     * Convenience method for retrieving all AthleteSportEventInfo objects for a contest.
     *
     * @param contest The contest we want AthleteSportEventInfo objects for.
     * @return
     */
    @Override
    public List<AthleteSportEventInfo> findAthleteSportEventInfos(Contest contest) {
        String sql = "select a.id, a.sport_event_id, a.athlete_id, a.fantasy_points, a.stats, a.timeline " +
                "from athlete_sport_event_info a " +
                "inner join sport_event se on se.id = a.sport_event_id " +
                "inner join sport_event_grouping_x_sport_event segx on a.sport_event_id = segx.sport_event_id " +
                "inner join sport_event_grouping seg on seg.id = segx.sport_event_grouping_id " +
                "inner join contest c on c.sport_event_grouping_id = seg.id " +
                "where c.id = " + contest.getId();

        RawSql rawSql = RawSqlBuilder.parse(sql)
                .columnMapping("a.id", "id")
                .columnMapping("a.sport_event_id", "sportEvent.id")
                .columnMapping("a.athlete_id", "athlete.id")
                .columnMapping("a.fantasy_points", "fantasyPoints")
                .columnMapping("a.stats", "stats")
                .columnMapping("a.timeline", "timeline")
                .create();

        com.avaje.ebean.Query<AthleteSportEventInfo> query = Ebean.find(AthleteSportEventInfo.class);
        query.setRawSql(rawSql);
        return query.findList();
    }

    /**
     * Convenience method to fetch AthleteSportEventInfo objects by the associated athlete and date of the SportEvent.
     *
     * @param athlete        The athlete whose AthleteSportEventInfo objects we want.
     * @param sportEventDate The SportEvent date whose AthleteSportEventInfo objects we want.
     * @return The AthleteSportEventInfo object that represents the provided athlete in the provided SportEvent date.
     */
//    @Override
//    public AthleteSportEventInfo findAthleteSportEventInfo(Athlete athlete, Date sportEventDate) {
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(sportEventDate);
//
//        cal.set(Calendar.HOUR_OF_DAY, 0);
//        Date start = cal.getTime();
//
//        cal.set(Calendar.HOUR_OF_DAY, 23);
//        Date end = cal.getTime();
//
//        List<SportEvent> sportEvents = Ebean.find(SportEvent.class)
//                .where().between("startTime", start, end).findList();
//        for (SportEvent sportEvent : sportEvents) {
//            if (sportEvent.getTeams().get(0).getStatProviderId() == athlete.getTeam().getStatProviderId() ||
//                    sportEvent.getTeams().get(1).getStatProviderId() == athlete.getTeam().getStatProviderId()) {
//                return Ebean.find(AthleteSportEventInfo.class)
//                        .where().eq("sportEvent", sportEvent).eq("athlete", athlete).findUnique();
//            }
//        }
//
//        return null;
//    }

    /**
     * Convenience method to fetch AthleteSportEventInfo objects by the associated athlete and SportEvent.
     *
     * @param athlete    The athlete whose AthleteSportEventInfo objects we want.
     * @param sportEvent The SportEvent whose AthleteSportEventInfo objects we want.
     * @return The AthleteSportEventInfo object that represents the provided athlete in the provided SportEvent.
     */
    @Override
    public AthleteSportEventInfo findAthleteSportEventInfo(Athlete athlete, SportEvent sportEvent) {
        AthleteSportEventInfo athleteSportEventInfo = Ebean.find(AthleteSportEventInfo.class)
                .where().eq("sportEvent", sportEvent)
                .eq("athlete", athlete).findUnique();
        if (athleteSportEventInfo == null) {
            throw new NotFoundException(
                    String.format("No combination of %s in %s found",
                            athlete.getFirstName() + " " + athlete.getLastName(),
                            sportEvent.getShortDescription()));
        }
        return athleteSportEventInfo;
    }

    /**
     * Convenience method to fetch AthleteSportEventInfo objects by the associated SportEvent.
     *
     * @param sportEvent The SportEvent whose AthleteSportEventInfo objects we want.
     * @return The list of AthleteSportEventInfo objects for the provided SportEvent.
     */
    @Override
    public List<AthleteSportEventInfo> findAthleteSportEventInfos(SportEvent sportEvent) {
        return Ebean.find(AthleteSportEventInfo.class).where().eq("sportEvent", sportEvent).findList();
    }

    /**
     * Convenience method to fetch AthleteSportEventInfo objects by the associated athlete.
     *
     * @param athlete The athlete whose AthleteSportEventInfo objects we want.
     * @return The list of AthleteSportEventInfo objects for the provided athlete.
     */
    @Override
    public List<AthleteSportEventInfo> findAthleteSportEventInfos(Athlete athlete) {
        return Ebean.find(AthleteSportEventInfo.class).where().eq("athlete", athlete).findList();
    }

    /**
     * Convenience method to fetch AthleteSportEventInfo objects by their id.
     *
     * @param id The id of the AthleteSportEventInfo object to fetch.
     * @return The AthleteSportEventInfo associated with the provided id.
     */
    @Override
    public AthleteSportEventInfo findAthleteSportEventInfo(int id) {
        return Ebean.find(AthleteSportEventInfo.class).where().eq("id", id).findUnique();
    }

    @Override
    public Athlete findAthlete(int statProviderId) {
        return Ebean.find(Athlete.class).where().eq("statProviderId", statProviderId).findUnique();
    }

    @Override
    public List<Athlete> findAthletes(Team team) {
        return Ebean.find(Athlete.class).where().eq(Athlete.TEAM_ID, team.getId()).findList();
    }

    @Override
    public List<League> findActiveLeagues() {
        return Ebean.find(League.class).where().eq(League.IS_ACTIVE, true).findList();
    }

    @Override
    public List<Team> findTeams(League league) {
        return Ebean.find(Team.class).where().eq(Team.LEAGUE_ID, league.getId()).findList();
    }

    @Override
    public AthleteSalary findAthleteSalary(Athlete athlete, SportEventGrouping grouping) {
        return Ebean.find(AthleteSalary.class).where()
                .eq(AthleteSalary.ATHLETE_ID, athlete.getId())
                .eq(AthleteSalary.SPORT_EVENT_GROUP_ID, grouping.getId())
                .findUnique();
    }

    @Override
    public List<AthleteSalary> findAthleteSalariesSorted(Athlete athlete, SportEventGroupingType sportEventGroupingType, int limit, String sortDirection) {
        return Ebean.find(AthleteSalary.class).fetch("sportEventGrouping")
                .where()
                .eq("athlete", athlete)
                .eq("sportEventGrouping.sportEventGroupingType", sportEventGroupingType)
                .lt("sportEventGrouping.eventDate", new Date())
                .order("sportEventGrouping.eventDate " + sortDirection)
                .setMaxRows(limit)
                .findList();
    }

    @Override
    public int calculateAverageDollarsPerFantasyPoint(Athlete athlete, Date startTime, int pastNGames) {
        String key = athlete.getId() + "_" + startTime.getTime() + "_" + pastNGames;
        Map<String, Integer> dollarsPerPointMap = DistributedServices.getInstance().getMap(GlobalConstants.ATHLETE_DOLLARS_PER_POINT_MAP);
        Integer result = dollarsPerPointMap.get(key);
        if (result != null) {
            return result;
        }

        SportEventGroupingType sportEventGroupingType = null;
        if (athlete.getTeam().getLeague().equals(League.NFL)) {
            List<SportEventGroupingType> types = findAllSportEventGroupingTypes();
            for (SportEventGroupingType type : types) {
                if (type.getName().equals("NFL Full")) {
                    sportEventGroupingType = type;
                    break;
                }
            }
        }

        String sql = "select avg(salary/fantasy_points) as dollars_per_point " +
                "from athlete_salary a inner join sport_event_grouping s on a.sport_event_grouping_id = s.id " +
                "inner join athlete_sport_event_info asei on a.athlete_id = asei.athlete_id " +
                "where a.athlete_id = :athlete_id and s.sport_event_grouping_type_id = :sport_event_grouping_type_id and s.event_date < now() order by s.event_date desc";

        SqlQuery sqlQuery = Ebean.createSqlQuery(sql);
        sqlQuery.setParameter("athlete_id", athlete.getId());
        sqlQuery.setParameter("sport_event_grouping_type_id", sportEventGroupingType.getId());

        List<SqlRow> list = sqlQuery.findList();
        Double dollarsPerPoint = 0.0;
        for (SqlRow row : list) {
            dollarsPerPoint = row.getDouble("dollars_per_point");
        }

        result = dollarsPerPoint == null ? 0 : dollarsPerPoint.intValue();
        dollarsPerPointMap.put(key, result);
        return result;
    }

    @Override
    public String createInitialJsonForAthleteBoxscore(Position position) {
        String BOXSCORE_JSON_FIELD_NAME = "name";
        String BOXSCORE_JSON_FIELD_AMOUNT = "amount";
        String BOXSCORE_JSON_FIELD_FPP = "fpp";
        String BOXSCORE_JSON_FIELD_ABBR = "abbr";
        String BOXSCORE_JSON_FIELD_ID = "id";

        ObjectMapper mapper = new ObjectMapper();

        BigDecimal zero = new BigDecimal(0);
        List<Map<String, Object>> boxScore = new ArrayList<>();

        if (position.getSport().equals(Sport.BASEBALL)) {
            ArrayList<String> statsLabels;
            if (position.equals(Position.BS_PITCHER)) {
                statsLabels = new ArrayList<>(Arrays.asList(GlobalConstants.STATS_ARRAY_FOR_MLB_PITCHER));
            } else {
                statsLabels = new ArrayList<>(Arrays.asList(GlobalConstants.STATS_ARRAY_FOR_MLB_BATTER));
            }

            for (String label : statsLabels) {
                Map<String, Object> json = new HashMap<>();
                json.put(BOXSCORE_JSON_FIELD_NAME, label);
                json.put(BOXSCORE_JSON_FIELD_ABBR, GlobalConstants.SCORING_MLB_NAME_TO_ABBR_MAP.get(label));
                json.put(BOXSCORE_JSON_FIELD_AMOUNT, zero);
                json.put(BOXSCORE_JSON_FIELD_FPP, zero);

                boxScore.add(json);
            }
        } else if (position.getSport().equals(Sport.FOOTBALL)) {
            ArrayList<String> statsLabels;
            if (position.equals(Position.FB_DEFENSE)) {
                statsLabels = new ArrayList<>(Arrays.asList(GlobalConstants.STATS_ARRAY_FOR_NFL_DEFENSE));
            } else {
                statsLabels = new ArrayList<>(Arrays.asList(GlobalConstants.STATS_ARRAY_FOR_NFL_OFFENSE));
            }

            for (String label : statsLabels) {
                Map<String, Object> json = new HashMap<>();
                json.put(BOXSCORE_JSON_FIELD_NAME, label);
                json.put(BOXSCORE_JSON_FIELD_ABBR, GlobalConstants.SCORING_NFL_NAME_TO_ABBR_MAP.get(label));
                json.put(BOXSCORE_JSON_FIELD_ID, GlobalConstants.SCORING_NFL_NAME_TO_ID_MAP.get(label));
                json.put(BOXSCORE_JSON_FIELD_AMOUNT, zero);
                json.put(BOXSCORE_JSON_FIELD_FPP, zero);

                boxScore.add(json);
            }
        }

        try {
            return mapper.writeValueAsString(boxScore);
        } catch (JsonProcessingException e) {
            Logger.error("Unable to initialize boxscore for AthleteSportEventInfo: " + e.getMessage());
            return "[]";
        }
    }

    @Override
    public void saveAthletes(List<Athlete> athletes) {
        Ebean.save(athletes);
    }

    @Override
    public void updateAthletes(List<Athlete> athletes) {
        Ebean.update(athletes);
    }

    @Override
    public Athlete findDefense(Team team) {
        return Ebean.find(Athlete.class).where().eq("uniform", "DEF")
                .eq("last_name", team.getName())
                .eq("uniform", "DEF")
                .findUnique();
    }

    @Override
    public AthleteSportEventInfo findPreviousAthleteSportEventInfo(AthleteSportEventInfo athleteSportEventInfo) {
        return Ebean.find(AthleteSportEventInfo.class).where()
                .eq("athlete", athleteSportEventInfo.getAthlete())
                .lt("sportEvent.startTime", athleteSportEventInfo.getSportEvent().getStartTime()).order("sportEvent.startTime desc").setMaxRows(1).findUnique();
    }

}
