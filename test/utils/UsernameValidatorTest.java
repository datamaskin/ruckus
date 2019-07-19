package utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mgiles on 7/8/14.
 */
public class UsernameValidatorTest {

    List<String> validNames = new ArrayList<>();
    List<String> invalidNames = new ArrayList<>();

    @Before
    public void setUp() {
        validNames.add("mickgiles");
        validNames.add("MickGiles");
        validNames.add("Mick-Giles");
        validNames.add("mick_giles");
        validNames.add("mickgiles2222");

        invalidNames.add("m");
        invalidNames.add("mi");
        invalidNames.add("@mickgiles");
        invalidNames.add("^mickgiles^");
        invalidNames.add("mick giles");
        invalidNames.add("Mick*Giles");
    }

    @Test
    public void ValidUsernameTest() {
        for (String temp : validNames) {
            boolean valid = UsernameValidator.isValid(temp);
            System.out.println("Username is valid : " + temp + " , " + valid);
            Assert.assertTrue(valid);
        }
    }

    @Test
    public void InValidUsernameTest() {
        for (String temp : invalidNames) {
            boolean valid = UsernameValidator.isValid(temp);
            System.out.println("username is valid : " + temp + " , " + valid);
            Assert.assertFalse(valid);
        }
    }
}
