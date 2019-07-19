package dao;

import com.amazonaws.services.dynamodbv2.datamodeling.S3Link;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.avaje.ebean.*;
import common.GlobalConstants;
import models.contest.Lineup;
import models.contest.LineupSpot;
import models.sports.*;
import models.stats.*;
import models.stats.mlb.StatsMlbBatting;
import models.stats.mlb.StatsMlbPitching;
import models.stats.nfl.*;
import models.stats.nfl.StatsNflProjection;
import models.stats.nfl.StatsNflProjectionDefense;
import models.stats.predictive.StatsProjection;
import org.json.JSONArray;
import org.json.JSONException;
import play.Logger;
import play.cache.Cache;
import stats.predictive.StatsEventInfo;
import utils.TimeService;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by mgiles on 7/16/14.
 */
public class StatsDao extends AbstractDao implements IStatsDao {

    @Override
    public List<StatsLiveFeedData> findLiveFeed(SportEvent sportEvent) {
        return Ebean.find(StatsLiveFeedData.class).where().eq("sportEvent", sportEvent).order("id asc").findList();
    }

    @Override
    public List<StatsLiveFeedData> findLiveFeed(List<SportEvent> sportEvents) {
        return Ebean.find(StatsLiveFeedData.class).where().in("sportEvent", sportEvents).order("id asc").findList();
    }

    @Override
    public boolean isDuplicateLiveFeed(String hash) {
        return !Ebean.find(StatsLiveFeedData.class).where().eq("dataHash", hash).findList().isEmpty();
    }

    @Override
    public List<StatsMlbBatting> findMlbBattingStats(int athleteStatProviderId, int season) {
        String startDate = String.format("%s-03-01", season);
        String endDate = String.format("%s-12-01", season);
        String sql = String.format("select b.id, b.stat_provider_id, b.event_id, b.hits_singles, b.hits_doubles, b.hits_triples, b.hits_home_runs, " +
                "b.runs_batted_in_total, b.runs_scored, b.walks_total, b.hit_by_pitch, b.stolen_bases_total, b.stolen_bases_caught_stealing " +
                "from stats_mlb_batting b inner join sport_event s on b.event_id = s.stat_provider_id " +
                "where s.start_time >= '%s' and s.start_time <= '%s' and b.stat_provider_id = %s", startDate, endDate, athleteStatProviderId);

        RawSql rawSql = RawSqlBuilder.parse(sql)
                .columnMapping("b.id", "id")
                .columnMapping("b.stat_provider_id", "statProviderId")
                .columnMapping("b.event_id", "eventId")
                .columnMapping("b.hits_singles", "hitsSingles")
                .columnMapping("b.hits_doubles", "hitsDoubles")
                .columnMapping("b.hits_triples", "hitsTriples")
                .columnMapping("b.hits_home_runs", "hitsHomeRuns")
                .columnMapping("b.runs_batted_in_total", "runsBattedInTotal")
                .columnMapping("b.runs_scored", "runsScored")
                .columnMapping("b.walks_total", "walksTotal")
                .columnMapping("b.hit_by_pitch", "hitByPitch")
                .columnMapping("b.stolen_bases_total", "stolenBasesTotal")
                .columnMapping("b.stolen_bases_caught_stealing", "stolenBasesCaughtStealing")
                .create();

        com.avaje.ebean.Query<StatsMlbBatting> query = Ebean.find(StatsMlbBatting.class);
        query.setRawSql(rawSql);
        return query.findList();
    }

    @Override
    public List<StatsMlbBatting> findMlbBattingStats(Position position, int season) {
        String startDate = String.format("%s-03-01", season);
        String endDate = String.format("%s-12-01", season);
        String sql = String.format("select b.id, b.stat_provider_id, b.event_id, b.hits_singles, b.hits_doubles, b.hits_triples, b.hits_home_runs, " +
                "b.runs_batted_in_total, b.runs_scored, b.walks_total, b.hit_by_pitch, b.stolen_bases_total, b.stolen_bases_caught_stealing " +
                "from stats_mlb_batting b inner join sport_event s on b.event_id = s.stat_provider_id " +
                "where s.start_time >= '%s' and s.start_time <= '%s' and b.stat_provider_id in (" +
                "select a.stat_provider_id from athlete a inner join athlete_x_position axp " +
                "on a.id = axp.athlete_id " +
                "inner join position p on p.id = axp.position_id " +
                "where p.id = %s" +
                ")", startDate, endDate, position.getId());

        RawSql rawSql = RawSqlBuilder.parse(sql)
                .columnMapping("b.id", "id")
                .columnMapping("b.stat_provider_id", "statProviderId")
                .columnMapping("b.event_id", "eventId")
                .columnMapping("b.hits_singles", "hitsSingles")
                .columnMapping("b.hits_doubles", "hitsDoubles")
                .columnMapping("b.hits_triples", "hitsTriples")
                .columnMapping("b.hits_home_runs", "hitsHomeRuns")
                .columnMapping("b.runs_batted_in_total", "runsBattedInTotal")
                .columnMapping("b.runs_scored", "runsScored")
                .columnMapping("b.walks_total", "walksTotal")
                .columnMapping("b.hit_by_pitch", "hitByPitch")
                .columnMapping("b.stolen_bases_total", "stolenBasesTotal")
                .columnMapping("b.stolen_bases_caught_stealing", "stolenBasesCaughtStealing")
                .create();

        com.avaje.ebean.Query<StatsMlbBatting> query = Ebean.find(StatsMlbBatting.class);
        query.setRawSql(rawSql);
        return query.findList();
    }

    @Override
    public Map<Integer, List<StatsMlbBatting>> findMlbBattingStatsAsMap(Position position, int season) {
        List<StatsMlbBatting> statsMlbBattingList = findMlbBattingStats(position, season);
        Map<Integer, List<StatsMlbBatting>> resultMap = new HashMap<>();
        for (StatsMlbBatting batting : statsMlbBattingList) {
            List<StatsMlbBatting> l = resultMap.get(batting.getStatProviderId());
            if (l == null) {
                l = new ArrayList<>();
                resultMap.put(batting.getStatProviderId(), l);
            }

            l.add(batting);
        }

        return resultMap;
    }

    @Override
    public Map<String, BigDecimal> generateNflOffenseMap(StatsNflAthleteByEvent model) {
        Map<String, BigDecimal> stats = new HashMap<>();
        stats.put(GlobalConstants.SCORING_NFL_KICK_RETURN_TOUCHDOWN_LABEL, new BigDecimal(model.getKickoffReturningTouchdowns()));
        stats.put(GlobalConstants.SCORING_NFL_LOST_FUMBLE_LABEL, new BigDecimal(model.getFumblesLostTotal()));
        stats.put(GlobalConstants.SCORING_NFL_PASSING_TOUCHDOWN_LABEL, new BigDecimal(model.getPassingTouchdowns()));
        stats.put(GlobalConstants.SCORING_NFL_PASSING_YARDS_LABEL, new BigDecimal(model.getPassingYards()));
        stats.put(GlobalConstants.SCORING_NFL_PUNT_RETURN_TOUCHDOWN_LABEL, new BigDecimal(model.getPuntReturningTouchdowns()));
        stats.put(GlobalConstants.SCORING_NFL_RECEIVING_TOUCHDOWN_LABEL, new BigDecimal(model.getReceivingTouchdowns()));
        stats.put(GlobalConstants.SCORING_NFL_RECEIVING_YARDS_LABEL, new BigDecimal(model.getReceivingYards()));
        stats.put(GlobalConstants.SCORING_NFL_RUSHING_TOUCHDOWN_LABEL, new BigDecimal(model.getRushingTouchdowns()));
        stats.put(GlobalConstants.SCORING_NFL_RUSHING_YARDS_LABEL, new BigDecimal(model.getRushingYards()));
        stats.put(GlobalConstants.SCORING_NFL_TWO_POINT_CONVERSION_LABEL, new BigDecimal(model.getTwoPointConversionsMade()));

        return stats;
    }

