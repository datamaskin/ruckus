package utils.email;

import models.user.User;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by mwalsh on 8/18/14.
 */
public class VerifyEmailAddress implements EmailMessage {

    private String text;

    public static String template;

    static {
        try {
            InputStream stream = EmailMessage.class.getClassLoader().getResourceAsStream("email/verifyEmail.html");
            template = IOUtils.toString(stream);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("VerifyEmailAddress has not been configured correctly.");
        }
    }

    public VerifyEmailAddress(String url, User user){
        this.text = template
                .replaceAll("#verify_url#", url)
                .replaceAll("#username#", user.getUserName());
    }

    @Override
    public String getText() {
        return text;
    }

}
