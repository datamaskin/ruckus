package stats.retriever;

import common.GlobalConstants;
import models.sports.League;
import models.sports.Team;
import stats.parser.TeamParser;
import stats.provider.StatProviderFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mwalsh on 6/6/14.
 */
public class TeamRetriever implements ITeamRetriever {
    @Override
    public List<Team> getAllTeamsInLeague(League league) {
        try {
            Map<String, String> map = new HashMap<>();
            map.put(GlobalConstants.STATS_INC_KEY_RESOURCE, "teams");
            String result = StatProviderFactory.getStatsProvider(league.getAbbreviation()).getStats(map);
            List<Team> teams = new TeamParser().parse(result);
            return teams;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
