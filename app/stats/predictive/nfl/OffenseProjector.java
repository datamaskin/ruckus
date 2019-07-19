package stats.predictive.nfl;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.GlobalConstants;
import dao.DaoFactory;
import models.sports.*;
import models.stats.nfl.StatsNflAthleteByEvent;
import models.stats.nfl.StatsNflDepthChart;
import models.stats.nfl.StatsNflGameOdds;
import models.stats.nfl.StatsNflProjection;
import org.json.JSONArray;
import org.json.JSONException;
import play.Logger;
import stats.predictive.StatsEventInfo;
import utils.ListUtil;

import java.util.*;

/**
 * Created by mgiles on 7/24/14.
 */
@SuppressWarnings("unchecked")
public class OffenseProjector {

    private static final int MAX_EVENTS = 42;

    private LinkedList<Float> getAverageRange(LinkedList<Float> list) {
        LinkedList<Float> averages = new LinkedList<>();
        float total = 0;
        for (int i = 0; i < list.size(); i++) {
            float ii = list.get(i);
            total += ii;
            float avg = total / (float) (i + 1);
            averages.add(avg);
        }
        return averages;
    }

    private Map<Integer, Float> handlePassAttemptsAtPositionByEvent(List<StatsNflAthleteByEvent> stats) throws Exception {
        Map<Integer, Float> pomMap = new HashMap<>();
        StatsNflAthleteByEvent max = null;
        for (StatsNflAthleteByEvent stat : stats) {
            if (max == null || max.getPassingAttempts() <= stat.getPassingAttempts()) {
                max = stat;
            }
        }
        for (StatsNflAthleteByEvent stat : stats) {
            float percentOfMax = max.getPassingAttempts().equals(0) ? 0f : (float) stat.getPassingAttempts() / (float) max.getPassingAttempts();
            pomMap.put(stat.getAthlete().getId(), percentOfMax);
        }
        return pomMap;
    }

    private Map<Integer, Float> handleRecTargetsAtPositionByEvent(List<StatsNflAthleteByEvent> stats) throws Exception {
        Map<Integer, Float> pomMap = new HashMap<>();
        StatsNflAthleteByEvent max = null;
        for (StatsNflAthleteByEvent stat : stats) {
            if (max == null || max.getReceivingTargets() <= stat.getReceivingTargets()) {
                max = stat;
            }
        }
        for (StatsNflAthleteByEvent stat : stats) {
            float percentOfMax = max.getReceivingTargets().equals(0) ? 0f : (float) stat.getReceivingTargets() / (float) max.getReceivingTargets();
            pomMap.put(stat.getAthlete().getId(), percentOfMax);
        }
        return pomMap;
    }

    private Map<Integer, Float> handleRushAttemptsAtPositionByEvent(List<StatsNflAthleteByEvent> stats) throws Exception {
        Map<Integer, Float> pomMap = new HashMap<>();
        StatsNflAthleteByEvent max = null;
        for (StatsNflAthleteByEvent stat : stats) {
            if (max == null || max.getRushingAttempts() <= stat.getRushingAttempts()) {
                max = stat;
            }
        }
        for (StatsNflAthleteByEvent stat : stats) {
            float percentOfMax = max.getRushingAttempts().equals(0) ? 0f : (float) stat.getRushingAttempts() / (float) max.getRushingAttempts();
            pomMap.put(stat.getAthlete().getId(), percentOfMax);
        }
        return pomMap;
    }

