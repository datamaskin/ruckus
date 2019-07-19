package models;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.DaoFactory;
import models.sports.League;
import models.sports.Sport;
import models.sports.SportEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utilities.BaseTest;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by dmaclean on 7/4/14.
 */
public class SportEventTest extends BaseTest {
    private SportEvent sportEvent;

    private Sport sport;
    private League league;

    @Before
    public void setUp() {
        // Set up Sport
        sport = new Sport(Sport.FOOTBALL.getName());
        Ebean.save(sport);

        // Set up League
        league = new League(sport, League.NFL.getName(), League.NFL.getAbbreviation(), League.NFL.getDisplayName(), true);
        Ebean.save(league);

        sportEvent = new SportEvent(1,
                league,
                new Date(),
                "{\"homeTeam\":\"Arizona Cardinals\",\"awayTeam\":\"Houston Texans\",\"venue\":\"University of Phoenix Stadium\"}",
                "{\"homeId\":\"355\",\"homeTeam\":\"Ari\",\"awayId\":\"325\",\"awayTeam\":\"Hou\",\"homeScore\":0,\"awayScore\":0}",
                60,
                false, 2014, -1, 1);
        DaoFactory.getSportsDao().saveSportEvent(sportEvent);
    }

    @After
    public void tearDown() {
        sport = null;
        league = null;
        sportEvent = null;
    }

    @Test
    public void testUpdateGameScore() {
        int[] gameScore = {1, 2};

        DaoFactory.getSportsDao().updateGameScore(gameScore, sportEvent);

        ObjectMapper mapper = new ObjectMapper();
        TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>() {
        };
        try {
            Map<String, Object> data = mapper.readValue(sportEvent.getShortDescription(), typeReference);
            assertTrue((Integer) data.get("homeScore") == 1);
            assertTrue((Integer) data.get("awayScore") == 2);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
