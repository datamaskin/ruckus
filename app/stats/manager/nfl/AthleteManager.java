package stats.manager.nfl;

import com.avaje.ebean.Ebean;
import common.GlobalConstants;
import dao.DaoFactory;
import models.sports.*;
import models.stats.StatsAthleteBySeasonRaw;
import models.stats.nfl.StatsNflProjection;
import play.Logger;
import stats.manager.StatsEventManager;
import stats.parser.AthleteStatsParser;
import stats.parser.nfl.AthleteDataset;
import stats.parser.nfl.AthleteParser;
import stats.parser.nfl.DefenseParser;
import stats.provider.nfl.StatsIncProviderNFL;
import stats.retriever.ITeamRetriever;
import stats.retriever.TeamRetriever;
import utils.ITimeService;
import utils.TimeService;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mgiles on 7/22/14.
 */
public class AthleteManager extends StatsEventManager {
    private static final ITimeService timeService = new TimeService();

    public void process() throws Exception {
        AthleteParser athleteParser = new AthleteParser();
        DefenseParser defenseParser = new DefenseParser();

        List<Athlete> athletes = getAthletes();

        for (Athlete a : athletes) {
            Athlete athlete = DaoFactory.getSportsDao().findAthlete(a.getStatProviderId());
            if (athlete == null) {
                athlete = a;
                DaoFactory.getSportsDao().saveAthlete(athlete);
            }
            // skip kickers. Mitch says they suck
            if (athlete.getPositions().contains(Position.FB_KICKER)) {
                continue;
            }
            AthleteDataset dataset = new AthleteDataset();

            int failed = 0;
            int year = LocalDate.now().getYear();
            for (int i = GlobalConstants.YEARS_BACK; i >= 0; i--) {
                defenseParser.resetDataset();

                //TODO: should only be running regular and post season stats??
                int[] eventTypes = {GlobalConstants.EVENT_TYPE_NFL_PRE_SEASON,
                        GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON,
                        GlobalConstants.EVENT_TYPE_NFL_POST_SEASON};
                for (int eventType : eventTypes) {
                    ZonedDateTime season = timeService.getNowAsZonedDateTimeEST().withYear(year - i);
                    Map<String, String> map = new HashMap<>();
                    if (athlete.getPositions().contains(Position.FB_DEFENSE)) {
                        map.put(GlobalConstants.STATS_INC_KEY_RESOURCE,
                                String.format("stats/teams/%d/events", athlete.getStatProviderId()));
                    } else {
                        map.put(GlobalConstants.STATS_INC_KEY_RESOURCE,
                                String.format("stats/players/%d/events", athlete.getStatProviderId()));
                    }
                    map.put("season", String.valueOf(season.getYear()));
                    map.put("eventTypeId", String.valueOf(eventType));

                    Logger.info("Updating stats for " + athlete.getFirstName() + " "
                            + athlete.getLastName() + "(" + athlete.getStatProviderId() + ") in year "
                            + String.valueOf(season.getYear()));

                    StatsAthleteBySeasonRaw raw;
                    try {
                        String results;
                        //see if we have already tried to get it once
                        raw = DaoFactory.getStatsDao().findStatsAthleteSeasonRaw(athlete, season.getYear(), eventType);
                        if (raw != null) {
                            // if it previously failed in a year prior to the current one, skip it
                            if (raw.isPreviouslyFailed() && raw.getSeason() != timeService.getNowAsZonedDateTimeEST().getYear()) {
                                Logger.debug("No cached data available for " + athlete.getFirstName() + " "
                                        + athlete.getLastName() + "(" + athlete.getStatProviderId() + ") in year "
                                        + String.valueOf(season.getYear()));
                                failed++;
                                continue;
                            } else {
                                // if it is this season we want to update it from stats if we haven't already in the last 23 hours
                                Timestamp last23 = Timestamp.from(timeService.getNow().minus(Duration.ofHours(23)));
                                if (raw.getSeason() == timeService.getNowAsZonedDateTimeEST().getYear()
                                        && raw.getLastUpdate().before(last23)) {
                                    Logger.debug("Trying to update from STATS for " + athlete.getFirstName() + " "
                                            + athlete.getLastName() + "(" + athlete.getStatProviderId() + ") in year "
                                            + String.valueOf(season.getYear()));
                                    results = new StatsIncProviderNFL().getStats(map);
                                    raw.setRawData(results);
                                    raw.setLastFetched(Timestamp.from(timeService.getNow()));
                                    raw.setUniqueKey(athlete.getStatProviderId() + "_" + season.getYear() + "_" + eventType);
                                    DaoFactory.getStatsDao().saveStatsAthleteSeasonRaw(raw);
                                } else {
                                    // otherwise just used the cached version
                                    Logger.debug("Using cached data for " + athlete.getFirstName() + " "
                                            + athlete.getLastName() + "(" + athlete.getStatProviderId() + ") in year "
                                            + String.valueOf(season.getYear()));
                                    results = raw.getRawData() == null || raw.getRawData().equals("") ? null : raw.getRawData();
                                    if (results == null) {
                                        failed++;
                                        if (failed >= eventTypes.length * (GlobalConstants.YEARS_BACK + 1) && athlete.getTeam() != null) {
                                            handleFailedAll(athlete, year - 1);
                                        }
                                        continue;
                                    }
                                }
                            }
                        } else {
                            Logger.info("Making an initial STATS retrieval for " + athlete.getFirstName() + " "
                                    + athlete.getLastName() + "(" + athlete.getStatProviderId() + ") in year "
                                    + String.valueOf(season.getYear()));
                            results = new StatsIncProviderNFL().getStats(map);
                            raw = DaoFactory.getStatsDao().findStatsAthleteSeasonRaw(athlete, season.getYear(), eventType);
                            if (raw == null) {
                                raw = new StatsAthleteBySeasonRaw();
                            }
                            raw.setStatsAthleteId(athlete.getStatProviderId());
                            raw.setPreviouslyFailed(false);
                            raw.setSeason(season.getYear());
                            raw.setEventTypeId(eventType);
                            raw.setRawData(results);
                            raw.setUniqueKey(athlete.getStatProviderId() + "_" + season.getYear() + "_" + eventType);
                            DaoFactory.getStatsDao().saveStatsAthleteSeasonRaw(raw);
                        }

                        if (athlete.getPositions().contains(Position.FB_DEFENSE)) {
                            defenseParser.parse(results);
                        } else {
                            athleteParser.parse(results, dataset);
                        }

                        Logger.info("Processed NFL stats for " + athlete.getFirstName() + " "
                                + athlete.getLastName() + "(" + athlete.getStatProviderId() + ") in year "
                                + String.valueOf(year - i));
                    } catch (Exception e) {
                        if (e.getMessage() != null && e.getMessage().contains("Unexpected response status: 404")) {
                            raw = DaoFactory.getStatsDao().findStatsAthleteSeasonRaw(athlete, season.getYear(), eventType);
                            if (raw == null) {
                                raw = new StatsAthleteBySeasonRaw();
                            }
                            raw.setStatsAthleteId(athlete.getStatProviderId());
                            raw.setSeason(season.getYear());
                            raw.setEventTypeId(eventType);
                            raw.setPreviouslyFailed(true);
                            raw.setUniqueKey(athlete.getStatProviderId() + "_" + season.getYear() + "_" + eventType);
                            raw.setLastFetched(Timestamp.from(timeService.getNow()));
                            DaoFactory.getStatsDao().saveStatsAthleteSeasonRaw(raw);

                            Logger.warn("(404) No STATS data available for " + athlete.getFirstName() + " "
                                    + athlete.getLastName() + "(" + athlete.getStatProviderId() + ") in year "
                                    + String.valueOf(season.getYear()));
                            failed++;
                            if (failed >= eventTypes.length * (GlobalConstants.YEARS_BACK + 1) && athlete.getTeam() != null) {
                                handleFailedAll(athlete, year - 1);
                            }
                        } else {
                            Logger.error("Unable to process stat update " +
                                    "for " + athlete.getFirstName() + " "
                                    + athlete.getLastName() + "(" + athlete.getStatProviderId() + "): "
                                    + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private void handleFailedAll(Athlete athlete, int year) {
        SportEvent sportEvent = DaoFactory.getSportsDao().findNextFutureSportEvent(athlete, new ArrayList<Integer>());
        if (sportEvent != null) {
            StatsNflProjection prediction = DaoFactory.getStatsDao()
                    .findNflPrediction(sportEvent, athlete);
            if (prediction == null) {
                prediction = new StatsNflProjection();
                prediction.setAthlete(athlete);
                prediction.setUniqueKey(athlete.getStatProviderId() + "_" + sportEvent.getStatProviderId());
                prediction.setSportEvent(sportEvent);
                prediction.setStartTime(sportEvent.getStartTime());
                prediction.setStatsAthleteId(athlete.getStatProviderId());
                prediction.setPosition(athlete.getPositions().get(0).getAbbreviation());
                prediction.setSeason(sportEvent.getSeason());
                prediction.setWeek(sportEvent.getWeek());
                try {
                    Ebean.save(prediction);
                } catch (Exception e) {
                    Logger.warn(e.getMessage());
                }
                Logger.info("Saving stub prediction for " + athlete.getFirstName() + " "
                        + athlete.getLastName() + "(" + athlete.getStatProviderId() + ") in year "
                        + String.valueOf(year));
            }
        }
    }

    private List<Athlete> getAthletes() throws Exception {
        List<Athlete> athletes = new ArrayList<>();

        ITeamRetriever teamRetriever = new TeamRetriever();
        List<Team> teams = teamRetriever.getAllTeamsInLeague(League.NFL);
        for (Team team : teams) {
            Athlete defense = DaoFactory.getSportsDao().findAthlete(team.getStatProviderId());
            if (defense != null) {
                athletes.add(defense);
            }
        }

        ZonedDateTime now = timeService.getNowAsZonedDateTimeEST();
        ZonedDateTime season = now.minusYears(GlobalConstants.YEARS_BACK);
        Map<String, String> map1 = new HashMap<>();
        map1.put(GlobalConstants.STATS_INC_KEY_RESOURCE, "participants");
        map1.put("sinceYearLast", String.valueOf(season.getYear()));
        String results1 = new StatsIncProviderNFL().getStats(map1);
        athletes.addAll(new AthleteStatsParser().parse(results1));

        return athletes;
    }
}
