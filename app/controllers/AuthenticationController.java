package controllers;

import auth.AppEnvironment;
import auth.AppPasswordValidator;
import com.avaje.ebean.Ebean;
import dao.IUserDao;
import distributed.DistributedServices;
import models.user.SecureSocialToken;
import models.user.User;
import models.wallet.UserWallet;
import org.joda.time.DateTime;
import play.Logger;
import play.Play;
import play.libs.F;
import play.mvc.Result;
import scala.Option;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import scala.util.Success;
import securesocial.core.authenticator.Authenticator;
import securesocial.core.authenticator.AuthenticatorBuilder;
import securesocial.core.providers.utils.PasswordHasher;
import securesocial.core.services.AuthenticatorService;
import utils.UsernameValidator;
import utils.WordFilter;
import utils.email.*;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Created by mwalsh on 8/18/14.
 */
public class AuthenticationController extends AbstractController {

    public static final String AUTH_LOGIN = "/auth/login";

    private static IUserDao userDao = DistributedServices.getContext().getBean("userDao", IUserDao.class);
    private static IEmailSender emailSender = EmailSender.getInstance();

    public static class EmailValidator {
        private static final Pattern emailPattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        public static boolean validate(String email){
            return emailPattern.matcher(email).matches();
        }
    }

    private static String usernameCheck(String username){
        if (username == null || username.equals("")) {
            return "Username must be at least 3-15 characters long and cannot contain spaces or special characters.";
        }

        if (!UsernameValidator.isValid(username)) {
            return "Username must be at least 3-15 characters long and cannot contain spaces or special characters.";
        }

        if (!WordFilter.isClean(username)) {
            return "That username is unavailable.";
        }

        if (WordFilter.isReserved(username)) {
            return "That username is reserved. Please contact customer support if you would like to claim it.";
        }

        if (userDao.findUserByUsername(username) != null) {
            return "That username is unavailable.";
        }

        return null;
    }

    public static Result verifyUsername(){
        String username = request().getQueryString("u").trim();
        String error = usernameCheck(username);
        if(error != null){
            return jerr(error);
        }
        return jok("true");
    }

    public static Result verifyPassword(){
        String password = request().getQueryString("p").trim();
        if(new AppPasswordValidator().isValid(password) == false){
            return jerr("Invalid password.");
        }
        return jok("true");
    }

    public static Result createNewSignUp(){
        Map<String, String[]> params = request().body().asFormUrlEncoded();
        String username = params.get("username")[0];
        String email = params.get("email")[0];
        String password = params.get("password")[0];

        User emailexists = userDao.findUserByEmail(email);
        if(emailexists != null){
            if(emailexists.getProviderId().equals("userpass")){
                flash("error", "A user with that email has already registered. If you've forgotten your password, please reset it.");
                return redirect("/newSignUp");
            } else {
                flash("error", "A user with that email has already registered using a social network. Please login using that social network icon via our login page.");
                return redirect("/newSignUp");
            }
        }

        User usernameExists = userDao.findUserByUsername(username);
        if(usernameExists != null){
            flash("error", "That username has already been taken. Please choose another one.");
            return redirect("/newSignUp");
        }

        if(EmailValidator.validate(email) == false){
            flash("error", "Please enter a valid email address.");
            return redirect("/newSignUp");
        }

        if(new AppPasswordValidator().isValid(password) == false){
            flash("error", "Invalid password.");
            return redirect("/newSignUp");
        }

        String error = usernameCheck(username);
        if(error != null){
            flash("error", error);
            return redirect("/newSignUp");
        }

        F.Promise.promise(() -> {
            User user = new User();
            AffiliateUtils.setUserAffiliate4Java(request(), user);
            user.setUserName(username);
            user.setProviderId("userpass");
            user.setEmail(email);
            user.setPassword(new PasswordHasher.Default().hash(password).password());
            userDao.saveUser(user);

            //TODO REMOVE ME I"M ADDING LOTS OF MONEY FOR NEW USERS SO THEY DON"T HAVE TO DEPOSIT
            //TODO REMOVE ME I"M ADDING LOTS OF MONEY FOR NEW USERS SO THEY DON"T HAVE TO DEPOSIT
            //TODO REMOVE ME I"M ADDING LOTS OF MONEY FOR NEW USERS SO THEY DON"T HAVE TO DEPOSIT
            //TODO REMOVE ME I"M ADDING LOTS OF MONEY FOR NEW USERS SO THEY DON"T HAVE TO DEPOSIT
            //TODO REMOVE ME I"M ADDING LOTS OF MONEY FOR NEW USERS SO THEY DON"T HAVE TO DEPOSIT
            UserWallet userWallet = new UserWallet(user);
            userWallet.updateUsd(1000000);
            userDao.updateWallet(userWallet);
            //TODO REMOVE ME I"M ADDING LOTS OF MONEY FOR NEW USERS SO THEY DON"T HAVE TO DEPOSIT
            //TODO REMOVE ME I"M ADDING LOTS OF MONEY FOR NEW USERS SO THEY DON"T HAVE TO DEPOSIT
            //TODO REMOVE ME I"M ADDING LOTS OF MONEY FOR NEW USERS SO THEY DON"T HAVE TO DEPOSIT
            //TODO REMOVE ME I"M ADDING LOTS OF MONEY FOR NEW USERS SO THEY DON"T HAVE TO DEPOSIT
            //TODO REMOVE ME I"M ADDING LOTS OF MONEY FOR NEW USERS SO THEY DON"T HAVE TO DEPOSIT

            SecureSocialToken token = new SecureSocialToken(UUID.randomUUID().toString());
            DateTime now = new DateTime();
            token.setCreationTime(now);
            token.setEmail(user.getEmail());
            token.setExpirationTime(now.plusDays(1));
            token.setSignUp(true);
            Ebean.save(token);

            emailSender.sendMail(user.getEmail(), "Please confirm email",
                    new VerifyEmailAddress(
                            Play.application().configuration().getString("external.url") + "/confirmEmail/" + token.getUuid(),
                            user));
            return null;
        });

        flash("error", "Please check your email to verify your account. If you didn’t receive a verification email, please check your spam folder or click here to resend.");
        return redirect(AUTH_LOGIN);
    }

