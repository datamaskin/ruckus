package utils.email;

import models.user.User;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by mwalsh on 8/20/14.
 */
public class WelcomeEmail implements EmailMessage {

    private String text;

    public static String template;

    static {
        try {
            InputStream stream = EmailMessage.class.getClassLoader().getResourceAsStream("email/welcome.html");
            template = IOUtils.toString(stream);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("WelcomeEmail has not been configured correctly.");
        }
    }

    public WelcomeEmail(User user){this.text = template.replace("#username#", user.getUserName());}

    @Override
    public String getText() {
        return text;
    }
}
