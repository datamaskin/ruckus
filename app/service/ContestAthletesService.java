package service;

import com.avaje.ebeaninternal.server.lib.util.NotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.GlobalConstants;
import dao.DaoFactory;
import distributed.DistributedServices;
import models.contest.Contest;
import models.sports.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import play.Logger;
import stats.provider.mlb.StatsIncProviderMLB;
import stats.retriever.mlb.ProbablePitcherRetriever;
import stats.translator.IFantasyPointTranslator;
import utils.ITimeService;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

/**
 * Created by mgiles on 6/19/14.
 */
@SuppressWarnings("unchecked")
public class ContestAthletesService extends AbstractCachingService {

    private ITimeService timeService;
    private IFantasyPointTranslator translator;

    public ContestAthletesService(ITimeService timeService) {
        this.timeService = timeService;
    }

    @Cacheable(value = "contestAthletes")
    public String getContestAthletesAsJson(String contestId) throws JSONException, JsonProcessingException {
        List<Map<String, Object>> athletes = new ArrayList<>();

        Contest contest = DaoFactory.getContestDao().findContest(contestId);

        Map<String, BigDecimal> fppgCache = DistributedServices.getInstance().getMap(GlobalConstants.ATHLETE_FPPG_MAP);
        Map<String, Integer> dollarsPerPointMap = DistributedServices.getInstance().getMap(GlobalConstants.ATHLETE_DOLLARS_PER_POINT_MAP);
        Map<String, int[]> rankMap = DistributedServices.getInstance().getMap(GlobalConstants.NFL_ATHLETE_RANK_MAP);

        /*
         * For MLB, grab probable pitchers map.
         */
        List<Integer> probablePitcherIds = getProbablePitchersList(contest.getLeague());

        Long start = System.currentTimeMillis();
        for (SportEvent se : contest.getSportEventGrouping().getSportEvents()) {
            for (Team team : se.getTeams()) {
                for (Athlete a : DaoFactory.getSportsDao().findAthletes(team)) {

                    if (!a.isActive()) {
                        continue;
                    }

                    if(a.getPositions().contains(Position.FB_KICKER)){
                        continue;
                    }

                    Map<String, Object> map = new HashMap<>();

                    //TODO: HACK to slim down MLB athlete list
                    if (contest.getLeague().equals(League.MLB)) {
                        /*
                         * Check for probable pitchers.  If one is found then add a flag to their data indicating so.
                         */
                        if (a.getPositions().get(0).equals(Position.BS_PITCHER) && probablePitcherIds != null && probablePitcherIds.contains(a.getStatProviderId())) {
                            map.put("probablePitcher", "true");
                        }
                    }
                    //TODO: HACK END

                    map.put("id", a.getId());
                    map.put("eventId", se.getStatProviderId());
                    map.put("firstName", a.getFirstName());
                    map.put("lastName", a.getLastName());
                    List<Integer> positions = new ArrayList<>();
                    for (Position p : a.getPositions()) {
                        positions.add(p.getId());
                    }
                    map.put("ppos", a.getPositions().get(0).getAbbreviation().toUpperCase());
                    map.put("positions", positions);
                    map.put("number", a.getUniform());
                    map.put("image", "https://dm63aeeijtc75.cloudfront.net/" + se.getLeague().getAbbreviation().toLowerCase() + "/" + a.getStatProviderId());
                    map.put("teamId", a.getTeam().getStatProviderId());
                    map.put("team", a.getTeam().getAbbreviation());
                    JSONObject desc = new JSONObject(se.getShortDescription());
                    JSONObject longDesc = new JSONObject(se.getDescription());
                    map.put("venue", longDesc.getString("venue"));
                    map.put("location", longDesc.getString("location"));
                    map.put("homeTeam", desc.getString("homeTeam"));
                    map.put("awayTeam", desc.getString("awayTeam"));

                    List<List> stats = DaoFactory.getStatsDao().getRucksackStats(a);
                    map.put("stats", stats);

                    AthleteSalary athleteSalary = DaoFactory.getSportsDao().findAthleteSalary(a, contest.getSportEventGrouping());

                    //FIXME: need real salary
                    if (athleteSalary == null) {
                        Logger.error("No salary information for " + a.getFirstName() + " " + a.getLastName() + "(" + a.getStatProviderId() + ")");
                    }
                    int fake = (4700 + ((int) (Math.random() * 9)) * 100) * 100;
                    fake += 100;
                    int salary = athleteSalary == null ? fake : athleteSalary.salary;
                    map.put("salary", salary);
                    //FIXME:

                    if (salary < 200000) {
                        continue;
                    }

                    AthleteSportEventInfo athleteSportEventInfo = null;
                    try {
                        athleteSportEventInfo = DaoFactory.getSportsDao().findAthleteSportEventInfo(a, se);
                    } catch (NotFoundException e) {
                        Logger.warn("Could not find " + a.getFirstName() + " " + a.getLastName() + " for " + se.getShortDescription());
                        continue;
                    }

                    /*
                     * Fantasy points-per-game
                     */
                    BigDecimal fppg = fppgCache.get(athleteSportEventInfo.getId() + "_" + 5);
                    if(fppg == null){
                        fppg = DaoFactory.getSportsDao().calculateFantasyPointsPerGame(translator, timeService, athleteSportEventInfo, 5).setScale(1, RoundingMode.HALF_EVEN);
                    }
                    map.put("ffpg", fppg);
                    map.put("athleteSportEventInfoId", athleteSportEventInfo != null ? athleteSportEventInfo.getId() : -1);

                    /*
                     * Matchup
                     */
                    map.put("matchup", determineMatchupDisplay(se, a));

                    if (contest.getLeague().equals(League.MLB)) {
                        String[] injuryData = a.getInjuryStatus().split("\\|");
                        map.put("injuryStatus", injuryData[0]);
                    } else {
                        map.put("injuryStatus", a.getInjuryStatus());
                    }

                    /*
                     * Opponent
                     */
                    map.put("opp", determineOpponent(se, a));

                    /*
                     * Dollars per point
                     */
                    String key = a.getId() + "_" + se.getStartTime().getTime() + "_" + 5;
                    Integer result = dollarsPerPointMap.get(key);
                    if(result == null) {
                        result = DaoFactory.getSportsDao().calculateAverageDollarsPerFantasyPoint(a, se.getStartTime(), 5);
                    }
                    map.put("dollarsPerFantasyPoint", result);

                    /*
                     * Rank
                     */
                    key = athleteSportEventInfo.getId() + "_" + se.getStartTime().getTime() + "_" + 5;
                    int[] rankResult = rankMap.get(key);
                    if(rankResult == null) {
                        rankResult = DaoFactory.getSportsDao().calculateRank(a.getPositions().get(0), translator, athleteSportEventInfo,
                                athleteSportEventInfo.getSportEvent().getSeason(), athleteSportEventInfo.getSportEvent().getLeague(), 17);
                    }
                    map.put("rank", rankResult);

                    athletes.add(map);
                }
            }
        }
        Long end = System.currentTimeMillis();
        Logger.info("Contest Athlete list generation took " + (end-start)/1000.0 + " seconds.");

        Collections.sort(athletes, new Sorter());
        return new ObjectMapper().writeValueAsString(athletes);
    }

