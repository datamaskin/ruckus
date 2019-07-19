package distributed.tasks;

import com.avaje.ebean.Ebean;
import dao.DaoFactory;
import dao.ISportsDao;
import models.sports.Athlete;
import models.sports.AthleteSportEventInfo;
import models.sports.League;
import models.sports.SportEvent;
import play.Logger;
import stats.retriever.IAthleteInjuryRetriever;
import stats.retriever.IAthleteRetriever;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by mwalsh on 6/5/14.
 */
public class AthleteUpdaterTask extends DistributedTask {

    private IAthleteInjuryRetriever athleteInjuryRetriever;
    private IAthleteRetriever athleteStatRetriever;

    public AthleteUpdaterTask(IAthleteInjuryRetriever athleteInjuryRetriever, IAthleteRetriever athleteStatRetriever) {
        this.athleteInjuryRetriever = athleteInjuryRetriever;
        this.athleteStatRetriever = athleteStatRetriever;
    }

    @Override
    protected String execute() throws Exception {
        List<League> leagues = Ebean.find(League.class).where().eq(League.IS_ACTIVE, true).findList();
        ISportsDao sportsDao = DaoFactory.getSportsDao();

        for (League league : leagues) {
            Map<Integer, String> injuries = athleteInjuryRetriever.getAthleteInjuries(league);

            List<Athlete> athletes = athleteStatRetriever.getAllAthletesForLeague(league);
            for (Athlete statAthlete : athletes) {
                Athlete dbAthlete = sportsDao.findAthlete(statAthlete.getStatProviderId());
                if (dbAthlete != null) {
                    dbAthlete.setFirstName(statAthlete.getFirstName());
                    dbAthlete.setLastName(statAthlete.getLastName());
                    dbAthlete.setTeam(statAthlete.getTeam());
                    dbAthlete.setUniform(statAthlete.getUniform());

                    dbAthlete.setActive(statAthlete.isActive());
                    dbAthlete.setInjuryStatus(injuries.get(statAthlete.getStatProviderId()));
                    dbAthlete.setPositions(statAthlete.getPositions());

                    sportsDao.updateAthlete(dbAthlete);
                } else {
                    dbAthlete = new Athlete(
                            statAthlete.getStatProviderId(),
                            statAthlete.getFirstName(),
                            statAthlete.getLastName(),
                            statAthlete.getTeam(),
                            statAthlete.getUniform());

                    dbAthlete.setActive(statAthlete.isActive());
                    dbAthlete.setInjuryStatus(injuries.get(statAthlete.getStatProviderId()));
                    dbAthlete.setPositions(statAthlete.getPositions());
                    sportsDao.saveAthlete(dbAthlete);

                    /*
                     * Create associated AthleteSportEventInfo
                     */
                    Instant instant = Instant.now().atZone(ZoneId.systemDefault()).toInstant();

                    // Check that the team is not null. This can occur on boot-up with a fresh schema, so in production it could occur once, ever.
                    if(dbAthlete.getTeam() != null) {
                        List<SportEvent> sportEvents = sportsDao.findSportEventsInFuture(league, new Date(Date.from(instant).getTime()), dbAthlete.getTeam());
                        for (SportEvent sportEvent : sportEvents) {
                            AthleteSportEventInfo athleteSportEventInfo =
                                    Ebean.find(AthleteSportEventInfo.class)
                                            .where().eq("sportEvent", sportEvent)
                                            .eq("athlete", dbAthlete).findUnique();
                            if (athleteSportEventInfo == null) {
                                // Set up the initial box score string.
                                String boxscore = "[]";
                                if (sportEvent.getLeague().equals(League.MLB) || sportEvent.getLeague().equals(League.NFL)) {
                                    boxscore = sportsDao.createInitialJsonForAthleteBoxscore(dbAthlete.getPositions().get(0));
                                }

                                athleteSportEventInfo = new AthleteSportEventInfo(sportEvent, dbAthlete, new BigDecimal("0.00"), boxscore, "[]");
                                sportsDao.saveAthleteSportEventInfo(athleteSportEventInfo);
                            }

                            Logger.info("AthleteUpdaterTask - added AthleteSportEventInfo for " + dbAthlete.getFirstName() + " " + dbAthlete.getLastName() + "/" +
                                    sportEvent.getTeams().get(0).getAbbreviation() + "-" + sportEvent.getTeams().get(1).getAbbreviation() + "@" + sportEvent.getStartTime());
                        }
                    }
                    else {
                        Logger.info("AthleteUpdaterTask - unable to create AthleteSportEventInfo for " + dbAthlete.getFirstName() + " " + dbAthlete.getLastName() + ".  Team is null.");
                    }
                }
            }
        }
        return "ok";
    }

}
