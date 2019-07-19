package stats.manager.mlb;

import dao.DaoFactory;
import models.sports.Athlete;
import models.sports.Position;
import models.sports.SportEvent;
import models.sports.Team;
import models.stats.mlb.StatsMlbBatting;
import play.Logger;
import stats.dvp.TeamFantasyPointPair;
import stats.manager.IStatsDefenseVsPositionManager;
import stats.translator.IFantasyPointTranslator;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class responsible for generating Defense-vs-Position rankings for MLB.
 */
public class DefenseVsPositionManager implements IStatsDefenseVsPositionManager {
    private IFantasyPointTranslator translator;
    private Map<Integer, Map<Integer, Integer>> dvpCache;

    public DefenseVsPositionManager(IFantasyPointTranslator translator) {
        this.translator = translator;
    }

    /**
     * Generates the DvP calculations for the specified season.
     *
     * @param season The season of interest.
     */
    public void calculateDefenseVsPosition(int season) {
        dvpCache.clear();
        for (Position position : Position.ALL_BASEBALL) {
            if (position.getAbbreviation().equals(Position.BS_FLEX.getAbbreviation()) ||
                    position.getAbbreviation().equals(Position.BS_PITCHER.getAbbreviation())) {
                continue;
            }

            Map<Integer, Integer> positionRanks = calculateDefenseVsPosition(season, position);

            dvpCache.put(position.getId(), positionRanks);
        }
    }

    /**
     * Generates the DvP calculations for the specified season and position.
     *
     * @param season   The season of interest.
     * @param position The position of interest.
     * @return
     */
    public Map<Integer, Integer> calculateDefenseVsPosition(int season, Position position) {
        if (dvpCache.containsKey(position.getId())) {
            return dvpCache.get(position.getId());
        }

        Logger.info("Calculating DvP for " + position.getSport().getName() + "/" + position.getName());
        Comparator<TeamFantasyPointPair> c = (o1, o2) -> o1.getFantasyPoints().compareTo(o2.getFantasyPoints());
        List<StatsMlbBatting> stats = DaoFactory.getStatsDao().findMlbBattingStats(position, season);

        Map<Team, BigDecimal> fpTotals = new HashMap<>();

        for (StatsMlbBatting statsMlbBatting : stats) {
            Athlete athlete = DaoFactory.getSportsDao().findAthlete(statsMlbBatting.getStatProviderId());
            SportEvent sportEvent = DaoFactory.getSportsDao().findSportEvent(statsMlbBatting.getEventId());
            if (sportEvent == null || athlete == null) {
                continue;
            }

            Team opponent = (athlete.getTeam().getId() == sportEvent.getTeams().get(0).getId()) ? sportEvent.getTeams().get(1) : sportEvent.getTeams().get(0);

            BigDecimal teamTotal = fpTotals.get(opponent);
            if (teamTotal == null) {
                teamTotal = new BigDecimal(0.0);
            }
            teamTotal = teamTotal.add(translator.calculateFantasyPoints(DaoFactory.getStatsDao().generateMlbBattingMap(statsMlbBatting)));
            fpTotals.put(opponent, teamTotal);
        }

        List<TeamFantasyPointPair> ranks = fpTotals.entrySet().stream().map(entry -> new TeamFantasyPointPair(entry.getKey(), entry.getValue())).collect(Collectors.toList());
        Collections.sort(ranks, c);


        int rank = 1;
        Map<Integer, Integer> positionRanks = new HashMap<>();
        for (TeamFantasyPointPair teamFantasyPointPair : ranks) {
            positionRanks.put(teamFantasyPointPair.getTeam().getId(), rank++);
        }
        return positionRanks;
    }

    @Override
    public Map<Integer, Integer> calculateDefenseVsPosition(Date startTime, Position position) {
        return null;
    }

    public IFantasyPointTranslator getTranslator() {
        return translator;
    }

    public void setTranslator(IFantasyPointTranslator translator) {
        this.translator = translator;
    }

    public Map<Integer, Map<Integer, Integer>> getDvpCache() {
        return dvpCache;
    }

    public void setDvpCache(Map<Integer, Map<Integer, Integer>> dvpCache) {
        this.dvpCache = dvpCache;
    }
}
