package stats.predictive.nfl;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.GlobalConstants;
import dao.DaoFactory;
import models.sports.Athlete;
import models.sports.League;
import models.sports.Position;
import models.sports.Team;
import models.stats.nfl.StatsNflDefenseByEvent;
import models.stats.nfl.StatsNflProjectionDefense;
import play.Logger;
import stats.predictive.StatsEventInfo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by dmaclean on 8/6/14.
 */
public class DefenseProjector {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final int MAX_EVENTS = 42;

    public void predictNextEvent(Athlete athlete, StatsNflDefenseByEvent statsNflDefenseByEvent, boolean nextPostSeason) throws Exception {
        /*
         * Get next sport event's opponent.
         */
        StatsEventInfo nextEvent = null;
        if (nextPostSeason) {
            Integer[] eventTypeIds = {GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON, GlobalConstants.EVENT_TYPE_NFL_POST_SEASON};
            nextEvent = DaoFactory.getStatsDao().findNflNextSportEvent(statsNflDefenseByEvent, eventTypeIds);
        } else {
            nextEvent = DaoFactory.getStatsDao().findNflNextSportEvent(statsNflDefenseByEvent, new Integer[0]);
        }
        if (nextEvent == null)
            return;

        StatsNflProjectionDefense prediction = DaoFactory.getStatsDao().findNflPredictionDefense(nextEvent.getSportEvent(),
                statsNflDefenseByEvent.getAthlete());
        StatsNflDefenseByEvent nextStat = DaoFactory.getStatsDao().findStatsNflDefenseByEvent(athlete, nextEvent.getSportEvent());

        if (prediction != null) {
            // only redo predictions for events that have not yet occurred.
            if (prediction.getSportEvent() == null || prediction.getSportEvent().isComplete()) {
                return;
            }
        } else {
            prediction = new StatsNflProjectionDefense();
            prediction.setAthlete(athlete);
            prediction.setSportEvent(nextEvent.getSportEvent());
        }

                /*
                 * Determine moving average of fantasy points.
                 */
        List<StatsNflDefenseByEvent> currentWindow = DaoFactory.getStatsDao().findStatsNflDefenseByEvent(
                statsNflDefenseByEvent.getAthlete(), nextEvent.getStartTime(), MAX_EVENTS, new Integer[]{
                        GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON,
                        GlobalConstants.EVENT_TYPE_NFL_POST_SEASON
                });

        List<BigDecimal> teamMovingAverage = createMovingAverages(currentWindow);
        prediction.setHistFppAvgRange(mapper.writeValueAsString(teamMovingAverage));

                /*
                 * Determine average FPP-against for next opponent.
                 */
        Team opponent = DaoFactory.getSportsDao().findTeam(nextEvent.getOpponentId());
        List<StatsNflDefenseByEvent> currentWindowOpponent = DaoFactory.getStatsDao().findLastStatsNflDefenseByEventsByOpponent(opponent, nextEvent.getStartTime(), MAX_EVENTS);

//                BigDecimal totalOpponent = BigDecimal.ZERO;
//                List<BigDecimal> opponentAverages = new ArrayList<>();
//                for(int i=0; i<currentWindowOpponent.size(); i++) {
//                    StatsNflDefenseByEvent currentWindowOpponentEvent = currentWindowOpponent.get(i);
//                    total = total.add(currentWindowOpponentEvent.getFppInThisEvent());
//                    opponentAverages.add(total.divide(new BigDecimal(i)));
//                }
        List<BigDecimal> opponentMovingAverage = createMovingAverages(currentWindowOpponent);
        prediction.setHistOpponentOffenseFppAvg(mapper.writeValueAsString(opponentMovingAverage));

        prediction.setSeason(nextEvent.getSportEvent().getSeason());
        prediction.setWeek(nextEvent.getSportEvent().getWeek());
        prediction.setStartTime(nextEvent.getSportEvent().getStartTime());
        prediction.setTeamName(statsNflDefenseByEvent.getTeam().getName());

        // Determine opponent
        List<Team> teams = nextEvent.getSportEvent().getTeams();
        String opponentTeamName = teams.get(0).equals(statsNflDefenseByEvent.getTeam()) ? teams.get(1).getName() : teams.get(0).getName();
        prediction.setOpponentTeamName(opponentTeamName);

        // Determine home or away
        TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>() {
        };
        Map<String, Object> shortDescription = mapper.readValue(nextEvent.getSportEvent().getShortDescription(), typeReference);
        prediction.setHome(shortDescription.get("homeTeam").equals(statsNflDefenseByEvent.getTeam().getAbbreviation()));

        // Actual FPP
        prediction.setActualFpp(nextStat == null ? 0 : nextStat.getFppInThisEvent().floatValue());

        // Projected FPP
        if (!teamMovingAverage.isEmpty() && !opponentMovingAverage.isEmpty()) {
            float teamFactor = 0.8f * teamMovingAverage.get(teamMovingAverage.size() - 1).floatValue();
            float opponentFactor = 0.2f * opponentMovingAverage.get(opponentMovingAverage.size() - 1).floatValue();
            prediction.setProjectedFpp(teamFactor + opponentFactor);

            // Projected FPP Mod
            prediction.setProjectedFppMod(teamFactor + opponentFactor);
        }

        StatsNflProjectionDefense dbProjection = DaoFactory.getStatsDao().findNflPredictionDefense(nextEvent.getSportEvent(),
                statsNflDefenseByEvent.getAthlete());
        if (dbProjection == null) {
            try {
                Ebean.save(prediction);
            } catch (Exception e) {
                Logger.warn(e.getMessage());
            }
        }
    }

    public void predict() throws Exception {
        List<Athlete> athletes = DaoFactory.getSportsDao().findAthletes(League.NFL);
        for (Athlete athlete : athletes) {
            if (!athlete.getPositions().contains(Position.FB_DEFENSE)) {
                continue;
            }

            LinkedList<BigDecimal> fppStack = new LinkedList<>();

            List<StatsNflDefenseByEvent> statsNflDefenseByEvents = DaoFactory.getStatsDao().findStatsNflDefenseByEvent(athlete, "asc");
            for (int i = 0; i < statsNflDefenseByEvents.size(); i++) {
                StatsNflDefenseByEvent statsNflDefenseByEvent = statsNflDefenseByEvents.get(i);

                if (i == (statsNflDefenseByEvents.size() - 1)) {
                    predictNextEvent(athlete, statsNflDefenseByEvent, true);
                }
                predictNextEvent(athlete, statsNflDefenseByEvent, false);
            }
        }
    }

    /**
     * Convenience method for generating an array of moving averages given a list of StatsNflDefenseByEvent objects.
     *
     * @param currentWindow
     * @return
     */
    private List<BigDecimal> createMovingAverages(List<StatsNflDefenseByEvent> currentWindow) {
        BigDecimal total = BigDecimal.ZERO;
        List<BigDecimal> averages = new ArrayList<>();
        for (int i = 0; i < currentWindow.size(); i++) {
            StatsNflDefenseByEvent currentWindowElement = currentWindow.get(i);
            total = total.add(currentWindowElement.getFppInThisEvent());
            averages.add(total.divide(new BigDecimal(i + 1), 4, RoundingMode.HALF_UP));
        }

        return averages;
    }
}
