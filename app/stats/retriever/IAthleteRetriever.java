package stats.retriever;

import models.sports.Athlete;
import models.sports.League;

import java.util.List;

/**
 * Created by mwalsh on 6/5/14.
 */
public interface IAthleteRetriever {

    List<Athlete> getAllAthletesForLeague(League league);

    Athlete getAthlete(League league, Integer id);

}