    @CacheEvict(value = "contestAthletes")
    public void removeContestAthletes(String contestId) {
    }

    /**
     * Determines whether the athlete's matchup (i.e. NE vs BAL) should be displayed as NE@BAL or NEvBAL.
     *
     * @return      The matchup, properly formatted for home or away.
     */
    private String determineMatchupDisplay(SportEvent sportEvent, Athlete athlete) {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>() {};

        try {
            Map<String, Object> shortDescription = mapper.readValue(sportEvent.getShortDescription(), typeReference);
            Integer homeTeamId = Integer.parseInt((String) shortDescription.get("homeId"));
            Integer athleteTeamId = athlete.getTeam().getStatProviderId();

            if(athleteTeamId.equals(homeTeamId)) {
                return String.format("%sv%s", shortDescription.get("homeTeam"), shortDescription.get("awayTeam"));
            }
            else {
                return String.format("%s@%s", shortDescription.get("awayTeam"), shortDescription.get("homeTeam"));
            }
        } catch (IOException e) {
            Logger.error("Unable to generate matchup display correctly", e);
            List<Team> teams = sportEvent.getTeams();
            return String.format("%sv%s", teams.get(0).getAbbreviation(), teams.get(1).getAbbreviation());
        }
    }

    /**
     * Determine the opponent for this athlete in the associated sport event.
     *
     * @return
     */
    private String determineOpponent(SportEvent sportEvent, Athlete athlete) {
        List<Team> teams = sportEvent.getTeams();
        if(athlete.getTeam().getId() == teams.get(0).getId()) {
            return teams.get(1).getAbbreviation();
        }
        else {
            return teams.get(0).getAbbreviation();
        }
    }

    /**
     * Retrieve the list of probable pitchers for an MLB contest.  We look in the cache first and if
     * nothing is found there then a call to Stats is made and the list is cached.
     *
     * @param league The League for the contest.
     * @return A list of stat provider ids representing probable starting pitchers.
     */
    private List<Integer> getProbablePitchersList(League league) {
        if (!league.equals(League.MLB)) {
            return null;
        }

        Instant now = timeService.getNowEST();
        Date today = Date.from(now);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy-MM-dd");

        Map<String, List<Integer>> cache = DistributedServices.getInstance().getMap(GlobalConstants.PROBABLE_PITCHERS_MAP);
        List<Integer> probablePitcherIds = cache.get(simpleDateFormat.format(today));
        if (probablePitcherIds == null) {
            Logger.info("Could not find a Probable Pitchers list in cache");
            ProbablePitcherRetriever probablePitcherRetriever = new ProbablePitcherRetriever(new StatsIncProviderMLB());
            probablePitcherIds = probablePitcherRetriever.getProbablePitchersForDate(now);
            cache.put(simpleDateFormat.format(today), probablePitcherIds);
        }

        return probablePitcherIds;
    }

    @Override
    @CacheEvict(value = "contestAthletes", allEntries = true)
    public void flushAllCaches() {
    }

    private class Sorter implements Comparator<Map<String, Object>> {
        @Override
        public int compare(Map<String, Object> o1, Map<String, Object> o2) {
            Integer salary1 = (Integer) o1.get("salary");
            Integer salary2 = (Integer) o2.get("salary");
            String name1 = (String) o1.get("lastName");
            String name2 = (String) o2.get("lastName");

            if (salary1.equals(salary2)) {
                return name1.compareTo(name2);
            } else {
                return salary2.compareTo(salary1);
            }
        }
    }

    public void setTranslator(IFantasyPointTranslator translator) {
        this.translator = translator;
    }
}
