package stats.retriever;

import models.sports.League;
import models.sports.Team;

import java.util.List;

/**
 * Created by mwalsh on 6/6/14.
 */
public interface ITeamRetriever {
    List<Team> getAllTeamsInLeague(League league);
}