    public void predictNextEvent(Athlete athlete, StatsNflAthleteByEvent stat, boolean nextRegSeason) throws Exception {

        // opposing team for next event
        StatsEventInfo nextEvent;
        if (nextRegSeason) {
            Integer[] eventTypeIds = {GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON, GlobalConstants.EVENT_TYPE_NFL_POST_SEASON};
            nextEvent = DaoFactory.getStatsDao().findNflNextSportEvent(stat, eventTypeIds);
        } else {
            nextEvent = DaoFactory.getStatsDao().findNflNextSportEvent(stat, new Integer[0]);
        }
        if (nextEvent == null) {
            return;
        }

        StatsNflProjection prediction = DaoFactory.getStatsDao().findNflPrediction(nextEvent.getSportEvent(),
                stat.getAthlete());
        StatsNflAthleteByEvent nextStat = DaoFactory.getStatsDao().findStatsNflAthleteByEvent(athlete, nextEvent.getSportEvent());

        if (prediction != null) {
            // only redo predictions for events that have not yet occurred.
            if (prediction.getSportEvent() == null || prediction.getSportEvent().isComplete()) {
                return;
            }
        } else {
            prediction = new StatsNflProjection();
            prediction.setUniqueKey(athlete.getStatProviderId() + "_" + nextEvent.getSportEvent().getStatProviderId());
            prediction.setStatsAthleteId(athlete.getStatProviderId());
        }

        // calculate last MAX_EVENTS games from this one
        Integer[] iSeasons = {GlobalConstants.EVENT_TYPE_NFL_POST_SEASON, GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON};
        List<StatsNflAthleteByEvent> lastN = DaoFactory.getStatsDao()
                .findStatsNflAthleteByEvents(athlete, nextEvent.getStartTime(), MAX_EVENTS, iSeasons);
        Map<Integer, LinkedList<Float>> percentOfMaxRangesPassAttempts = new HashMap<>();
        Map<Integer, LinkedList<Float>> percentOfMaxRangesRushAttempts = new HashMap<>();
        Map<Integer, LinkedList<Float>> percentOfMaxRangesRecTargets = new HashMap<>();

        for (StatsNflAthleteByEvent lastStat : lastN) {
            String pos = lastStat.getAthlete().getPositions().get(0).getAbbreviation().equalsIgnoreCase("FB")
                    ? "RB" : lastStat.getAthlete().getPositions().get(0).getAbbreviation();

            List<StatsNflAthleteByEvent> allAtPositionByEventAndTeam = DaoFactory.getStatsDao()
                    .findStatsNflAthleteByEvents(lastStat.getSportEvent(), lastStat.getTeam(), pos);

            //Passing
            Map<Integer, Float> pass = handlePassAttemptsAtPositionByEvent(allAtPositionByEventAndTeam);
            for (Map.Entry<Integer, Float> entry : pass.entrySet()) {
                LinkedList<Float> passList = percentOfMaxRangesPassAttempts.get(entry.getKey()) == null ?
                        new LinkedList<>() : percentOfMaxRangesPassAttempts.get(entry.getKey());
                passList.add(entry.getValue());
                percentOfMaxRangesPassAttempts.put(entry.getKey(), passList);

            }

            //Rushing
            Map<Integer, Float> rush = handleRushAttemptsAtPositionByEvent(allAtPositionByEventAndTeam);
            for (Map.Entry<Integer, Float> entry : rush.entrySet()) {
                LinkedList<Float> rushList = percentOfMaxRangesRushAttempts.get(entry.getKey()) == null ?
                        new LinkedList<>() : percentOfMaxRangesRushAttempts.get(entry.getKey());
                rushList.add(entry.getValue());
                percentOfMaxRangesRushAttempts.put(entry.getKey(), rushList);
            }

            //Rec Targets
            Map<Integer, Float> rec = handleRecTargetsAtPositionByEvent(allAtPositionByEventAndTeam);
            for (Map.Entry<Integer, Float> entry : rec.entrySet()) {
                LinkedList<Float> recList = percentOfMaxRangesRecTargets.get(entry.getKey()) == null ?
                        new LinkedList<>() : percentOfMaxRangesRecTargets.get(entry.getKey());
                recList.add(entry.getValue());
                percentOfMaxRangesRecTargets.put(entry.getKey(), recList);
            }
        }
        LinkedList<Float> pa = percentOfMaxRangesPassAttempts.get(stat.getAthlete().getId());
        stat.setPassingAttemptsPercentOfMaxPerGameRange(new ObjectMapper().writeValueAsString(ListUtil.trim(pa, MAX_EVENTS)));
        stat.setPassingAttemptsPercentOfMaxAvgRange(new ObjectMapper().writeValueAsString(ListUtil.findAverages(pa, MAX_EVENTS)));

        LinkedList<Float> rt = percentOfMaxRangesRecTargets.get(stat.getAthlete().getId());
        stat.setReceivingTargetsPercentOfMaxPerGameRange(new ObjectMapper().writeValueAsString(ListUtil.trim(rt, MAX_EVENTS)));
        stat.setReceivingTargetsPercentOfMaxAvgRange(new ObjectMapper().writeValueAsString(ListUtil.findAverages(rt, MAX_EVENTS)));

        LinkedList<Float> ra = percentOfMaxRangesRushAttempts.get(stat.getAthlete().getId());
        stat.setRushingAttemptsPercentOfMaxPerGameRange(new ObjectMapper().writeValueAsString(ListUtil.trim(ra, MAX_EVENTS)));
        stat.setRushingAttemptsPercentOfMaxAvgRange(new ObjectMapper().writeValueAsString(ListUtil.findAverages(ra, MAX_EVENTS)));

        // their last MAX_EVENTS opponents (an enemy of my enemy is my friend)
        LinkedList<Float> qbs = new LinkedList<>();
        LinkedList<Float> wrs = new LinkedList<>();
        LinkedList<Float> rbs = new LinkedList<>();
        LinkedList<Float> tes = new LinkedList<>();
        LinkedList<Float> mes = new LinkedList<>();
        List<String> positionsQB = new ArrayList<>();

        Map<SportEvent, Team> lastNOpponents = DaoFactory.getStatsDao()
                .findLastSportEventsByOpponent(nextEvent.getOpponentId(), MAX_EVENTS, iSeasons);
        for (Map.Entry<SportEvent, Team> entry : lastNOpponents.entrySet()) {
            List<StatsNflAthleteByEvent> allOppQBs = DaoFactory.getStatsDao()
                    .findStatsNflAthleteByEvents(entry.getKey(), entry.getValue(), "QB");

            Float qbTotal = 0f;
            for (StatsNflAthleteByEvent qb : allOppQBs) {
                qbTotal += qb.getFppInThisEvent().floatValue();
            }

            qbs.add(qbTotal);

            List<StatsNflAthleteByEvent> allOppWRs = DaoFactory.getStatsDao()
                    .findStatsNflAthleteByEvents(entry.getKey(), entry.getValue(), "WR");

            Float wrTotal = 0f;
            for (StatsNflAthleteByEvent wr : allOppWRs) {
                wrTotal += wr.getFppInThisEvent().floatValue();
            }
            wrs.add(wrTotal);

            List<StatsNflAthleteByEvent> allOppRBs = DaoFactory.getStatsDao()
                    .findStatsNflAthleteByEvents(entry.getKey(), entry.getValue(), "RB");

            Float rbTotal = 0f;
            for (StatsNflAthleteByEvent rb : allOppRBs) {
                rbTotal += rb.getFppInThisEvent().floatValue();
            }
            rbs.add(rbTotal);

            List<StatsNflAthleteByEvent> allOppTEs = DaoFactory.getStatsDao()
                    .findStatsNflAthleteByEvents(entry.getKey(), entry.getValue(), "TE");

            Float teTotal = 0f;
            for (StatsNflAthleteByEvent te : allOppTEs) {
                teTotal += te.getFppInThisEvent().floatValue();
            }
            tes.add(teTotal);

            String mePos = stat.getAthlete().getPositions().get(0).getAbbreviation().equalsIgnoreCase("FB")
                    ? "RB" : stat.getAthlete().getPositions().get(0).getAbbreviation();

            List<StatsNflAthleteByEvent> meOpp = DaoFactory.getStatsDao()
                    .findStatsNflAthleteByEvents(entry.getKey(), entry.getValue(), mePos);

            Float meTotal = 0f;
            for (StatsNflAthleteByEvent me : meOpp) {
                meTotal += me.getFppInThisEvent().floatValue();
            }
            mes.add(meTotal);
        }
        stat.setOpponentPointsAllowedAtPositionPerGameRange(new ObjectMapper().writeValueAsString(mes));
        stat.setOpponentPointsAllowedAtPositionAvgRange(new ObjectMapper().writeValueAsString(getAverageRange(mes)));

        StatsNflGameOdds odds = DaoFactory.getStatsDao().findStatsNflGameOdds(nextEvent.getSportEvent());

        prediction.setParticipatedLastTwo(DaoFactory.getStatsDao().participatedInLastTwoEvents(stat));
        prediction.setSeason(nextEvent.getSportEvent().getSeason());
        prediction.setPosition(stat.getPosition());
        prediction.setWeek(nextEvent.getSportEvent().getWeek());
        prediction.setActualFpp(nextStat == null ? 0 : nextStat.getFppInThisEvent().floatValue());
        prediction.setSportEvent(nextEvent.getSportEvent());
        prediction.setAthlete(stat.getAthlete());
        prediction.setStartTime(nextEvent.getStartTime());
        prediction.setHistPassAttemptsPercentMaxAvgRange(stat.getPassingAttemptsPercentOfMaxAvgRange());
        prediction.setHistFppAvgRange(stat.getFantasyPointsAvgRange()); //VictisFantasyPointsAgroMean5GB
        prediction.setHistRecTargetsPercentMaxAvgRange(stat.getReceivingTargetsPercentOfMaxAvgRange()); // PlayerPercentMaxPositionTargetsAgroMean5GB
        prediction.setHistRushAttemptsPercentMaxAvgRange(stat.getRushingAttemptsPercentOfMaxAvgRange());// PlayerPercentMaxPositionRushingAttemptsAgroMean5GB
        prediction.setPredFppAllowedQbAvgRange(new ObjectMapper().writeValueAsString(getAverageRange(qbs))); //FantasyPointsAllowedQBxStartedAgroMean5GB
        prediction.setPredFppAllowedRbAvgRange(new ObjectMapper().writeValueAsString(getAverageRange(rbs)));
        prediction.setPredFppAllowedTeAvgRange(new ObjectMapper().writeValueAsString(getAverageRange(tes)));
        stat.setPredFppAllowedQbAvgRange(new ObjectMapper().writeValueAsString(getAverageRange(qbs))); //FantasyPointsAllowedQBxStartedAgroMean5GB
        prediction.setPredFppAllowedWrAvgRange(new ObjectMapper().writeValueAsString(getAverageRange(wrs))); //FantasyPointsAllowedWRxAllAgroMean5GB
        stat.setPredFppAllowedWrAvgRange(new ObjectMapper().writeValueAsString(getAverageRange(wrs))); //FantasyPointsAllowedWRxAllAgroMean5GB
        prediction.setHistRecTargetsAvgRange(stat.getReceivingTargetsAvgRange());//ReceivingTargetsAgroMean1GB
        prediction.setHistPassingRatingAvgRange(stat.getPassingRatingAvgRange());//PassingRatingAgroMean5GB
        prediction.setHistRecYardsAvgRange(stat.getReceivingYardsAvgRange());//"ReceivingYardsAgroMean7GB"
        StatsNflDepthChart dbChart = DaoFactory.getStatsDao()
                .findStatsNflDepthChart(athlete, nextEvent.getSportEvent().getSeason(), nextEvent.getSportEvent().getWeek(), nextEvent.getSportEvent().getEventTypeId());
        if (dbChart != null) {
            prediction.setDepth(dbChart.getDepth());
            prediction.setDepthPosition(dbChart.getDepthPosition());
        }
        prediction.setEventTypeId(nextEvent.getSportEvent().getEventTypeId());
        prediction.setOverUnder(odds == null ? 0 : odds.getCurrentTotal());
        if (odds == null) {
            prediction.setPointSpread(0);
        } else if (stat.getTeam().getStatProviderId() == odds.getCurrentFavoriteTeamId()) {
            prediction.setPointSpread(odds.getCurrentFavoritePoints());
        } else {
            prediction.setPointSpread(odds.getCurrentFavoritePoints() * -1);
        }
        predictFpp3(stat, nextEvent, prediction);
        try {
            Ebean.update(stat);
            Logger.info("Updated stat: " + stat.getId());
            Ebean.save(prediction);
            Logger.info("Saved Prediction: " + prediction.getId());
        } catch (Exception e) {
            Logger.warn(e.getMessage());
        }
    }

