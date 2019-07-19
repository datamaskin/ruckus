package controllers;

import auth.AppPasswordValidator;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by mwalsh on 8/20/14.
 */
public class AuthenticationValidators {

    AppPasswordValidator validator = new AppPasswordValidator();

    @Test
    public void testEmailPatterns(){
        assertTrue(validator.isValid("Plc-201-ftpP"));
        assertTrue(validator.isValid("Plc-201@ftp!"));
        assertTrue(validator.isValid("Plc@201@ftp"));
        assertTrue(validator.isValid("(Plc@201@ftp)"));
        assertTrue(validator.isValid("(Plc@201@ftp)*"));

        assertFalse(validator.isValid("(Plc@201@ftp§)"));
        assertFalse(validator.isValid("Plc01ftpxx"));
        assertFalse(validator.isValid("plc-01-ftpxx"));
        assertFalse(validator.isValid("Plc-ftp-xxxx"));
        assertFalse(validator.isValid("PLC201-XXX"));
    }

}
