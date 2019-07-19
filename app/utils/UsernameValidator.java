package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mgiles on 7/8/14.
 */
public class UsernameValidator {
    private static final Pattern pattern = Pattern.compile("^[a-zA-Z0-9_-]{3,15}$");

    /**
     * Validate username with regular expression
     *
     * @param username username for validation
     * @return true valid username, false invalid username
     */
    public static boolean isValid(final String username) {
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }
}
