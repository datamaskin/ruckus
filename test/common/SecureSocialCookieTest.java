package common;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utilities.BaseTest;

import static org.junit.Assert.assertEquals;

/**
 * Created by dmaclean on 8/26/14.
 */
public class SecureSocialCookieTest extends BaseTest {
    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {

    }

    @Test
    public void testGetName() {
        assertEquals("ss_id_local", SecureSocialCookie.getName());
    }
}
