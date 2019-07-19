package models;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.GlobalConstants;
import models.contest.ContestSalary;
import models.sports.League;
import models.sports.Sport;
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
public class ContestSalaryTest extends BaseTest {
    private Sport sport;
    private League league;
    private ContestSalary contestSalary;

    @Before
    public void setUp() {
        sport = new Sport(1, "football");
//        Ebean.save(sport);

        league = new League(100, sport, "National Football League", GlobalConstants.SPORT_NFL, "National Football League", true);
        Ebean.save(league);

        contestSalary = new ContestSalary(league, 50000);
    }

    @After
    public void tearDown() {
        contestSalary = null;
        league = null;
        sport = null;
    }

    @Test
    public void createAndRetrieveContestSalary() {
        league = new League(100, Sport.FOOTBALL, "National Football League", GlobalConstants.SPORT_NFL, "National Football League", true);
        contestSalary = new ContestSalary(league, 50000);

        List<ContestSalary> salaryList = Ebean.find(ContestSalary.class).findList();
        assertTrue(!salaryList.isEmpty());

        Ebean.save(contestSalary);

        salaryList = Ebean.find(ContestSalary.class).findList();
        assertTrue(salaryList.size() >= 0);

        assertNotEquals(league, salaryList.get(0).getLeague());
        assertEquals(5000000, salaryList.get(0).getSalary());
    }

    @Test
    public void testValidations() {
        league = new League(100, Sport.FOOTBALL, "National Football League", GlobalConstants.SPORT_NFL, "National Football League", true);
        contestSalary = new ContestSalary(league, 50000);

        Collection<ConstraintViolation<ContestSalary>> errors = Validation.getValidator().validate(contestSalary);
        assertTrue(errors.isEmpty());

        /*
         * Test league requiredness
         */
        contestSalary.setLeague(null);
        errors = Validation.getValidator().validate(contestSalary);
        assertTrue(errors.size() == 1);
        contestSalary.setLeague(league);

        /*
         * Test salary
         */
        contestSalary.setSalary(0);
        errors = Validation.getValidator().validate(contestSalary);
        assertTrue(errors.size() == 1);
        contestSalary.setSalary(1);
    }

    @Test
    public void testEquals() {
        league = new League(100, Sport.FOOTBALL, "National Football League", GlobalConstants.SPORT_NFL, "National Football League", true);
        contestSalary = new ContestSalary(league, 50000);

        // Test invalid object type.
        assertTrue(!contestSalary.equals(new Object()));

        // Test same object
        assertTrue(contestSalary.equals(contestSalary));

        ContestSalary c = new ContestSalary(contestSalary.getLeague(), contestSalary.getSalary());
        assertTrue(contestSalary.equals(c));

        c.setSalary(10);
        assertTrue(!contestSalary.equals(c));
    }

    @Test
    public void testJsonConversion() throws JsonProcessingException {
        league = new League(100, Sport.FOOTBALL, "National Football League", GlobalConstants.SPORT_NFL, "National Football League", true);
        contestSalary = new ContestSalary(league, 50000);

        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(contestSalary);
        assertTrue(json.equals("{\"id\":0,\"salary\":50000}"));

    }
}