    @Override
    public Map<String, BigDecimal> generateMlbBattingMap(StatsMlbBatting model) {
        Map<String, BigDecimal> stats = new HashMap<>();
        stats.put(GlobalConstants.SCORING_MLB_SINGLE_LABEL, new BigDecimal(model.getHitsSingles()));
        stats.put(GlobalConstants.SCORING_MLB_DOUBLE_LABEL, new BigDecimal(model.getHitsDoubles()));
        stats.put(GlobalConstants.SCORING_MLB_TRIPLE_LABEL, new BigDecimal(model.getHitsTriples()));
        stats.put(GlobalConstants.SCORING_MLB_HOMERUN_LABEL, new BigDecimal(model.getHitsHomeRuns()));
        stats.put(GlobalConstants.SCORING_MLB_RUN_BATTED_IN_LABEL, new BigDecimal(model.getRunsBattedInTotal()));
        stats.put(GlobalConstants.SCORING_MLB_RUN_LABEL, new BigDecimal(model.getRunsScored()));
        stats.put(GlobalConstants.SCORING_MLB_WALK_LABEL, new BigDecimal(model.getWalksTotal()));
        stats.put(GlobalConstants.SCORING_MLB_HIT_BY_PITCH_LABEL, new BigDecimal(model.getHitByPitch()));
        stats.put(GlobalConstants.SCORING_MLB_STOLEN_BASE_LABEL, new BigDecimal(model.getStolenBasesTotal()));
        stats.put(GlobalConstants.SCORING_MLB_STOLEN_BASE_LABEL, new BigDecimal(model.getStolenBasesCaughtStealing()));

        return stats;
    }

    @Override
    public Map<String, BigDecimal> generateMlbPitchingMap(StatsMlbPitching model) {
        Map<String, BigDecimal> stats = new HashMap<>();
        stats.put(GlobalConstants.SCORING_MLB_INNING_PITCHED_LABEL, new BigDecimal(model.getInningsPitched()));
        stats.put(GlobalConstants.SCORING_MLB_STRIKEOUT_LABEL, new BigDecimal(model.getStrikeoutsTotal()));
        stats.put(GlobalConstants.SCORING_MLB_EARNED_RUN_LABEL, new BigDecimal(model.getRunsAllowedEarnedRuns()));
        stats.put(GlobalConstants.SCORING_MLB_PITCHER_HIT_LABEL, new BigDecimal(model.getHitsAllowedTotal()));
        stats.put(GlobalConstants.SCORING_MLB_PITCHER_WALK_LABEL, new BigDecimal(model.getWalksTotal()));
        stats.put(GlobalConstants.SCORING_MLB_PITCHER_HIT_BY_PITCH_LABEL, new BigDecimal(model.getHitBatsmen()));

        return stats;
    }

    @Override
    public List<StatsMlbPitching> findMlbPitchingStats(int athleteStatProviderId, int season) {
        String startDate = String.format("%s-03-01", season);
        String endDate = String.format("%s-12-01", season);
        String sql = String.format("select p.id, p.stat_provider_id, p.event_id, p.hits_allowed_total, p.innings_pitched, p.runs_allowed_earned_runs, p.walks_total, p.hit_batsmen " +
                "from stats_mlb_pitching p inner join sport_event s on p.event_id = s.stat_provider_id " +
                "where s.start_time >= '%s' and s.start_time <= '%s' and p.stat_provider_id = %s", startDate, endDate, athleteStatProviderId);

        RawSql rawSql = RawSqlBuilder.parse(sql)
                .columnMapping("p.id", "id")
                .columnMapping("p.stat_provider_id", "statProviderId")
                .columnMapping("p.event_id", "eventId")
                .columnMapping("p.hits_allowed_total", "hitsAllowedTotal")
                .columnMapping("p.innings_pitched", "inningsPitched")
                .columnMapping("p.runs_allowed_earned_runs", "runsAllowedEarnedRuns")
                .columnMapping("p.walks_total", "walksTotal")
                .columnMapping("p.hit_batsmen", "hitBatsmen")
                .create();

        com.avaje.ebean.Query<StatsMlbPitching> query = Ebean.find(StatsMlbPitching.class);
        query.setRawSql(rawSql);
        return query.findList();
    }

    @Override
    public Map<Integer, List<StatsMlbPitching>> findMlbPitchingStats(int season) {
        String startDate = String.format("%s-03-01", season);
        String endDate = String.format("%s-12-01", season);
        String sql = String.format("select p.id, p.stat_provider_id, p.event_id, p.hits_allowed_total, p.innings_pitched, p.runs_allowed_earned_runs, p.walks_total, p.hit_batsmen " +
                "from stats_mlb_pitching p inner join sport_event s on p.event_id = s.stat_provider_id " +
                "where s.start_time >= '%s' and s.start_time <= '%s'", startDate, endDate);

        RawSql rawSql = RawSqlBuilder.parse(sql)
                .columnMapping("p.id", "id")
                .columnMapping("p.stat_provider_id", "statProviderId")
                .columnMapping("p.event_id", "eventId")
                .columnMapping("p.hits_allowed_total", "hitsAllowedTotal")
                .columnMapping("p.innings_pitched", "inningsPitched")
                .columnMapping("p.runs_allowed_earned_runs", "runsAllowedEarnedRuns")
                .columnMapping("p.walks_total", "walksTotal")
                .columnMapping("p.hit_batsmen", "hitBatsmen")
                .create();

        com.avaje.ebean.Query<StatsMlbPitching> query = Ebean.find(StatsMlbPitching.class);
        query.setRawSql(rawSql);
        List<StatsMlbPitching> statsMlbPitchingList = query.findList();
        Map<Integer, List<StatsMlbPitching>> resultMap = new HashMap<>();
        for (StatsMlbPitching pitching : statsMlbPitchingList) {
            List<StatsMlbPitching> l = resultMap.get(pitching.getStatProviderId());
            if (l == null) {
                l = new ArrayList<>();
                resultMap.put(pitching.getStatProviderId(), l);
            }

            l.add(pitching);
        }

        return resultMap;
    }

    @Override
    public List<StatsNflAthleteByEvent> findStatsNflAthleteByEvents() {
        List<StatsNflAthleteByEvent> events = (List<StatsNflAthleteByEvent>) Cache.get("findStatsNflAthleteByEvents");
        if (events == null) {
            events = Ebean.find(StatsNflAthleteByEvent.class).orderBy("startTime desc").findList();
            Cache.set("findStatsNflAthleteByEvents", events, 7200);
        }
        return events;
    }

    @Override
    public List<StatsNflAthleteByEvent> findStatsNflAthleteByEvents(Position position, Integer season) {
        List<StatsNflAthleteByEvent> events = (List<StatsNflAthleteByEvent>) Cache.get("findStatsNflAthleteByEvents" + position.getAbbreviation() + "_" + season);
        if (events == null) {
            events = Ebean.find(StatsNflAthleteByEvent.class).where().eq("season", season).eq("position", position.getAbbreviation()).findList();
            Cache.set("findStatsNflAthleteByEvents" + position.getAbbreviation() + "_" + season, events, 7200);
        }
        return events;
    }

