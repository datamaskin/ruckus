package dao;

import models.contest.Lineup;
import models.sports.*;
import models.stats.*;
import models.stats.mlb.StatsMlbBatting;
import models.stats.mlb.StatsMlbPitching;
import models.stats.nfl.*;
import models.stats.nfl.StatsNflProjection;
import models.stats.nfl.StatsNflProjectionDefense;
import models.stats.predictive.StatsProjection;
import stats.predictive.StatsEventInfo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by mgiles on 7/16/14.
 */
public interface IStatsDao {

    /**
     * @param sportEvent
     * @return LiveFeedData by SportEvent
     */
    public List<StatsLiveFeedData> findLiveFeed(SportEvent sportEvent);

    /**
     * Retrieve live feed data from the sport events provided.
     *
     * @param sportEvents The sport events to retrieve live feed data for.
     * @return LiveFeedData by SportEvent
     */
    public List<StatsLiveFeedData> findLiveFeed(List<SportEvent> sportEvents);

    /**
     * @param hash
     * @return true if the feeds are the same, false otherwise
     */
    public boolean isDuplicateLiveFeed(String hash);

    /**
     * Convenience method to fetch all gamelogs for current season, focusing on stats relevant to FP computation.
     *
     * @param athleteStatProviderId The Stats id of the athlete we're interested in.
     * @param season                The season we're interested in.
     * @return A list of StatsMlbBatting gamelogs.
     */
    public List<StatsMlbBatting> findMlbBattingStats(int athleteStatProviderId, int season);

    /**
     * @param position
     * @param season
     * @return List of StatsMlbBatting by Position and season
     */
    public List<StatsMlbBatting> findMlbBattingStats(Position position, int season);

    /**
     * @param position
     * @param season
     * @return Map<Integer, List<StatsMlbBatting>> by position and season
     */
    public Map<Integer, List<StatsMlbBatting>> findMlbBattingStatsAsMap(Position position, int season);

    /**
     * @param athleteStatProviderId
     * @param season
     * @return List of StatsMlbPitching by athlete id and season
     */
    public List<StatsMlbPitching> findMlbPitchingStats(int athleteStatProviderId, int season);

    /**
     * @param season
     * @return Map<Integer, List<StatsMlbPitching>> by season
     */
    public Map<Integer, List<StatsMlbPitching>> findMlbPitchingStats(int season);

    /**
     * @param model
     * @return Map<String, BigDecimal> based on the StatsMlbPitching model
     */
    public Map<String, BigDecimal> generateMlbPitchingMap(StatsMlbPitching model);

    /**
     * @param model
     * @return Map<String, BigDecimal> based on the StatsMlbPitching model
     */
    public Map<String, BigDecimal> generateMlbBattingMap(StatsMlbBatting model);

    /**
     * @param model
     * @return Map<String, BigDecimal> based on the StatsNflOffense model
     */
    public Map<String, BigDecimal> generateNflOffenseMap(StatsNflAthleteByEvent model);

    public StatsNflAthleteByEvent findStatsNflAthleteByEvent(Athlete athlete, SportEvent sportEvent);

    public Map<SportEvent, Team> findLastSportEventsByOpponent(Integer opponentId, int lastNEvents, Integer[] eventTypeIds);

    public StatsNflProjection findNflPrediction(SportEvent sportEvent, Athlete athlete);

    public List<StatsNflProjection> findNflPredictions(SportEvent sportEvent);

    public StatsEventInfo findNflNextSportEvent(StatsNflAthleteByEvent stat,  Integer[] eventTypeIds);

    public StatsEventInfo findNflNextSportEvent(StatsNflDefenseByEvent stat, Integer[] eventTypeIds);

    public List<List> getRucksackStats(Athlete athlete);

    public List<SportEvent> findDistinctEvents();

    public StatsAthleteBySeasonRaw findStatsAthleteSeasonRaw(Athlete athlete, int season, int eventTypeId);

    public List<StatsNflAthleteByEvent> findStatsNflAthleteByEvents(Athlete athlete);

    public List<StatsNflAthleteByEvent> findStatsNflAthleteByEvents(SportEvent sportEvent, Team team, String position);

    public List<StatsNflAthleteByEvent> findStatsNflAthleteByEvents();

    public List<StatsNflAthleteByEvent> findStatsNflAthleteByEvents(Position position, Integer season);

    public List<StatsNflAthleteByEvent> findStatsNflAthleteByEvents(Athlete athlete, Integer season);

    public List<StatsNflAthleteByEvent> findStatsNflAthleteByEvents(Athlete athlete, String asc_desc);

    public List<StatsNflAthleteByEvent> findStatsNflAthleteByEvents(Athlete athlete, Date startTime, int lastNfromEvent, Integer[] eventTypeIds);

    public StatsNflGameOdds findStatsNflGameOdds(SportEvent sportEvent);

    StatsNflDefenseByEvent findStatsNflDefenseByEvent(Athlete athlete, SportEvent sportEvent);

    List<StatsNflDefenseByEvent> findStatsNflDefenseByEvent(Athlete athlete, String startTimeSortOrder);

    List<StatsNflDefenseByEvent> findStatsNflDefenseByEvent(Athlete athlete, Date startTime, int lastNfromEvent, Integer[] eventTypeIds);

    StatsNflProjectionDefense findNflPredictionDefense(SportEvent sportEvent, Athlete athlete);

    List<StatsNflProjectionDefense> findNflPredictionDefense(SportEvent sportEvent);

    List<StatsNflDefenseByEvent> findLastStatsNflDefenseByEventsByOpponent(Team opponent, Date startTime, int lastNEvents);

    public void saveStatsAthleteSeasonRaw(StatsAthleteBySeasonRaw raw);

    public boolean wasRankedNOverLastWeeks(StatsNflAthleteByEvent stat, String position, int ranking, int weeks);

    public boolean wasDefRankedNOverLastWeeks(StatsNflAthleteByEvent stat,
                                              StatsEventInfo nextEvent,
                                              int ranking, int weeks,
                                              String position);

    public Float getAverageFantasyPointsAllowedAtPositionByDef(StatsNflAthleteByEvent stat, String position, int weeks, StatsEventInfo nextEvent);

    void updateAllFutureAthleteProjections(int statsAthleteId, float projection, Date startTime);

    public Boolean participatedInLastTwoEvents(StatsNflAthleteByEvent stat);

    public String clearNflDefStatsTable();

    public String clearNflStatsTable();

    public String clearNflDefPredictionTable();

    public String clearNflPredictionTable();

    public List<StatsNflProjection> findNextNflPredictions(Athlete athlete);

    public void saveStatsNflDepthChartsRaw(DynamoStatsNflDepthChartRaw raw, String rawData);

    public StatsNflDepthChart findStatsNflDepthChart(Athlete athlete, int season, int week, int eventTypeId);

    public Integer getNflPredictionCount();

    public void updateNflInjuries();

    List<StatsNflProjection> getProjectionsForLineup(Lineup lineup);

    StatsProjection findStatsProjection(AthleteSportEventInfo athleteSportEventInfo);

    void saveStatsProjection(StatsProjection statsProjection);
}
