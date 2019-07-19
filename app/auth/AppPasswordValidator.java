package auth;

import java.util.regex.Pattern;

/**
 * Created by mwalsh on 8/20/14.
 */
public class AppPasswordValidator {

    private static final Pattern passwordPattern =
            Pattern.compile("^" + //start
                    "(?=.*[0-9])" + //look ahead for at least one digit
                    "(?=.*[a-z])" + //look ahead for at least one lower-case
                    "(?=.*[A-Z])" + //look ahead for at least one upper-case
//                    "(?=.*[\\-.!@#$%^&*\\(\\)])" + //look ahead for at least one-symbol
                    "[0-9a-zA-Z\\ \\!\\@\\#\\$\\%\\^\\&\\*\\(\\)\\_\\+\\-\\=\\{\\}\\|\\[\\]\\\\\\:\\\"\\;\\'\\<\\>\\?\\,\\.\\/]{8,}$"); //consume at least 8 of the defined characters

    public boolean isValid(String password) {
        return passwordPattern.matcher(password).matches();
    }

}
