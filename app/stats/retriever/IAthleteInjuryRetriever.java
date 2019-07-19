package stats.retriever;

import models.sports.League;

import java.util.Map;

/**
 * Created by mwalsh on 6/30/14.
 */
public interface IAthleteInjuryRetriever {
    Map<Integer, String> getAthleteInjuries(League league);
}
