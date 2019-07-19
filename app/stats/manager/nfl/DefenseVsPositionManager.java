package stats.manager.nfl;

import common.GlobalConstants;
import dao.DaoFactory;
import models.sports.*;
import models.stats.nfl.StatsNflAthleteByEvent;
import models.stats.nfl.StatsNflDefenseByEvent;
import play.Logger;
import stats.dvp.TeamFantasyPointPair;
import stats.manager.IStatsDefenseVsPositionManager;
import utils.ITimeService;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by dmaclean on 8/3/14.
 */
public class DefenseVsPositionManager implements IStatsDefenseVsPositionManager {
    private ITimeService timeService;
    private Map<Integer, Map<Integer, Integer>> dvpCache;

    public DefenseVsPositionManager(ITimeService timeService) {
        this.timeService = timeService;
    }

    @Override
    public void calculateDefenseVsPosition(int season) {
        dvpCache.clear();
        for (Position position : Position.ALL_FOOTBALL) {
            if (position.getAbbreviation().equals(Position.FB_FLEX.getAbbreviation()) ||
                    position.getAbbreviation().equals(Position.FB_DEFENSE.getAbbreviation())) {
                continue;
            }

            Map<Integer, Integer> positionRanks = calculateDefenseVsPosition(season, position);

            dvpCache.put(position.getId(), positionRanks);
        }
    }

    /**
     * Determine the defense-vs-position rankings for teams as of the provided start date for the desired position.
     *
     * @param startTime The point in time we want to evaluate DvP from.
     * @param position  The position we want to evaluate for.
     * @return
     */
    @Override
    public Map<Integer, Integer> calculateDefenseVsPosition(Date startTime, Position position) {
        /*
         * Check the cache first.
         */
        if (dvpCache.containsKey(position.getId())) {
            return dvpCache.get(position.getId());
        }

        List<Team> teams = DaoFactory.getSportsDao().findTeams(League.NFL);

        Comparator<TeamFantasyPointPair> c = (o1, o2) -> o1.getFantasyPoints().compareTo(o2.getFantasyPoints());
        Map<Team, BigDecimal> fpTotals = new HashMap<>();

        /*
         * Iterate through each team to determine their DvP vs the specified position for the past 17 games.
         */
        for (Team team : teams) {
            BigDecimal total = BigDecimal.ZERO;

            Athlete teamAthlete = DaoFactory.getSportsDao().findAthlete(team.getStatProviderId());
            List<StatsNflDefenseByEvent> statsNflDefenseByEvents = DaoFactory.getStatsDao().findStatsNflDefenseByEvent(teamAthlete,
                    startTime, 17, new Integer[]{GlobalConstants.EVENT_TYPE_NFL_POST_SEASON, GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON});

            /*
             * Get all the opponents by position and sport event for the current sport event.
             */
            for (StatsNflDefenseByEvent statsNflDefenseByEvent : statsNflDefenseByEvents) {
                String pos = position.getAbbreviation().equalsIgnoreCase("FB") ? "RB" : position.getAbbreviation();
                List<StatsNflAthleteByEvent> statsNflAthleteByEvents = DaoFactory.getStatsDao().findStatsNflAthleteByEvents(
                        statsNflDefenseByEvent.getSportEvent(), statsNflDefenseByEvent.getOpponent(), pos);

                for (StatsNflAthleteByEvent statsNflAthleteByEvent : statsNflAthleteByEvents) {
                    total = total.add(statsNflAthleteByEvent.getFppInThisEvent());
                }
            }

            fpTotals.put(team, total);
        }

        List<TeamFantasyPointPair> ranks = fpTotals.entrySet().stream().map(entry -> new TeamFantasyPointPair(entry.getKey(), entry.getValue())).collect(Collectors.toList());
        Collections.sort(ranks, c);

        int rank = 1;
        Map<Integer, Integer> positionRanks = new HashMap<>();
        for (TeamFantasyPointPair teamFantasyPointPair : ranks) {
            positionRanks.put(teamFantasyPointPair.getTeam().getId(), rank++);
        }

        dvpCache.put(position.getId(), positionRanks);
        return positionRanks;
    }

    @Override
    public Map<Integer, Integer> calculateDefenseVsPosition(int season, Position position) {
        if (dvpCache.containsKey(position.getId())) {
            return dvpCache.get(position.getId());
        }

        Logger.info("Calculating DvP for " + position.getSport().getName() + "/" + position.getName());
        Comparator<TeamFantasyPointPair> c = (o1, o2) -> o1.getFantasyPoints().compareTo(o2.getFantasyPoints());
        List<StatsNflAthleteByEvent> stats = DaoFactory.getStatsDao().findStatsNflAthleteByEvents(position, season);
        if (stats.isEmpty()) {
            stats = DaoFactory.getStatsDao().findStatsNflAthleteByEvents(position, season - 1);
        }

        Map<Team, BigDecimal> fpTotals = new HashMap<>();

        for (StatsNflAthleteByEvent statsNflAthleteByEvent : stats) {
            Athlete athlete = statsNflAthleteByEvent.getAthlete();
            SportEvent sportEvent = statsNflAthleteByEvent.getSportEvent();
            if (sportEvent == null || athlete == null || athlete.getTeam() == null) {
                continue;
            }

            Team opponent = (athlete.getTeam().getId() == sportEvent.getTeams().get(0).getId()) ? sportEvent.getTeams().get(1) : sportEvent.getTeams().get(0);

            BigDecimal teamTotal = fpTotals.get(opponent);
            if (teamTotal == null) {
                teamTotal = new BigDecimal(0.0);
            }
            teamTotal = teamTotal.add(statsNflAthleteByEvent.getFppInThisEvent());
            fpTotals.put(opponent, teamTotal);
        }

        List<TeamFantasyPointPair> ranks = fpTotals.entrySet().stream().map(entry -> new TeamFantasyPointPair(entry.getKey(), entry.getValue())).collect(Collectors.toList());
        Collections.sort(ranks, c);


        int rank = 1;
        Map<Integer, Integer> positionRanks = new HashMap<>();
        for (TeamFantasyPointPair teamFantasyPointPair : ranks) {
            positionRanks.put(teamFantasyPointPair.getTeam().getId(), rank++);
        }

        dvpCache.put(position.getId(), positionRanks);
        return positionRanks;
    }

    public Map<Integer, Map<Integer, Integer>> getDvpCache() {
        return dvpCache;
    }

    public void setDvpCache(Map<Integer, Map<Integer, Integer>> dvpCache) {
        this.dvpCache = dvpCache;
    }
}
