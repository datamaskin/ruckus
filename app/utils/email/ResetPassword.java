package utils.email;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by mwalsh on 8/20/14.
 */
public class ResetPassword implements EmailMessage {

    private String text;

    public static String template;

    static {
        try {
            InputStream stream = EmailMessage.class.getClassLoader().getResourceAsStream("email/resetPassword.html");
            template = IOUtils.toString(stream);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("ConfirmEmailAddress has not been configured correctly.");
        }
    }

    public ResetPassword(String url){
        this.text = String.format(template, url);
    }

    @Override
    public String getText() {
        return text;
    }
}