    @Override
    public List<StatsNflAthleteByEvent> findStatsNflAthleteByEvents(SportEvent sportEvent, Team team, String position) {
        List<StatsNflAthleteByEvent> events = (List<StatsNflAthleteByEvent>) Cache.get("findStatsNflAthleteByEvents" + team.getStatProviderId() + "_" + sportEvent.getStatProviderId() + "_" + position);
        if (events == null) {
            events = Ebean.find(StatsNflAthleteByEvent.class).where()
                    .eq(StatsNflAthleteByEvent.SPORT_EVENT_ID, sportEvent.getId())
                    .eq(StatsNflAthleteByEvent.TEAM_ID, team.getId())
                    .eq("position", position.toUpperCase()).findList();
            Cache.set("findStatsNflAthleteByEvents" + team.getStatProviderId() + "_" + sportEvent.getStatProviderId() + "_" + position, events, 7200);
        }
        return events;
    }

    @Override
    public List<StatsNflAthleteByEvent> findStatsNflAthleteByEvents(Athlete athlete) {
        List<StatsNflAthleteByEvent> events = (List<StatsNflAthleteByEvent>) Cache.get("findStatsNflAthleteByEvents" + athlete.getStatProviderId());
        if (events == null) {
            events = Ebean.find(StatsNflAthleteByEvent.class).where()
                    .eq(StatsNflAthleteByEvent.ATHLETE_ID, athlete.getId())
                    .orderBy("startTime desc").findList();
            Cache.set("findStatsNflAthleteByEvents" + athlete.getStatProviderId(), events, 7200);
        }
        return events;
    }

    @Override
    public StatsNflAthleteByEvent findStatsNflAthleteByEvent(Athlete athlete, SportEvent sportEvent) {
        StatsNflAthleteByEvent event = (StatsNflAthleteByEvent) Cache.get("findStatsNflAthleteByEvents" + athlete.getStatProviderId() + "_" + sportEvent.getStatProviderId());
        if (event == null) {
            event = Ebean.find(StatsNflAthleteByEvent.class).where()
                    .eq(StatsNflAthleteByEvent.UNIQUE_KEY,
                            athlete.getStatProviderId() + "_" + sportEvent.getStatProviderId()).findUnique();
            Cache.set("findStatsNflAthleteByEvents" + athlete.getStatProviderId() + "_" + sportEvent.getStatProviderId(), event, 7200);
        }
        return event;
    }

    @Override
    public List<StatsNflAthleteByEvent> findStatsNflAthleteByEvents(Athlete athlete, Integer season) {
        List<StatsNflAthleteByEvent> events = (List<StatsNflAthleteByEvent>) Cache.get("findStatsNflAthleteByEvents" + athlete.getStatProviderId() + "_" + season);
        if (events == null) {
            events = Ebean.find(StatsNflAthleteByEvent.class).where()
                    .eq(StatsNflAthleteByEvent.ATHLETE_ID, athlete.getId()).eq("season", season).findList();
            Cache.set("findStatsNflAthleteByEvents" + athlete.getStatProviderId() + "_" + season, events, 7200);
        }
        return events;
    }

    @Override
    public List<StatsNflAthleteByEvent> findStatsNflAthleteByEvents(Athlete athlete, String asc_desc) {
        List<StatsNflAthleteByEvent> events = (List<StatsNflAthleteByEvent>) Cache.get("findStatsNflAthleteByEvents" + athlete.getStatProviderId() + asc_desc);
        if (events == null) {
            events = Ebean.find(StatsNflAthleteByEvent.class).where()
                    .eq(StatsNflAthleteByEvent.ATHLETE_ID, athlete.getId())
                    .orderBy("startTime " + asc_desc).findList();
            Cache.set("findStatsNflAthleteByEvents" + athlete.getStatProviderId() + asc_desc, events, 7200);
        }
        return events;
    }

    @Override
    public List<StatsNflAthleteByEvent> findStatsNflAthleteByEvents(Athlete athlete, Date startTime, int lastNfromEvent, Integer[] eventTypeIds) {
        String s = "";
        for (Integer eid : eventTypeIds) {
            s += eid + "";
        }
        List<StatsNflAthleteByEvent> events = (List<StatsNflAthleteByEvent>) Cache.get("findStatsNflAthleteByEvents" + athlete.getStatProviderId() + startTime + s);
        if (events == null) {
            List<Integer> eventTypeIdsList = new ArrayList<>();
            for (int eventType : eventTypeIds) eventTypeIdsList.add(eventType);

            events = Ebean.find(StatsNflAthleteByEvent.class).where()
                    .eq(StatsNflAthleteByEvent.ATHLETE_ID, athlete.getId())
                    .le("startTime", startTime)
                    .in("eventTypeId", eventTypeIdsList)
                    .orderBy("startTime desc").setMaxRows(lastNfromEvent).findList();
            Cache.set("findStatsNflAthleteByEvents" + athlete.getStatProviderId() + startTime + s, events, 7200);
        }
        return events;
    }

    @Override
    public StatsNflDefenseByEvent findStatsNflDefenseByEvent(Athlete athlete, SportEvent sportEvent) {
        return Ebean.find(StatsNflDefenseByEvent.class).where().eq("athlete", athlete).eq("sportEvent", sportEvent).findUnique();
    }

    @Override
    public List<StatsNflDefenseByEvent> findStatsNflDefenseByEvent(Athlete athlete, String startTimeSortOrder) {
        return Ebean.find(StatsNflDefenseByEvent.class).where().eq("athlete", athlete).order("startTime " + startTimeSortOrder).findList();
    }

    @Override
    public List<StatsNflDefenseByEvent> findStatsNflDefenseByEvent(Athlete athlete, Date startTime, int lastNfromEvent, Integer[] eventTypeIds) {
        List<Integer> eventTypeIdsList = new ArrayList<>();
        for (int eventType : eventTypeIds) eventTypeIdsList.add(eventType);

        return Ebean.find(StatsNflDefenseByEvent.class).where()
                .eq(StatsNflAthleteByEvent.ATHLETE_ID, athlete.getId())
                .lt("startTime", startTime)
                .in("eventTypeId", eventTypeIdsList)
                .orderBy("startTime desc").setMaxRows(lastNfromEvent).findList();
    }

