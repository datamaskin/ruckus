package distributed.tasks;

import dao.DaoFactory;
import models.sports.Athlete;
import models.sports.League;
import models.sports.Position;
import models.sports.Team;
import stats.retriever.ITeamRetriever;

import java.util.Arrays;
import java.util.List;

/**
 * Created by mwalsh on 6/6/14.
 */
public class TeamUpdaterTask extends DistributedTask {

    private ITeamRetriever teamRetriever;

    public TeamUpdaterTask(ITeamRetriever teamRetriever) {
        this.teamRetriever = teamRetriever;
    }

    @Override
    protected String execute() throws Exception {
        for (League league : League.ALL_LEAGUES) {
            List<Team> teams = teamRetriever.getAllTeamsInLeague(league);
            for (Team team : teams) {
                Team dbTeam = DaoFactory.getSportsDao().findTeam(team.getStatProviderId());
                if (dbTeam == null) {
                    dbTeam = new Team(league, team.getLocation(), team.getName(), team.getAbbreviation(), team.getStatProviderId());
                    DaoFactory.getSportsDao().saveTeam(dbTeam);
                } else {
                    dbTeam.setAbbreviation(team.getAbbreviation());
                    dbTeam.setLocation(team.getLocation());
                    dbTeam.setName(team.getName());
                    DaoFactory.getSportsDao().updateTeam(dbTeam);
                }

                if(league.equals(League.NFL)){
                    Athlete defense = DaoFactory.getSportsDao().findDefense(team);
                    if(defense == null){
                        defense = new Athlete(
                                dbTeam.getStatProviderId(),
                                "",
                                dbTeam.getName(),
                                dbTeam, "DEF");
                        defense.setPositions(Arrays.asList(Position.FB_DEFENSE));
                        DaoFactory.getSportsDao().saveAthlete(defense);
                    }
                }
            }
        }
        return null;
    }
}
