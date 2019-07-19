package auth;

import models.user.User;
import securesocial.core.RuntimeEnvironment;

/**
 * Created by mwalsh on 8/13/14.
 */
public class AppEnvironment {

    private static RuntimeEnvironment<User> env;

    public static void setEnvironment(RuntimeEnvironment<User> environment) {
        env = environment;
    }

    public static RuntimeEnvironment<User> getEnvironment() {
        return env;
    }
}