    @Override
    public StatsEventInfo findNflNextSportEvent(StatsNflAthleteByEvent stat, Integer[] eventTypeIds) {
        String s0 = "";
        for (Integer eid : eventTypeIds) {
            s0 += eid + "";
        }
        StatsEventInfo statsEventInfo = (StatsEventInfo) Cache.get("StatsEventInfo" + stat.getAthlete().getStatProviderId() + s0);
        if (statsEventInfo == null) {
            List<Integer> eventTypeIdsList = new ArrayList<>();
            for (int eventType : eventTypeIds) eventTypeIdsList.add(eventType);

            List<StatsNflAthleteByEvent> athleteStatsSorted;
            if (eventTypeIds.length > 0) {
                athleteStatsSorted = Ebean.find(StatsNflAthleteByEvent.class).where()
                        .eq(StatsNflAthleteByEvent.ATHLETE_ID, stat.getAthlete().getId())
                        .in("eventTypeId", eventTypeIdsList).orderBy("startTime asc").findList();
            } else {
                athleteStatsSorted = Ebean.find(StatsNflAthleteByEvent.class).where()
                        .eq(StatsNflAthleteByEvent.ATHLETE_ID, stat.getAthlete().getId()).orderBy("startTime asc").findList();
            }
            statsEventInfo = new StatsEventInfo();
            StatsNflAthleteByEvent nextEvent = null;
            for (int i = 0; i < athleteStatsSorted.size(); i++) {
                StatsNflAthleteByEvent s = athleteStatsSorted.get(i);
                if (s.getSportEvent().equals(stat.getSportEvent())) {
                    if (i + 1 < athleteStatsSorted.size()) {
                        nextEvent = athleteStatsSorted.get(i + 1);
                    }
                    break;
                }
            }
            if (nextEvent != null) {
                statsEventInfo.setSportEvent(nextEvent.getSportEvent());
                statsEventInfo.setOpponentId(nextEvent.getOpponentId());
                statsEventInfo.setStartTime(nextEvent.getStartTime());
                return statsEventInfo;
            }


            Athlete athlete = stat.getAthlete();
            if (athlete.getTeam() == null) {
                return null;
            }

            SportEvent nextSportEvent = DaoFactory.getSportsDao().findNextFutureSportEvent(athlete, eventTypeIdsList);
            int opponentId = stat.getTeam().equals(nextSportEvent.getTeams().get(0)) ?
                    nextSportEvent.getTeams().get(1).getStatProviderId() :
                    nextSportEvent.getTeams().get(0).getStatProviderId();

            statsEventInfo.setSportEvent(nextSportEvent);
            statsEventInfo.setOpponentId(opponentId);
            statsEventInfo.setStartTime(nextSportEvent.getStartTime());
            Cache.set("StatsEventInfo" + stat.getAthlete().getStatProviderId() + s0, statsEventInfo, 7200);
        }
        return statsEventInfo;
    }

    @Override
    public StatsEventInfo findNflNextSportEvent(StatsNflDefenseByEvent stat, Integer[] eventTypeIds) {
        String s0 = "";
        for (Integer eid : eventTypeIds) {
            s0 += eid + "";
        }
        StatsEventInfo statsEventInfo = (StatsEventInfo) Cache.get("statsDefEventInfo" + stat.getAthlete().getStatProviderId() + s0);
        if (statsEventInfo == null) {
            List<Integer> eventTypeIdsList = new ArrayList<>();
            for (int eventType : eventTypeIds) eventTypeIdsList.add(eventType);

            List<StatsNflDefenseByEvent> athleteStatsSorted;
            if (eventTypeIds.length > 0) {
                athleteStatsSorted = Ebean.find(StatsNflDefenseByEvent.class).where()
                        .eq(StatsNflAthleteByEvent.ATHLETE_ID, stat.getAthlete().getId())
                        .in("eventTypeId", eventTypeIdsList).orderBy("startTime asc").findList();
            } else {
                athleteStatsSorted = Ebean.find(StatsNflDefenseByEvent.class).where()
                        .eq(StatsNflAthleteByEvent.ATHLETE_ID, stat.getAthlete().getId()).orderBy("startTime asc").findList();
            }
            statsEventInfo = new StatsEventInfo();
            StatsNflDefenseByEvent nextEvent = null;
            for (int i = 0; i < athleteStatsSorted.size(); i++) {
                StatsNflDefenseByEvent s = athleteStatsSorted.get(i);
                if (s.getSportEvent().equals(stat.getSportEvent())) {
                    if (i + 1 < athleteStatsSorted.size()) {
                        nextEvent = athleteStatsSorted.get(i + 1);
                    }
                    break;
                }
            }
            if (nextEvent != null) {
                statsEventInfo.setSportEvent(nextEvent.getSportEvent());
                statsEventInfo.setOpponentId(nextEvent.getOpponent().getStatProviderId());
                statsEventInfo.setStartTime(nextEvent.getStartTime());
                return statsEventInfo;
            }


            Athlete athlete = stat.getAthlete();
            if (athlete.getTeam() == null) {
                return null;
            }
            SportEvent nextSportEvent = DaoFactory.getSportsDao().findNextFutureSportEvent(athlete, eventTypeIdsList);
            int opponentId = stat.getTeam().equals(nextSportEvent.getTeams().get(0)) ?
                    nextSportEvent.getTeams().get(1).getStatProviderId() :
                    nextSportEvent.getTeams().get(0).getStatProviderId();

            statsEventInfo.setSportEvent(nextSportEvent);
            statsEventInfo.setOpponentId(opponentId);
            statsEventInfo.setStartTime(nextSportEvent.getStartTime());
            Cache.set("statsDefEventInfo" + stat.getAthlete().getStatProviderId() + s0, statsEventInfo, 7200);
        }
        return statsEventInfo;
    }

