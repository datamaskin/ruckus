package utils.email;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import play.Play;

import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Created by mwalsh on 8/18/14.
 */
public class EmailSender implements IEmailSender {

    private static EmailSender instance;
    private static String defaultFrom;
    private static JavaMailSenderImpl mailSenderImpl;

    private EmailSender() {
        String defaultFrom = Play.application().configuration().getString("smtp.user");
        this.defaultFrom = defaultFrom;
        mailSenderImpl = new org.springframework.mail.javamail.JavaMailSenderImpl();
        mailSenderImpl.setHost(Play.application().configuration().getString("smtp.host"));
        mailSenderImpl.setPort(Play.application().configuration().getInt("smtp.port"));
        mailSenderImpl.setUsername(defaultFrom);
        mailSenderImpl.setPassword(Play.application().configuration().getString("smtp.password"));
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol" ,"smtp");
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.starttls.enable", "true");
        props.setProperty("mail.debug", "true");
        mailSenderImpl.setJavaMailProperties(props);
    }

    public static EmailSender getInstance(){
        if(instance == null){
            instance = new EmailSender();
        }
        return instance;
    }

    @Override
    public void sendMail(String to, String subject, EmailMessage emailMessage) {
        try{
            MimeMessage message = mailSenderImpl.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(defaultFrom);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(emailMessage.getText(), true);
            mailSenderImpl.send(message);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
