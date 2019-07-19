package models;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.contest.ContestGrouping;
import models.sports.League;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.data.validation.Validation;
import utilities.BaseTest;

import javax.validation.ConstraintViolation;
import java.util.Collection;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by dan on 6/2/14.
 */
public class ContestGroupingTest extends BaseTest {
    //    private Sport sport;
//    private League league;
    private ContestGrouping contestGrouping;

    @Before
    public void setUp() {
//        sport = new Sport(1, "football");
//        Ebean.save(sport);
//
//        league = new League(100, sport, "National Football League", GlobalConstants.SPORT_NFL, "National Football League", true);
//        Ebean.save(league);

        contestGrouping = new ContestGrouping("ALL", League.NFL);
    }

    @After
    public void tearDown() {
        contestGrouping = null;
//        league = null;
//        sport = null;
    }

    @Test
    public void createAndRetrieveContestGrouping() {
        List<ContestGrouping> groupingList = Ebean.find(ContestGrouping.class).findList();
        assertTrue(!groupingList.isEmpty());

        Ebean.save(contestGrouping);

        groupingList = Ebean.find(ContestGrouping.class).findList();
        assertTrue(groupingList.size() > 0);

        assertNotEquals(null, groupingList.get(0).getLeague());
        assertNotEquals(null, groupingList.get(0).getName());
    }

    @Test
    public void testValidations() {
        Collection<ConstraintViolation<ContestGrouping>> errors = Validation.getValidator().validate(contestGrouping);
        assertTrue(errors.isEmpty());

        /*
         * Test league requiredness
         */
        contestGrouping.setLeague(null);
        errors = Validation.getValidator().validate(contestGrouping);
        assertTrue(errors.size() == 1);
        contestGrouping.setLeague(League.NFL);

        /*
         * Test entry fee
         */
        contestGrouping.setName(null);
        errors = Validation.getValidator().validate(contestGrouping);
        assertTrue(errors.size() == 1);
        contestGrouping.setName("ALL");
    }

    @Test
    public void testEquals() {
        // Test invalid object type.
        assertTrue(!contestGrouping.equals(new Object()));

        // Test same object
        assertTrue(contestGrouping.equals(contestGrouping));

        ContestGrouping c = new ContestGrouping(contestGrouping.getName(), contestGrouping.getLeague());
        assertTrue(contestGrouping.equals(c));

        c.setName("EARLY");
        assertTrue(!contestGrouping.equals(c));
    }

    @Test
    public void testJsonConversion() {
        ObjectMapper mapper = new ObjectMapper();

        try {
            String json = mapper.writeValueAsString(contestGrouping);
            assertTrue(json.equals("{\"id\":0,\"name\":\"ALL\"}"));
        } catch (JsonProcessingException e) {
            fail(e.getMessage());
        }
    }
}