    @Override
    public Boolean participatedInLastTwoEvents(StatsNflAthleteByEvent stat) {
        Integer[] eventTypeIds = {GlobalConstants.EVENT_TYPE_NFL_POST_SEASON, GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON};
        String sql = "SELECT s.id, " +
                "s.start_time, " +
                "s.event_type_id, " +
                "s.stat_provider_id " +
                "FROM sport_event s JOIN sport_event_x_team" +
                " b ON s.id = b.sport_event_id JOIN team t ON t.id = b.team_id WHERE t.stat_provider_id="
                + stat.getTeam().getStatProviderId()
                + " AND s.start_time < '" + Timestamp.from(stat.getStartTime().toInstant()) + "'"
                + " AND s.event_type_id IN "
                + toInString(eventTypeIds)
                + " ORDER BY s.start_time DESC LIMIT "
                + 2 + ";";

        RawSql rawSql = RawSqlBuilder.parse(sql).create();
        com.avaje.ebean.Query<SportEvent> query =
                Ebean.find(SportEvent.class)
                        .setUseQueryCache(true)
                        .setUseCache(true);
        query.setRawSql(rawSql);
        List<SportEvent> events = query.findList();

        for (SportEvent event : events) {
            StatsNflAthleteByEvent lastStat = findStatsNflAthleteByEvent(stat.getAthlete(), event);
            if (lastStat != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Integer getNflPredictionCount() {
        return Ebean.find(StatsNflProjection.class).findRowCount();
    }

    @Override
    public String clearNflPredictionTable() {
        String sql = "DELETE FROM stats_nfl_projection;";
        SqlUpdate update = Ebean.getServer(GlobalConstants.DEFAULT_DATABASE)
                .createSqlUpdate(sql);
        Ebean.getServer(GlobalConstants.DEFAULT_DATABASE).execute(update);
        int rowCount = Ebean.find(StatsNflProjection.class).findRowCount();
        return "There are " + rowCount + " rows remaining in the stats_nfl_projection table";
    }

    @Override
    public String clearNflDefPredictionTable() {
        String sql = "DELETE FROM stats_nfl_projection_defense;";
        SqlUpdate update = Ebean.getServer(GlobalConstants.DEFAULT_DATABASE)
                .createSqlUpdate(sql);
        Ebean.getServer(GlobalConstants.DEFAULT_DATABASE).execute(update);
        int rowCount = Ebean.find(StatsNflProjectionDefense.class).findRowCount();
        return "There are " + rowCount + " rows remaining in the stats_nfl_projection_defense table";
    }

    @Override
    public String clearNflStatsTable() {
        String sql = "DELETE FROM stats_nfl_athlete_by_event;";
        SqlUpdate update = Ebean.getServer(GlobalConstants.DEFAULT_DATABASE)
                .createSqlUpdate(sql);
        Ebean.getServer(GlobalConstants.DEFAULT_DATABASE).execute(update);
        int rowCount = Ebean.find(StatsNflAthleteByEvent.class).findRowCount();
        return "There are " + rowCount + " rows remaining in the stats_nfl_athlete_by_event table";
    }

    @Override
    public String clearNflDefStatsTable() {
        String sql = "DELETE FROM stats_nfl_defense_by_event;";
        SqlUpdate update = Ebean.getServer(GlobalConstants.DEFAULT_DATABASE)
                .createSqlUpdate(sql);
        Ebean.getServer(GlobalConstants.DEFAULT_DATABASE).execute(update);
        int rowCount = Ebean.find(StatsNflDefenseByEvent.class).findRowCount();
        return "There are " + rowCount + " rows remaining in the stats_nfl_defense_by_event table";
    }

    @Override
    public Map<SportEvent, Team> findLastSportEventsByOpponent(Integer opponentId,
                                                               int lastNOpponents,
                                                               Integer[] eventTypeIds) {
        String s0 = "";
        for (Integer eid : eventTypeIds) {
            s0 += eid + "";
        }
        Map<SportEvent, Team> eventTeamMap = (Map<SportEvent, Team>) Cache.get("eventTeamMap" + opponentId + "_" + lastNOpponents + s0);
        if (eventTeamMap == null) {
            eventTeamMap = new LinkedHashMap<>();

            String sql = "SELECT s.id, " +
                    "s.stat_provider_id, " +
                    "s.start_time, " +
                    "s.short_description, " +
                    "s.description, " +
                    "s.units_remaining, " +
                    "s.complete, " +
                    "s.week, " +
                    "s.season, " +
                    "s.event_type_id " +
                    "FROM sport_event s JOIN sport_event_x_team" +
                    " b ON s.id = b.sport_event_id JOIN team t ON t.id = b.team_id WHERE t.stat_provider_id="
                    + opponentId
                    + " AND s.start_time < '"
                    + Timestamp.from(new TimeService().getNow()) + "'"
                    + " AND s.event_type_id IN "
                    + toInString(eventTypeIds)
                    + " ORDER BY s.start_time DESC LIMIT "
                    + lastNOpponents + ";";

            RawSql rawSql = RawSqlBuilder.parse(sql).create();
            com.avaje.ebean.Query<SportEvent> query =
                    Ebean.find(SportEvent.class)
                            .setUseQueryCache(true)
                            .setUseCache(true);
            query.setRawSql(rawSql);
            List<SportEvent> events = query.findList();

            for (SportEvent event : events) {
                event.setLeague(League.NFL);
                eventTeamMap.put(event, opponentId.equals(event.getTeams()
                        .get(0).getStatProviderId()) ? event.getTeams().get(1) : event.getTeams().get(0));
            }
            Cache.set("eventTeamMap" + opponentId + "_" + lastNOpponents + s0, eventTeamMap, 7200);
        }
        return eventTeamMap;
    }

    @Override
    public StatsNflProjection findNflPrediction(SportEvent sportEvent, Athlete athlete) {
        return Ebean.find(StatsNflProjection.class).where()
                .eq(StatsNflProjection.UNIQUE_KEY,
                        athlete.getStatProviderId() + "_" + sportEvent.getStatProviderId()).findUnique();
    }

    @Override
    public List<StatsNflProjection> findNextNflPredictions(Athlete athlete) {
        return Ebean.find(StatsNflProjection.class).where()
                .eq("athlete_id", athlete.getId())
                .gt("start_time", Timestamp.from(new TimeService().getNow()))
                .orderBy("start_time asc")
                .findList();
    }

    @Override
    public List<StatsNflProjection> findNflPredictions(SportEvent sportEvent) {
        return Ebean.find(StatsNflProjection.class).where()
                .eq(StatsNflProjection.SPORT_EVENT_ID, sportEvent.getId()).findList();
    }

    @Override
    public StatsNflProjectionDefense findNflPredictionDefense(SportEvent sportEvent, Athlete athlete) {
        return Ebean.find(StatsNflProjectionDefense.class).where().eq("sportEvent", sportEvent).eq("athlete", athlete).findUnique();
    }

    @Override
    public List<StatsNflProjectionDefense> findNflPredictionDefense(SportEvent sportEvent) {
        return Ebean.find(StatsNflProjectionDefense.class).where().eq("sportEvent", sportEvent).findList();
    }

    @Override
    public List<StatsNflDefenseByEvent> findLastStatsNflDefenseByEventsByOpponent(Team opponent, Date startTime, int lastNEvents) {
        return Ebean.find(StatsNflDefenseByEvent.class)
                .where().eq("team", opponent).lt("startTime", startTime).orderBy("startTime desc").setMaxRows(lastNEvents).findList();
    }

    @Override
    public List<List> getRucksackStats(Athlete athlete) {
        List<List> stats = (List<List>) Cache.get("RucksackStats" + athlete.getStatProviderId());
        if (stats == null) {
            stats = new LinkedList<>();
            if (athlete.getTeam().getLeague().equals(League.NFL)) {
                try {
                    //TODO: remove preseason
                    Integer[] iSeasons = {GlobalConstants.EVENT_TYPE_NFL_PRE_SEASON,
                            GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON,
                            GlobalConstants.EVENT_TYPE_NFL_POST_SEASON};

                    List<StatsNflAthleteByEvent> nflStatsArr = findStatsNflAthleteByEvents(athlete, new Date(), 1, iSeasons);
                    if (nflStatsArr != null && nflStatsArr.size() > 0) {
                        StatsNflAthleteByEvent nflStats = nflStatsArr.get(0);
                        SportEvent nextEvent = DaoFactory.getSportsDao().findNextFutureSportEvent(nflStats.getAthlete(), Arrays.asList(iSeasons));
                        StatsNflDepthChart depthChart = DaoFactory.getStatsDao()
                                .findStatsNflDepthChart(nflStats.getAthlete(),
                                        nextEvent.getSeason(), nextEvent.getWeek(), nextEvent.getEventTypeId());
                        if (depthChart != null) {
                            String pos = depthChart.getDepthPosition();
                                pos = pos == null ? nflStats.getAthlete().getPositions().get(0).getAbbreviation() : pos;
                            if (pos.equals("QB")) {
                                if (depthChart.getDepth() != 1) {
                                    stats = getDummyStats();
                                    Cache.set("RucksackStats" + athlete.getStatProviderId(), stats, 7200);
                                    return stats;
                                }
                            } else if (pos.equals("RB")) {
                                if (depthChart.getDepth() > 2 || depthChart.getDepth() == 0) {
                                    stats = getDummyStats();
                                    Cache.set("RucksackStats" + athlete.getStatProviderId(), stats, 7200);
                                    return stats;
                                }
                            } else if (pos.equals("FB")) {
                                if (depthChart.getDepth() != 1) {
                                    stats = getDummyStats();
                                    Cache.set("RucksackStats" + athlete.getStatProviderId(), stats, 7200);
                                    return stats;
                                }
                            } else if (pos.startsWith("WR")) {
                                if (depthChart.getDepth() > 2 || depthChart.getDepth() == 0) {
                                    stats = getDummyStats();
                                    Cache.set("RucksackStats" + athlete.getStatProviderId(), stats, 7200);
                                    return stats;
                                }
                            } else if (pos.equals("TE")) {
                                if (depthChart.getDepth() != 1) {
                                    stats = getDummyStats();
                                    Cache.set("RucksackStats" + athlete.getStatProviderId(), stats, 7200);
                                    return stats;
                                }
                            } else if (pos.equals("TE2")) {
                                stats = getDummyStats();
                                Cache.set("RucksackStats" + athlete.getStatProviderId(), stats, 7200);
                                return stats;
                            } else {
                                stats = getDummyStats();
                                Cache.set("RucksackStats" + athlete.getStatProviderId(), stats, 7200);
                                return stats;
                            }
                        }
                        String s1 = nflStats.getOpponentPointsAllowedAtPositionAvgRange() != null
                                && nflStats.getOpponentPointsAllowedAtPositionAvgRange().startsWith("[")
                                ? nflStats.getOpponentPointsAllowedAtPositionAvgRange() : "[]";
                        JSONArray paap = new JSONArray(s1);
                        String s2 = nflStats.getTouchDownsAvgRange() != null
                                && nflStats.getTouchDownsAvgRange().startsWith("[")
                                ? nflStats.getTouchDownsAvgRange() : "[]";
                        JSONArray td = new JSONArray(s2);
                        String s3 = nflStats.getPassingYardsAvgRange() != null
                                && nflStats.getPassingYardsAvgRange().startsWith("[")
                                ? nflStats.getPassingYardsAvgRange() : "[]";
                        JSONArray py = new JSONArray(s3);
                        String s4 = nflStats.getRushingYardsAvgRange() != null
                                && nflStats.getRushingYardsAvgRange().startsWith("[")
                                ? nflStats.getRushingYardsAvgRange() : "[]";
                        JSONArray ruy = new JSONArray(s4);
                        String s5 = nflStats.getReceivingYardsAvgRange() != null
                                && nflStats.getReceivingYardsAvgRange().startsWith("[")
                                ? nflStats.getReceivingYardsAvgRange() : "[]";
                        JSONArray rey = new JSONArray(s5);

                        LinkedList<Double> tdList = new LinkedList<>();
                        LinkedList<Double> pyList = new LinkedList<>();
                        LinkedList<Double> ruyList = new LinkedList<>();
                        LinkedList<Double> reyList = new LinkedList<>();
                        LinkedList<Double> paapList = new LinkedList<>();

                        // 1, 3, 5, 10, 15, 20, and 25 game averages
                        int[] times = {0, 2, 4, 9, 14, 19, 24};
                        for (int i : times) {
                            if (py.length() > 0) {
                                pyList.add(py.length() > i ? py.getDouble(i) : py.getDouble(py.length() - 1));
                            } else {
                                pyList.add(0.0);
                            }
                            if (ruy.length() > 0) {
                                ruyList.add(ruy.length() > i ? ruy.getDouble(i) : ruy.getDouble(ruy.length() - 1));
                            } else {
                                ruyList.add(0.0);
                            }
                            if (rey.length() > 0) {
                                reyList.add(rey.length() > i ? rey.getDouble(i) : rey.getDouble(rey.length() - 1));
                            } else {
                                reyList.add(0.0);
                            }
                            if (td.length() > 0) {
                                tdList.add(td.length() > i ? td.getDouble(i) : td.getDouble(td.length() - 1));
                            } else {
                                tdList.add(0.0);
                            }
                            if (paap.length() > 0) {
                                paapList.add(paap.length() > i ? paap.getDouble(i) : paap.getDouble(paap.length() - 1));
                            } else {
                                paapList.add(0.0);
                            }
                        }
                        stats.add(pyList);
                        stats.add(ruyList);
                        stats.add(reyList);
                        stats.add(tdList);
                        stats.add(paapList);
                    } else {
                        stats = getDummyStats();
                    }
                } catch (Exception e) {
                    Logger.error("ERROR: ", e);
                    stats = getDummyStats();
                }
            } else {
                // return dummy data
                stats = getDummyStats();
            }
            Cache.set("RucksackStats" + athlete.getStatProviderId(), stats, 7200);
        }
        return stats;
    }

    @Override
    public List<SportEvent> findDistinctEvents() {
        List<SportEvent> returnList = new ArrayList<>();
        List<StatsNflAthleteByEvent> allEvents = findStatsNflAthleteByEvents();
        for (StatsNflAthleteByEvent event : allEvents) {
            if (!returnList.contains(event.getSportEvent())) {
                returnList.add(event.getSportEvent());
            }
        }
        return returnList;
    }

    private List<List> getDummyStats() {
        List<List> stats = new ArrayList<>();
        // return dummy data
        for (int i = 0; i < 7; i++) {
            List<Double> list = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                list.add(0.0);
            }
            stats.add(list);
        }
        return stats;
    }

    @Override
    public void saveStatsNflDepthChartsRaw(DynamoStatsNflDepthChartRaw raw, String rawData) {
        DynamoStatsNflDepthChartRaw dynamoRaw = dynamoDBMapper
                .load(DynamoStatsNflDepthChartRaw.class, raw.getId());
        if (dynamoRaw == null) {
            dynamoRaw = raw;
            S3Link s3link = dynamoDBMapper
                    .createS3Link(GlobalConstants.STATS_INC_S3_CACHE_BUCKET,
                            "StatsNflDeptChartRaw/" + dynamoRaw.getId() + ".json");
            dynamoRaw.setS3Link(s3link);
            // Only Prod/Master can update Dynamo
            if (DaoFactory.isMaster()) {
                dynamoRaw.getS3Link().uploadFrom(rawData.getBytes());
                dynamoDBMapper.save(dynamoRaw);
            }
        }
    }

    @Override
    public StatsNflDepthChart findStatsNflDepthChart(Athlete athlete, int season, int week, int eventTypeId) {
        StatsNflDepthChart statsNflDepthChart =
                (StatsNflDepthChart) Cache.get("StatsNflDepthChart" + athlete.getStatProviderId() + "_" + season + "_" + week + "_" + eventTypeId);
        if (statsNflDepthChart == null) {
            statsNflDepthChart = Ebean.find(StatsNflDepthChart.class)
                    .where()
                    .eq(StatsNflDepthChart.ATHLETE_ID, athlete.getId())
                    .eq("season", season)
                    .eq("week", week)
                    .eq("eventTypeId", eventTypeId)
                    .findUnique();
            Cache.set("StatsNflDepthChart" + athlete.getStatProviderId() + "_" + season + "_" + week + "_" + eventTypeId, statsNflDepthChart, 7200);
        }
        return statsNflDepthChart;
    }

    @Override
    public void saveStatsAthleteSeasonRaw(StatsAthleteBySeasonRaw raw) {
        // whenever saving we want to save/update to the db and to dynamo/s3
        StatsAthleteBySeasonRaw dbRaw = Ebean.find(StatsAthleteBySeasonRaw.class).where()
                .eq(StatsAthleteBySeasonRaw.UNIQUE_KEY, raw.getUniqueKey()).findUnique();
        if (dbRaw == null) {
            dbRaw = new StatsAthleteBySeasonRaw();
            dbRaw.setEventTypeId(raw.getEventTypeId());
            dbRaw.setLastFetched(raw.getLastFetched());
            dbRaw.setPreviouslyFailed(raw.isPreviouslyFailed());
            dbRaw.setRawData(raw.getRawData());
            dbRaw.setSeason(raw.getSeason());
            dbRaw.setStatsAthleteId(raw.getStatsAthleteId());
            dbRaw.setUniqueKey(raw.getUniqueKey());
        }
        DynamoStatsAthleteBySeasonRaw dbdRaw = dynamoDBMapper
                .load(DynamoStatsAthleteBySeasonRaw.class, dbRaw.getUniqueKey());
        if (dbdRaw == null) {
            dbdRaw = new DynamoStatsAthleteBySeasonRaw();
        }
        S3Link s3link = dynamoDBMapper
                .createS3Link(GlobalConstants.STATS_INC_S3_CACHE_BUCKET,
                        "StatsAthleteBySeasonRaw/" + dbRaw.getUniqueKey() + ".json");
        dbdRaw.setS3Link(s3link);
        dbdRaw.setId(dbRaw.getUniqueKey());
        dbdRaw.setEventTypeId(dbRaw.getEventTypeId());
        dbdRaw.setLastFetched(dbRaw.getLastFetched() == null ? null : dbRaw.getLastFetched().toString());
        dbdRaw.setLastUpdate(dbRaw.getLastUpdate() == null ? null : dbRaw.getLastUpdate().toString());
        dbdRaw.setPreviouslyFailed(dbRaw.isPreviouslyFailed());
        dbdRaw.setSeason(dbRaw.getSeason());
        dbdRaw.setStatsAthleteId(dbRaw.getStatsAthleteId());

        // Only Prod/Master can update Dynamo
        if (DaoFactory.isMaster()) {
            dbdRaw.getS3Link().uploadFrom(dbRaw.getRawData() == null ? "".getBytes() : dbRaw.getRawData().getBytes());
            dynamoDBMapper.save(dbdRaw);
        }
        Ebean.save(raw);
    }

    @Override
    public StatsAthleteBySeasonRaw findStatsAthleteSeasonRaw(Athlete athlete, int season, int eventTypeId) {
        String key = athlete.getStatProviderId() + "_" + season + "_" + eventTypeId;
        StatsAthleteBySeasonRaw raw = Ebean.find(StatsAthleteBySeasonRaw.class).where()
                .eq(StatsAthleteBySeasonRaw.UNIQUE_KEY, key).findUnique();
        DynamoStatsAthleteBySeasonRaw dbdRaw = dynamoDBMapper.load(DynamoStatsAthleteBySeasonRaw.class, key);

        // if we have both return the one that was updated most recently
        if (raw != null && dbdRaw != null) {
            if (raw.getLastUpdate() != null
                    && dbdRaw.getLastUpdate() != null
                    && !raw.getLastUpdate().equals(Timestamp.valueOf(dbdRaw.getLastUpdate()))
                    && raw.getLastUpdate().before(Timestamp.valueOf(dbdRaw.getLastUpdate()))) {
                // if the DB copy is older than let's update it
                return updateStatsAthleteSeasonRawFromDynamo(raw, dbdRaw);
            }
        }

        // If we don't have it in the DB let's look in the dynamo cache and re-populate the DB with it
        if (raw == null && dbdRaw != null) {
            return updateStatsAthleteSeasonRawFromDynamo(new StatsAthleteBySeasonRaw(), dbdRaw);
        }

        // if we have it in the DB but not in Dynamo let's update Dynamo
        if (raw != null && dbdRaw == null) {
            saveStatsAthleteSeasonRaw(raw);
        }
        return raw;
    }

    private StatsAthleteBySeasonRaw updateStatsAthleteSeasonRawFromDynamo(StatsAthleteBySeasonRaw raw, DynamoStatsAthleteBySeasonRaw dbdRaw) {
        S3Link s3link = dynamoDBMapper
                .createS3Link(GlobalConstants.STATS_INC_S3_CACHE_BUCKET,
                        "StatsAthleteBySeasonRaw/" + dbdRaw.getId() + ".json");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        s3link.downloadTo(bos);
        raw.setRawData(bos.toString());
        //raw.setUniqueKey(dbdRaw.getId());
        raw.setEventTypeId(dbdRaw.getEventTypeId());
        raw.setLastFetched(dbdRaw.getLastFetched() == null || dbdRaw.getLastFetched().equals("")
                ? null : Timestamp.valueOf(dbdRaw.getLastFetched()));
        raw.setPreviouslyFailed(dbdRaw.isPreviouslyFailed());
        raw.setSeason(dbdRaw.getSeason());
        raw.setStatsAthleteId(dbdRaw.getStatsAthleteId());
        Ebean.save(raw);
        return raw;
    }

    @Override
    public StatsNflGameOdds findStatsNflGameOdds(SportEvent sportEvent) {
        StatsNflGameOdds statsNflGameOdds = (StatsNflGameOdds) Cache.get("StatsNflGameOdds" + sportEvent.getStatProviderId());
        if (statsNflGameOdds == null) {
            statsNflGameOdds = Ebean.find(StatsNflGameOdds.class).where()
                    .eq("statsEventId", sportEvent.getStatProviderId()).findUnique();
            Cache.set("StatsNflGameOdds" + sportEvent.getStatProviderId(), statsNflGameOdds, 7200);
        }
        return statsNflGameOdds;
    }

    /**
     * What we want here is whether this athlete ranked in the top N in the week average from the average FPP array
     *
     * @param stat
     * @param position
     * @param ranking
     * @param weeks
     * @return
     */
    @Override
    public boolean wasRankedNOverLastWeeks(StatsNflAthleteByEvent stat, String position, int ranking, int weeks) {
        position = position == null ? stat.getPosition() : position;

        String sql = "SELECT s.id, " +
                "s.week, " +
                "s.season, " +
                "s.position, " +
                "s.event_type_id, " +
                "s.fantasy_points_avg_range " +
                "FROM stats_nfl_athlete_by_event s " +
                "WHERE s.week=" + stat.getWeek() +
                " AND s.season=" + stat.getSeason() +
                " AND s.position='" + position + "'" +
                " AND s.event_type_id=" + stat.getEventTypeId();

        RawSql rawSql = RawSqlBuilder.parse(sql).create();
        com.avaje.ebean.Query<StatsNflAthleteByEvent> query =
                Ebean.find(StatsNflAthleteByEvent.class)
                        .setUseQueryCache(true)
                        .setUseCache(true);
        query.setRawSql(rawSql);
        List<StatsNflAthleteByEvent> allForWeek =
                (List<StatsNflAthleteByEvent>) Cache.get("allForWeek" + stat.getWeek() + "_" + stat.getSeason() + "_" + position + "_" + stat.getEventTypeId());
        if (allForWeek == null) {
            allForWeek = query.findList();
            Cache.set("allForWeek" + stat.getWeek() + "_" + stat.getSeason() + "_" + position + "_" + stat.getEventTypeId(), allForWeek, 7200);
        }

        List<Float> ranks = new ArrayList<>();
        Float myRank;
        try {
            JSONArray myJsArray = new JSONArray(stat.getFantasyPointsAvgRange());
            if (myJsArray.length() < weeks) {
                return false;
            }
            myRank = (float) myJsArray.getDouble(weeks - 1);
        } catch (JSONException e) {
            return false;
        }

        for (StatsNflAthleteByEvent statForWeek : allForWeek) {
            try {
                JSONArray jsArray = new JSONArray(statForWeek.getFantasyPointsAvgRange());
                float rank = (float) jsArray.getDouble(weeks - 1);
                ranks.add(rank);
            } catch (JSONException e) {
                ranks.add(0f);
            }
        }
        Collections.sort(ranks);
        Collections.reverse(ranks);
        int index = ranks.indexOf(myRank);
        if (index == -1 || index > ranking) {
            return false;
        }

        return true;
    }

    /**
     * What we want here is to know if the opposing defense was ranked N in fewest points allowed at a given position
     * over the last N number of games on average.
     */
    @Override
    public boolean wasDefRankedNOverLastWeeks(StatsNflAthleteByEvent stat,
                                              StatsEventInfo nextEvent,
                                              int ranking, int weeks,
                                              String position) {
        Integer[] eventTypeIds = {GlobalConstants.EVENT_TYPE_NFL_POST_SEASON, GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON};

        JSONArray averagePointsAllowed = null;
        float myPointsAllowed = -99f;
        try {
            if (position == null) {
                position = stat.getPosition().equals("FB") ? "RB" : stat.getPosition();
                averagePointsAllowed = new JSONArray(stat.getOpponentPointsAllowedAtPositionAvgRange());
            } else if (position.equals("QB")) {
                averagePointsAllowed = new JSONArray(stat.getPredFppAllowedQbAvgRange());
            } else if (position.equals("WR")) {
                averagePointsAllowed = new JSONArray(stat.getPredFppAllowedWrAvgRange());
            }

            myPointsAllowed = averagePointsAllowed == null ? -99f : (float) averagePointsAllowed.optDouble(weeks, -99f);
        } catch (Exception e) {
            return false;
        }
        if (myPointsAllowed == -99f) {
            return false;
        }

        // List of total points in an event against this defense at any given position
        LinkedList<Float> positionTotals = new LinkedList<>();

        // The list of N events for this opponent
        Map<SportEvent, Team> lastNOpponents = DaoFactory.getStatsDao()
                .findLastSportEventsByOpponent(nextEvent.getOpponentId(), weeks, eventTypeIds);

        // Go through all of the opponents events and figure out how many points they gave up at any given position
        for (Map.Entry<SportEvent, Team> entry : lastNOpponents.entrySet()) {
            List<StatsNflAthleteByEvent> allAtPosition = DaoFactory.getStatsDao()
                    .findStatsNflAthleteByEvents(entry.getKey(), entry.getValue(), position);

            Float atPositionTotal = 0f;
            for (StatsNflAthleteByEvent atPosition : allAtPosition) {
                atPositionTotal += atPosition.getFppInThisEvent().floatValue();
            }
            positionTotals.add(atPositionTotal);
        }
        Collections.sort(positionTotals);
        // don't reverse because fewer points allowed is better
        int index = positionTotals.indexOf(myPointsAllowed);
        if (index == -1 || index > ranking) {
            return false;
        }
        return true;
    }

    @Override
    public Float getAverageFantasyPointsAllowedAtPositionByDef(StatsNflAthleteByEvent stat, String position, int weeks, StatsEventInfo nextEvent) {
        position = position != null && position.equals("FB") ? "RB" : position;
        Integer[] iSeasons = {GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON, GlobalConstants.EVENT_TYPE_NFL_POST_SEASON};
        Map<SportEvent, Team> lastNOpponents = DaoFactory.getStatsDao()
                .findLastSportEventsByOpponent(nextEvent.getOpponentId(), weeks, iSeasons);
        if (lastNOpponents.size() < weeks) {
            return -99f;
        }
        float total = 0f;
        for (Map.Entry<SportEvent, Team> entry : lastNOpponents.entrySet()) {
            List<StatsNflAthleteByEvent> allOppAP = DaoFactory.getStatsDao()
                    .findStatsNflAthleteByEvents(entry.getKey(), entry.getValue(), position);
            for (StatsNflAthleteByEvent s : allOppAP) {
                total += s.getFppInThisEvent().floatValue();
            }

        }
        return total / (float) weeks;
    }

    @Override
    public void updateAllFutureAthleteProjections(int statsAthleteId, float projection, Date startTime) {
        SqlUpdate update = Ebean.getServer(GlobalConstants.DEFAULT_DATABASE)
                .createSqlUpdate("update stats_nfl_projection" +
                        " set projected_fpp_mod = :projection" +
                        " where stats_athlete_id = :statsAthleteId" +
                        " and start_time > :startTime");
        update.setParameter("projection", projection);
        update.setParameter("statsAthleteId", statsAthleteId);
        update.setParameter("startTime", startTime);
        Ebean.getServer(GlobalConstants.DEFAULT_DATABASE).execute(update);
    }

    @Override
    public void updateNflInjuries() {
        S3Object s3Object = s3Client.getObject(new GetObjectRequest("injuries", "nfl_injuries.csv"));

        BufferedReader in = new BufferedReader(new InputStreamReader(s3Object.getObjectContent()));
        String line = null;
        try {
            while ((line = in.readLine()) != null) {
                try {
                    String[] inj = line.split(",");
                    String name = inj[0];
                    String firstName = name.split("\\s")[0];
                    String lastName = name.split("\\s")[1];
                    String pos = inj[1];
                    String status = inj[2];
                    String reason = inj.length > 3 ? inj[3] : "Unknown";
                    Athlete athlete = Ebean.find(Athlete.class)
                            .where()
                            .like("first_name", firstName)
                            .like("last_name", lastName)
                            .findUnique();
                    if (athlete != null && athlete.getPositions().get(0).getAbbreviation().equals(pos)) {
                        athlete.setInjuryStatus(status.toUpperCase() + "|" + reason);
                        DaoFactory.getSportsDao().saveAthlete(athlete);
                    }


                } catch (Exception e) {
                    Logger.error(e.getMessage(), e);
                }
            }
        } catch (IOException e) {
            Logger.error(e.getMessage(), e);
        }
    }

    @Override
    public List<StatsNflProjection> getProjectionsForLineup(Lineup lineup) {
        List<LineupSpot> lineupSpots = lineup.getLineupSpots();
        String whereClause = "";
        for(LineupSpot lineupSpot: lineupSpots) {
            if(!whereClause.equals("")) {
                whereClause += " or ";
            }
            whereClause += " (p.athlete_id = " + lineupSpot.getAthlete().getId() + " and p.sport_event_id = " + lineupSpot.getAthleteSportEventInfo().getSportEvent().getId() + ") ";
        }

        String sql = String.format("select p.id, p.athlete_id, p.sport_event_id, p.projected_fpp, p.projected_fpp_mod " +
                "from stats_nfl_projection p " +
                "where " + whereClause);

        RawSql rawSql = RawSqlBuilder.parse(sql)
                .columnMapping("p.id", "id")
                .columnMapping("p.athlete_id", "athlete.id")
                .columnMapping("p.sport_event_id", "sportEvent.id")
                .columnMapping("p.projected_fpp", "projectedFpp")
                .columnMapping("p.projected_fpp_mod", "projectedFppMod")
                .create();

        com.avaje.ebean.Query<StatsNflProjection> query = Ebean.find(StatsNflProjection.class);
        query.setRawSql(rawSql);
        return query.findList();
    }

    @Override
    public StatsProjection findStatsProjection(AthleteSportEventInfo athleteSportEventInfo) {
        return Ebean.find(StatsProjection.class).where().eq("athleteSportEventInfo", athleteSportEventInfo).findUnique();
    }

    @Override
    public void saveStatsProjection(StatsProjection statsProjection) {
        Ebean.save(statsProjection);
    }
}