    public void predict() throws Exception {
        List<Athlete> athletes = DaoFactory.getSportsDao().findAthletes(League.NFL);
        for (Athlete athlete : athletes) {
            // skip kickers. Mitch says they suck
            if (athlete.getPositions().contains(Position.FB_KICKER)) {
                continue;
            }
//
//
//            //TODO: remove///////////////////////
//            if (!athlete.getPositions().contains(Position.FB_QUARTERBACK)) {
//                continue;
//            }
//            //TODO: remove///////////////////////
//

            List<StatsNflAthleteByEvent> statsPerAthlete = DaoFactory.getStatsDao().findStatsNflAthleteByEvents(athlete, "asc");
            for (int i = 0; i < statsPerAthlete.size(); i++) {
                StatsNflAthleteByEvent stat = statsPerAthlete.get(i);
                if (i == (statsPerAthlete.size() - 1)) {
                    predictNextEvent(athlete, stat, true);
                }
                predictNextEvent(athlete, stat, false);
            }
        }
    }

    private void predictFpp3(StatsNflAthleteByEvent stat, StatsEventInfo nextEvent, StatsNflProjection prediction) throws JSONException {
        String position = prediction.getAthlete().getPositions().get(0).getAbbreviation();
        JSONArray fppArray = new JSONArray(prediction.getHistFppAvgRange());
        JSONArray rtpmArray = new JSONArray(prediction.getHistRecTargetsPercentMaxAvgRange());
        JSONArray ratpmArray = new JSONArray(prediction.getHistRushAttemptsPercentMaxAvgRange());
        JSONArray rtArray = new JSONArray(prediction.getHistRecTargetsAvgRange());
        JSONArray ryArray = new JSONArray(prediction.getHistRecYardsAvgRange());
        JSONArray passRatingArray = new JSONArray(prediction.getHistPassingRatingAvgRange());

        float pred = 0;

        // true/false
        float Top15HistFppAvgRange3gb = DaoFactory.getStatsDao().wasRankedNOverLastWeeks(stat, null, 15, 3) ? 1f : 0f;
        float Top30HistFppAvgRange7gb = DaoFactory.getStatsDao().wasRankedNOverLastWeeks(stat, null, 30, 7) ? 1f : 0f;

        // true/false
        float StatsAthleteId267443Q = isAthlete(267443, stat) ? 1f : 0f;
        float StatsAthleteId269221Q = isAthlete(269221, stat) ? 1f : 0f;
        float StatsAthleteId300346Q = isAthlete(300346, stat) ? 1f : 0f;
        float StatsAthleteId332757Q = isAthlete(332757, stat) ? 1f : 0f;
        float StatsAthleteId332959Q = isAthlete(332959, stat) ? 1f : 0f;
        float StatsAthleteId227442Q = isAthlete(227442, stat) ? 1f : 0f;
        float StatsAthleteId295918Q = isAthlete(295918, stat) ? 1f : 0f;
        float StatsAthleteId381091Q = isAthlete(381091, stat) ? 1f : 0f;
        float StatsAthleteId550870Q = isAthlete(550870, stat) ? 1f : 0f;

        float HistRushAttemptsPercentMaxAvgRange11gb = (float) ratpmArray.optDouble(10, -99f); //zero based
        float HistRecTargetsPercentMaxAvgRange11gb = (float) rtpmArray.optDouble(10, -99f);//zero based
        float HistRecTargetsAvgRange3gb = (float) rtArray.optDouble(2, -99f); //zero based
        float HistRecTargetsAvgRange4gb = (float) rtArray.optDouble(3, -99f); //zero based
        float HistRecTargetsAvgRange8gb = (float) rtArray.optDouble(7, -99f); //zero based
        float HistRecYardsAvgRange7gb = (float) ryArray.optDouble(6, -99f); //zero based
        float HistFppAvgRange10gb = (float) fppArray.optDouble(9, -99f);//zero based
        float HistFppAvgRange8gb = (float) fppArray.optDouble(7, -99f);//zero based
        float HistFppAvgRange6gb = (float) fppArray.optDouble(5, -99f);//zero based
        float HistFppAvgRange3gb = (float) fppArray.optDouble(2, -99f);//zero based
        float HistPassingRatingAvgRange5gb = (float) passRatingArray.optDouble(4, -99f);//zero based
        float PredFppAllowedQbAvgRange35gb = DaoFactory.getStatsDao().getAverageFantasyPointsAllowedAtPositionByDef(stat, "QB", 35, nextEvent);
        float PredFppAllowedRbAvgRange31gb = DaoFactory.getStatsDao().getAverageFantasyPointsAllowedAtPositionByDef(stat, "RB", 31, nextEvent);
        float PredFppAllowedTeAvgRange34gb = DaoFactory.getStatsDao().getAverageFantasyPointsAllowedAtPositionByDef(stat, "TE", 34, nextEvent);
        float PredFppAllowedWrAvgRange38gb = DaoFactory.getStatsDao().getAverageFantasyPointsAllowedAtPositionByDef(stat, "WR", 38, nextEvent);
        int depth = prediction.getDepth();
        boolean futureEvent = prediction.getSportEvent().getStartTime().after(new Date());

        if (position.equals("QB")) {
            /**
             {{"QB", {-16.12` + 0.487` HistFppAvgRange10gb +
             0.092` HistPassingRatingAvgRange5gb +
             0.933` PredFppAllowedQbAvgRange35gb}},
             */
            pred = -16.12f
                    + (0.487f * (HistFppAvgRange10gb == -99f ? 0f : HistFppAvgRange10gb))
                    + (0.092f * (HistPassingRatingAvgRange5gb == -99f ? 0f : HistPassingRatingAvgRange5gb))
                    + (0.933f * (PredFppAllowedQbAvgRange35gb == -99f ? 0f : PredFppAllowedQbAvgRange35gb));
            if (futureEvent) {
                switch (depth) {
                    case 0:
                        pred = 0;
                        break;
                    case 1:
                        break;
                    default:
                        pred = pred / (float) depth;
                }
            }
        } else if (position.equals("RB") || position.equals("FB")) {
            /**
             {"RB", {-3.138` +
             0.253` HistFppAvgRange3gb +
             0.196` HistFppAvgRange6gb +
             0.366` HistRecTargetsAvgRange3gb +
             4.377` HistRushAttemptsPercentMaxAvgRange11gb -
             0.048` PointSpread +
             0.191` PredFppAllowedRbAvgRange31gb +
             3.61` StatsAthleteId267443Q +
             2.842` StatsAthleteId269221Q +
             5.144` StatsAthleteId300346Q +
             3.966` StatsAthleteId332757Q +
             4.203` StatsAthleteId332959Q}},
             */
            pred = -3.138f
                    + (0.253f * (HistFppAvgRange3gb == -99f ? 0f : HistFppAvgRange3gb))
                    + (0.196f * (HistFppAvgRange6gb == -99f ? 0f : HistFppAvgRange6gb))
                    + (0.366f * (HistRecTargetsAvgRange3gb == -99f ? 0f : HistRecTargetsAvgRange3gb))
                    + (4.377f * (HistRushAttemptsPercentMaxAvgRange11gb == -99f ? 0f : HistRushAttemptsPercentMaxAvgRange11gb))
                    + (0.048f * prediction.getPointSpread())
                    + (0.191f * (PredFppAllowedRbAvgRange31gb == -99f ? 0f : PredFppAllowedRbAvgRange31gb))
                    + (3.61f * StatsAthleteId267443Q)
                    + (2.842f * StatsAthleteId269221Q)
                    + (5.144f * StatsAthleteId300346Q)
                    + (3.966f * StatsAthleteId332757Q)
                    + (4.203f * StatsAthleteId332959Q);
            if (futureEvent) {
                switch (depth) {
                    case 0:
                        pred = 0;
                        break;
                    case 1:
                    case 2:
                    case 3:
                        break;
                    default:
                        pred = pred / (float) depth;
                }
            }
        } else if (position.equals("WR")) {
            /*
            {"WR", {-3.734` +
            0.396` HistFppAvgRange8gb +
            0.473 HistRecTargetsAvgRange8gb +
            0.224` PredFppAllowedWrAvgRange38gb +
            2.728` StatsAthleteId227442Q -
            1.06` Top15HistFppAvgRange3gb}},
             */
            pred = -3.734f
                    + (0.396f * (HistFppAvgRange8gb == -99f ? 0f : HistFppAvgRange8gb))
                    + (0.473f * (HistRecTargetsAvgRange8gb == -99f ? 0f : HistRecTargetsAvgRange8gb))
                    + (0.224f * (PredFppAllowedWrAvgRange38gb == -99f ? 0f : PredFppAllowedWrAvgRange38gb))
                    + (2.728f * StatsAthleteId227442Q)
                    + (1.06f * Top15HistFppAvgRange3gb);
            if (futureEvent) {
                switch (depth) {
                    case 0:
                        pred = 0;
                        break;
                    default:
                        pred = pred / (float) depth;
                }
            }
        } else if (position.equals("TE")) {
            /*
            {"TE", {-1.367` +
            0.285 HistRecTargetsAvgRange4gb +
            1.362` HistRecTargetsPercentMaxAvgRange11gb +
            0.04` HistRecYardsAvgRange7gb +
            0.323` PredFppAllowedTeAvgRange34gb +
            3.8 StatsAthleteId295918Q +
            6.516` StatsAthleteId381091Q +
            5.084 StatsAthleteId550870Q -
            0.964` Top30HistFppAvgRange7gb}}}
             */
            pred = -1.367f
                    + (0.285f * (HistRecTargetsAvgRange4gb == -99f ? 0 : HistRecTargetsAvgRange4gb))
                    + (1.362f * (HistRecTargetsPercentMaxAvgRange11gb == -99f ? 0 : HistRecTargetsPercentMaxAvgRange11gb))
                    + (0.04f * (HistRecYardsAvgRange7gb == -99f ? 0 : HistRecYardsAvgRange7gb))
                    + (0.323f * (PredFppAllowedTeAvgRange34gb == -99f ? 0 : PredFppAllowedTeAvgRange34gb))
                    + (3.8f * StatsAthleteId295918Q)
                    + (6.516f * StatsAthleteId381091Q)
                    + (5.084f * StatsAthleteId550870Q)
                    + (0.964f * (Top30HistFppAvgRange7gb == -99f ? 0 : Top30HistFppAvgRange7gb));
            if (futureEvent) {
                switch (depth) {
                    case 0:
                        pred = 0;
                        break;
                    case 1:
                    case 2:
                        break;
                    default:
                        pred = pred / (float) depth;
                }
            }
        }
        prediction.setProjectedFpp(pred);
    }

    private boolean isAthlete(Integer id, StatsNflAthleteByEvent stat) {
        return id.equals(stat.getAthlete().getStatProviderId());
    }
}
