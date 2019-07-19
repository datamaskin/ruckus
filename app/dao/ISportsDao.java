package dao;

import models.contest.Contest;
import models.contest.Lineup;
import models.sports.*;
import models.stats.StatsLiveFeedData;
import models.user.User;
import stats.translator.IFantasyPointTranslator;
import utils.ITimeService;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ISportsDao {

    /**
     * Initialize this DAO
     */
    public void init();

    // Save, update, and delete methods where hooks can be added for dependent actions /////////////////////////////////

    /**
     * Saves an Team to the DB
     *
     * @param team
     */
    public void saveTeam(Team team);

    /**
     * Updates a Team to the DB
     *
     * @param team
     */
    public void updateTeam(Team team);

    /**
     * Saves an LiveFeedData to the DB
     *
     * @param statsLiveFeedData
     */
    public void saveLiveFeedData(StatsLiveFeedData statsLiveFeedData);

    /**
     * Updates a LiveFeedData to the DB
     *
     * @param statsLiveFeedData
     */
    public void updateLiveFeedData(StatsLiveFeedData statsLiveFeedData);

    /**
     * Saves an AthleteSportEventInfo to the DB
     *
     * @param athleteSportEventInfo
     */
    public void saveAthleteSportEventInfo(AthleteSportEventInfo athleteSportEventInfo);

    /**
     * Updates an AthleteSportEventInfo to the DB
     *
     * @param athleteSportEventInfo
     */
    public void updateAthleteSportEventInfo(AthleteSportEventInfo athleteSportEventInfo);

    /**
     * Saves an SportEvent to the DB
     *
     * @param sportEvent
     */
    public void saveSportEvent(SportEvent sportEvent);

    /**
     * Updates an SportEvent to the DB
     *
     * @param sportEvent
     */
    public void updateSportEvent(SportEvent sportEvent);

    /**
     * Saves an Athlete to the DB
     *
     * @param athlete
     */
    public void saveAthlete(Athlete athlete);

    /**
     * Updates an Athlete to the DB
     *
     * @param athlete
     */
    public void updateAthlete(Athlete athlete);

    /**
     * Saves Position to the database
     *
     * @param position
     */
    public void savePosition(Position position);

    // find helpers ///////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @param sportEventGroupingType
     * @param localDate
     * @return List of SportEvents by SportEventGroupingType and ZonedDateTime
     */
    public List<SportEvent> findSportEvents(
            SportEventGroupingType sportEventGroupingType,
            ZonedDateTime localDate);

    /**
     * @param contest
     * @return List of SportEvents by Contest
     */
    public List<SportEvent> findSportEvents(Contest contest);

    /**
     * @param id
     * @return SportEvent by stat provider id
     */
    public SportEvent findSportEvent(int id);

    /**
     * @param league
     * @param early
     * @param late
     * @return List of SportEvents by League and date range
     */
    public List<SportEvent> findSportEvents(League league, Date early, Date late);

    /**
     * Finds sport events in a league for the provided team that are in the future.
     *
     * @param league
     * @param date
     * @param team
     * @return
     */
    List<SportEvent> findSportEventsInFuture(League league, Date date, Team team);

    /**
     * @param sportEvent
     * @return List of Athletes by SportEvent
     */
    public List<Athlete> findAthletes(SportEvent sportEvent);

    /**
     * @param league
     * @return List of Athletes by League
     */
    public List<Athlete> findAthletes(League league);

    /**
     * Finds all athletes that play the provided position.
     *
     * @param position
     * @param active
     * @return
     */
    public List<Athlete> findAthletes(Position position, boolean active);

    /**
     * @param positionId
     * @return Position by ID
     */
    public Position findPosition(int positionId);

    /**
     * @param abbreviation
     * @param sport
     * @return Position by abbreviation and sport
     */
    public Position findPosition(String abbreviation, Sport sport);

    /**
     * @return List of all Positions
     */
    public List<Position> findAllPositions();

    /**
     * @param teamId
     * @return Team by stats provider id
     */
    public Team findTeam(int teamId);

    /**
     * @return List of all SportEventGroupingTypes
     */
    public List<SportEventGroupingType> findAllSportEventGroupingTypes();

    /**
     * @param abbreviation
     * @return League by abbreviation
     */
    public League findLeague(String abbreviation);

    /**
     * Update the current scores in a specific SportEvent
     *
     * @param gameScore
     * @param sportEvent
     */
    public void updateGameScore(int[] gameScore, SportEvent sportEvent);

    /**
     * @param athelteSportEventInfo
     * @param pastNGames
     * @return A Map of name/value pairs, where the name is a string of the stat name, and the value is the average.
     */
    public Map<String, BigDecimal> calculateStatAverages(AthleteSportEventInfo athelteSportEventInfo, int pastNGames);

    /**
     * Retrieve an AthleteSalary based on an Athlete and SportEventGrouping.
     *
     * @param athlete            The Athlete whose salary we're interested in.
     * @param sportEventGrouping The SportEventGrouping we're interested in.
     * @return The AthleteSalary object satisfying both parameters.
     */
    AthleteSalary findAthleteSalary(Athlete athlete, SportEventGrouping sportEventGrouping);

    /**
     * Retrieve a list of AthleteSalary objects based on SportEventGroupingType.
     *
     * @param athlete                   The Athlete whose salary we're interested in.
     * @param sportEventGroupingType    The SportEventGroupingType to use.
     * @param limit                     The maximum number of rows to return.
     * @param sortDirection             The sort direction - asc or desc
     * @return                          A list of AthleteSalary objects.
     */
    List<AthleteSalary> findAthleteSalariesSorted(Athlete athlete, SportEventGroupingType sportEventGroupingType, int limit, String sortDirection);

    int calculateAverageDollarsPerFantasyPoint(Athlete athlete, Date startTime, int pastNGames);

//    public AthleteSportEventInfo findAthleteSportEventInfo(Athlete athlete, Date sportEventDate);

    public AthleteSportEventInfo findAthleteSportEventInfo(Athlete athlete, SportEvent sportEvent);

    public List<AthleteSportEventInfo> findAthleteSportEventInfos(SportEvent sportEvent);

    public List<AthleteSportEventInfo> findAthleteSportEventInfos(Athlete athlete);

    public AthleteSportEventInfo findAthleteSportEventInfo(int id);

    public List<AthleteSportEventInfo> findAthleteSportEventInfos(Contest contest);

    public List<AthleteSportEventInfo> findAthleteSportEventInfos(Contest contest, Position position, List<Lineup> lineups);

    public List<AthleteSportEventInfo> findAthleteSportEventInfos(Lineup lineup);

    public int[] calculateRank(Position position, IFantasyPointTranslator translator, AthleteSportEventInfo athleteSportEventInfo, int season, League league, int pastNGames);

    public int[] calculateRankMlb(Position position, IFantasyPointTranslator translator, Athlete athlete, int season, int pastNGames);

    int[] calculateRankNfl(Position position, IFantasyPointTranslator translator, AthleteSportEventInfo athleteSportEventInfo, Map<String, int[]> rankMap, int pastNGames);

    public BigDecimal calculateFantasyPointsPerGame(IFantasyPointTranslator translator, ITimeService timeService, AthleteSportEventInfo athleteSportEventInfo,
                                                    int pastNGames);

    Map<Integer, Integer> calculateExposure(User user);

    void saveAthleteSalary(AthleteSalary athleteSalary);

    public BigDecimal calculateFantasyPointsPerGameMLB(IFantasyPointTranslator translator, ITimeService timeService, AthleteSportEventInfo athleteSportEventInfo,
                                                       int pastNGames, Map<String, BigDecimal> fppgCache);

    BigDecimal calculateFantasyPointsPerGameNFL(IFantasyPointTranslator translator, ITimeService timeService, AthleteSportEventInfo athleteSportEventInfo,
                                                int pastNGames, Map<String, BigDecimal> fppgCache);

    public Athlete findAthlete(int statProviderId);

    public List<Athlete> findAthletes(Team team);

    public List<League> findActiveLeagues();

    String createInitialJsonForAthleteBoxscore(Position position);

    public List<Team> findTeams(League league);

    public List<SportEvent> findSportEventsSorted(Team team, boolean complete);

    public SportEvent findNextFutureSportEvent(Athlete athlete, List<Integer> eventTypeIds);

    public void saveAthletes(List<Athlete> athletes);

    public void updateAthletes(List<Athlete> athletes);

    Athlete findDefense(Team team);

    AthleteSportEventInfo findPreviousAthleteSportEventInfo(AthleteSportEventInfo athleteSportEventInfo);
}
