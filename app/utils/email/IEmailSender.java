package utils.email;

/**
 * Created by mwalsh on 8/18/14.
 */
public interface IEmailSender {
    void sendMail(String to, String subject, EmailMessage emailMessage);
}
