package common;

import play.Play;

/**
 * Acts as a convenience class for retrieving the SecureSocial cookie name.
 */
public class SecureSocialCookie {
    /**
     * Retrieves the Secure Social cookie name from the Play config properties.
     *
     * @return      A String representing the cookie name.
     */
    public static String getName() {
        return Play.application().configuration().getString("securesocial.cookie.name");
    }
}
