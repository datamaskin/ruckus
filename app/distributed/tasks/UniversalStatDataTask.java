package distributed.tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.HazelcastInstance;
import common.GlobalConstants;
import dao.DaoFactory;
import distributed.DistributedServices;
import models.sports.Athlete;
import models.sports.League;
import models.sports.SportEvent;
import models.sports.Team;
import stats.retriever.IAthleteRetriever;
import stats.retriever.ISportEventRetriever;
import stats.retriever.ITeamRetriever;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dmaclean on 6/18/14.
 */
public class UniversalStatDataTask extends DistributedTask {

    private ISportEventRetriever sportEventStatRetriever;
    private ITeamRetriever teamRetriever;
    private IAthleteRetriever athleteStatRetriever;

    public UniversalStatDataTask(ISportEventRetriever sportEventStatRetriever, ITeamRetriever teamRetriever, IAthleteRetriever athleteStatRetriever) {
        this.sportEventStatRetriever = sportEventStatRetriever;
        this.teamRetriever = teamRetriever;
        this.athleteStatRetriever = athleteStatRetriever;
    }

    @Override
    protected String execute() throws Exception {
        HazelcastInstance hazelcastInstance = DistributedServices.getInstance();
        Map<String, String> cache = hazelcastInstance.getMap(GlobalConstants.ATHLETE_SPORT_EVENT_INFO_MAP);

        ObjectMapper mapper = new ObjectMapper();

        for (League league : DaoFactory.getSportsDao().findActiveLeagues()) {
            /*
             * Get all teams for this league.
			 */
            List<Team> teams = teamRetriever.getAllTeamsInLeague(league);
            for (Team team : teams) {
                Team dbTeam = DaoFactory.getSportsDao().findTeam(team.getStatProviderId());
                if (dbTeam == null) {
                    Team newTeam = new Team(league, team.getLocation(), team.getName(), team.getAbbreviation(), team.getStatProviderId());
                    DaoFactory.getSportsDao().saveTeam(newTeam);
                } else {
                    dbTeam.setAbbreviation(team.getAbbreviation());
                    dbTeam.setLocation(team.getLocation());
                    dbTeam.setName(team.getName());
                    DaoFactory.getSportsDao().updateTeam(dbTeam);
                }
            }

			/*
             * Get all athlete for this league.
			 */
            List<Athlete> savedAthletes = new ArrayList<>();
            List<Athlete> athletes = athleteStatRetriever.getAllAthletesForLeague(league);
            for (Athlete statAthlete : athletes) {
                Athlete dbAthlete = DaoFactory.getSportsDao().findAthlete(statAthlete.getStatProviderId());
                if (dbAthlete != null) {
                    dbAthlete.setFirstName(statAthlete.getFirstName());
                    dbAthlete.setLastName(statAthlete.getLastName());
                    dbAthlete.setTeam(statAthlete.getTeam());
                    dbAthlete.setUniform(statAthlete.getUniform());

                    dbAthlete.setActive(statAthlete.isActive());
//                    dbAthlete.setInjuryStatus(injuries.get(statAthlete.getStatProviderId()));
                    dbAthlete.setPositions(statAthlete.getPositions());
                    DaoFactory.getSportsDao().updateAthlete(dbAthlete);
                } else {
                    dbAthlete = new Athlete(
                            statAthlete.getStatProviderId(),
                            statAthlete.getFirstName(),
                            statAthlete.getLastName(),
                            statAthlete.getTeam(),
                            statAthlete.getUniform());

                    dbAthlete.setActive(statAthlete.isActive());
//                    dbAthlete.setInjuryStatus(injuries.get(statAthlete.getStatProviderId()));
                    dbAthlete.setPositions(statAthlete.getPositions());
                    DaoFactory.getSportsDao().saveAthlete(dbAthlete);
                }

                savedAthletes.add(dbAthlete);
            }

			/*
             * Get all Sport Events for today.
			 */
            getEventsForDate(league, LocalDate.now());
            getEventsForDate(league, LocalDate.now().plus(1, ChronoUnit.DAYS));

        }
        return null;
    }

    private void getEventsForDate(League league, LocalDate date) {
        Map<Team, SportEvent> savedEvents = new HashMap<>();
        List<SportEvent> events = sportEventStatRetriever.getSportEventsForDate(league, date);
        if (events != null) {
            for (SportEvent event : events) {
                SportEvent dbEvent = DaoFactory.getSportsDao().findSportEvent(event.getStatProviderId());
                if (dbEvent == null) {
                    DaoFactory.getSportsDao().saveSportEvent(event);
                }

                SportEvent savedEvent = (dbEvent == null) ? event : dbEvent;
                for (Team team : savedEvent.getTeams()) {
                    savedEvents.put(team, savedEvent);
                }
            }
        }
    }

}
