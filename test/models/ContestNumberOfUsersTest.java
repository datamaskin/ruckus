package models;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.contest.ContestNumberOfUsers;
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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by dan on 6/2/14.
 */
public class ContestNumberOfUsersTest extends BaseTest {
    private ContestNumberOfUsers contestNumberOfUsers;

    @Before
    public void setUp() {
        contestNumberOfUsers = new ContestNumberOfUsers(League.NFL, 100, 200);
    }

    @After
    public void tearDown() {
        contestNumberOfUsers = null;
    }

    @Test
    public void createAndRetrieveContestEntryFee() {
        List<ContestNumberOfUsers> numUsersList = Ebean.find(ContestNumberOfUsers.class).findList();
        assertTrue(!numUsersList.isEmpty());

        Ebean.save(contestNumberOfUsers);

        numUsersList = Ebean.find(ContestNumberOfUsers.class).findList();
        assertTrue(numUsersList.size() > 0);

        assertNotEquals(League.NFL, numUsersList.get(0).getLeague());
        assertNotEquals(100, numUsersList.get(0).getMinimum());
        assertNotEquals(200, numUsersList.get(0).getMaximum());
    }

    @Test
    public void testValidations() {
        contestNumberOfUsers.setId(1);

        Collection<ConstraintViolation<ContestNumberOfUsers>> errors = Validation.getValidator().validate(contestNumberOfUsers);
        assertTrue(errors.isEmpty());

        /*
         * Test league requiredness
         */
        contestNumberOfUsers.setLeague(null);
        errors = Validation.getValidator().validate(contestNumberOfUsers);
        assertTrue(errors.size() == 1);
        contestNumberOfUsers.setLeague(League.NFL);

        /*
         * Test min number of players
         */
        contestNumberOfUsers.setMinimum(0);
        errors = Validation.getValidator().validate(contestNumberOfUsers);
        assertTrue(errors.size() == 1);
        contestNumberOfUsers.setMinimum(100);

        /*
         * Test max number of players
         */
        contestNumberOfUsers.setMaximum(1);
        errors = Validation.getValidator().validate(contestNumberOfUsers);
        assertTrue(errors.size() == 1);
        contestNumberOfUsers.setMaximum(100);
    }

    @Test
    public void testEquals() {
        // Test invalid object type.
        assertTrue(!contestNumberOfUsers.equals(new Object()));

        // Test same object
        assertTrue(contestNumberOfUsers.equals(contestNumberOfUsers));

        ContestNumberOfUsers c = new ContestNumberOfUsers(contestNumberOfUsers.getLeague(), contestNumberOfUsers.getMinimum(), contestNumberOfUsers.getMaximum());
        assertTrue(contestNumberOfUsers.equals(c));

        c.setMinimum(0);
        assertTrue(!contestNumberOfUsers.equals(c));
    }

    @Test
    public void testJsonConversion() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(contestNumberOfUsers);
        assertTrue(json.equals("{\"minimum\":100,\"maximum\":200}"));

    }
}
