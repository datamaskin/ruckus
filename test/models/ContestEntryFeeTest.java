package models;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.contest.ContestEntryFee;
import models.sports.League;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.data.validation.Validation;
import utilities.BaseTest;

import javax.validation.ConstraintViolation;
import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by dan on 6/2/14.
 */
public class ContestEntryFeeTest extends BaseTest {

    //    private Sport sport;
//    private League league;
    private ContestEntryFee contestEntryFee;

    @Before
    public void setUp() {
//        sport = new Sport(1, "football");
//        Ebean.save(sport);
//
//        league = new League(100, sport, "National Football League", GlobalConstants.SPORT_NFL, "National Football League", true);
//        Ebean.save(league);

        contestEntryFee = new ContestEntryFee(League.NFL, 100);
    }

    @After
    public void tearDown() {
        contestEntryFee = null;
//        league = null;
//        sport = null;
    }

    @Test
    public void createAndRetrieveContestEntryFee() {
        List<ContestEntryFee> entryFeeList = Ebean.find(ContestEntryFee.class).findList();
        assertTrue(!entryFeeList.isEmpty());
        contestEntryFee = new ContestEntryFee(League.NFL, 100);
        Ebean.save(contestEntryFee);

        entryFeeList = Ebean.find(ContestEntryFee.class).findList();
        assertTrue(entryFeeList.size() > 0);

        assertNotEquals(League.NFL, entryFeeList.get(0).getLeague());
        assertNotEquals(100, entryFeeList.get(0).getEntryFee());
    }

    @Test
    public void testValidations() {
        Collection<ConstraintViolation<ContestEntryFee>> errors = Validation.getValidator().validate(contestEntryFee);
        assertTrue(errors.isEmpty());

        /*
         * Test league requiredness
         */
        contestEntryFee.setLeague(null);
        errors = Validation.getValidator().validate(contestEntryFee);
        assertTrue(errors.size() == 1);
        contestEntryFee.setLeague(League.NFL);

        /*
         * Test entry fee
         */
        contestEntryFee.setEntryFee(-1);
        errors = Validation.getValidator().validate(contestEntryFee);
        assertTrue(errors.size() == 1);
        contestEntryFee.setEntryFee(100);
    }

    @Test
    public void testEquals() {
        // Test invalid object type.
        assertTrue(!contestEntryFee.equals(new Object()));

        // Test same object
        assertTrue(contestEntryFee.equals(contestEntryFee));

        ContestEntryFee c = new ContestEntryFee(contestEntryFee.getLeague(), contestEntryFee.getEntryFee());
        assertTrue(contestEntryFee.equals(c));

        c.setEntryFee(0);
        assertTrue(!contestEntryFee.equals(c));
    }

    @Test
    public void testJsonConversion() {
        ObjectMapper mapper = new ObjectMapper();

        try {
            String json = mapper.writeValueAsString(contestEntryFee);
            assertTrue(json.equals("{\"id\":0,\"entryFee\":100}"));
        } catch (JsonProcessingException e) {
            fail(e.getMessage());
        }
    }
}