    public static Result confirmEmail(String tokenId){
        SecureSocialToken token = Ebean.find(SecureSocialToken.class)
                .where().eq("uuid", tokenId).findUnique();

        if(token == null){
            flash("error", "That verification code is invalid or has expired. Please use the link below to send a new verification code.");
            return redirect(AUTH_LOGIN);
        }

        User user = userDao.findUserByEmail(token.getEmail());
        user.setVerified(new Date());
        userDao.saveUser(user);

        F.Promise.promise(() -> {
            emailSender.sendMail(user.getEmail(), "Welcome to Victiv.com", new WelcomeEmail(user));
            return null;
        });

        Ebean.delete(token);

        AuthenticatorService service = AppEnvironment.getEnvironment().authenticatorService();
        Option cookieAuthenticatorBuilder = service.find("cookie");
        AuthenticatorBuilder builder = (AuthenticatorBuilder) cookieAuthenticatorBuilder.get();
        Future<Authenticator<User>> obj = builder.fromUser(user);

        try {
            Authenticator<User> result = Await.result(obj, Duration.create(5, "seconds"));
            Success success2 = (Success) result.starting(redirect("/#lobby").toScala()).value().get();
            play.api.mvc.Result redirect1 = (play.api.mvc.Result) success2.value();
            String cookieVal = redirect1.header().headers().get("Set-Cookie").get();
            String[] cookieParts = cookieVal.split("=");
            response().setCookie(cookieParts[0], cookieParts[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return redirect("/#lobby");
    }

    public static Result sendResetPassword(){
        Map<String,String[]> form = request().body().asFormUrlEncoded();
        String email = form.get("email")[0];

        User user = userDao.findUserByEmail(email);

        if(user == null){
            flash("error", "An email has been sent to " + email);
            return redirect(AUTH_LOGIN);
        }

        if(user.getProviderId().equals("userpass")){
            F.Promise.promise(() -> {
                if(user.getVerified() == null){
                    SecureSocialToken token = new SecureSocialToken(UUID.randomUUID().toString());
                    DateTime now = new DateTime();
                    token.setCreationTime(now);
                    token.setEmail(user.getEmail());
                    token.setExpirationTime(now.plusDays(1));
                    token.setSignUp(true);
                    Ebean.save(token);
                    emailSender.sendMail(user.getEmail(), "Please confirm email",
                            new VerifyEmailAddress(
                                    Play.application().configuration().getString("external.url") + "/confirmEmail/" + token.getUuid(),
                                    user));
                    Logger.info("Sending re-verification email to " + user.getEmail());
                } else {
                    SecureSocialToken token = new SecureSocialToken(UUID.randomUUID().toString());
                    DateTime now = new DateTime();
                    token.setCreationTime(now);
                    token.setEmail(user.getEmail());
                    token.setExpirationTime(now.plusDays(1));
                    token.setSignUp(false);
                    Ebean.save(token);
                    emailSender.sendMail(user.getEmail(), "VICTIV password reset",
                            new ResetPassword(Play.application().configuration().getString("external.url")
                                    + "/auth/reset/"+token.getUuid()));
                    Logger.info("Sending password reset email to " + user.getEmail());
                }
                return null;
            });
            flash("error", "An email has been sent to " + email);
        } else {
            flash("error", String.format("Email '%s' is already registered using a social network like Facebook or Google. Please login using that social network icon via our login page.", email));
        }
        return redirect(AUTH_LOGIN);
    }

    public static Result resetPassword(String tokenId){
        return redirect("/auth/login");
    }
}
