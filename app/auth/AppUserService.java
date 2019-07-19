package auth;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import dao.IUserDao;
import models.user.SecureSocialToken;
import models.user.User;
import models.wallet.UserWallet;
import play.Logger;
import play.libs.F;
import scala.Option;
import scala.Some;
import securesocial.core.AuthenticationMethod;
import securesocial.core.BasicProfile;
import securesocial.core.PasswordInfo;
import securesocial.core.java.BaseUserService;
import securesocial.core.java.Token;
import securesocial.core.services.SaveMode;

import java.util.Date;
import java.util.List;

public class AppUserService extends BaseUserService<User> {

    private IUserDao appUserDao;

    public AppUserService(IUserDao appUserDao) {
        this.appUserDao = appUserDao;
    }

    @Override
    public F.Promise doSave(BasicProfile profile, SaveMode mode) {
        String email = profile.email().isEmpty() ? "" : profile.email().get();
        String userName = profile.userId();
        String providerId = profile.providerId();
        if (!profile.authMethod().is(AuthenticationMethod.UserPassword())) {
            userName = email;
        }
        User user = appUserDao.findUserByEmail(email);

        if (user == null) {
            user = new User();
            user.setProviderId(providerId);
            user.setEmail(email);
            if (!profile.firstName().isEmpty()) {
                user.setFirstName(profile.firstName().get());
            }
            if (!profile.lastName().isEmpty()) {
                user.setLastName(profile.lastName().get());
            }
            user.setUserName(userName);

            if (!profile.passwordInfo().isEmpty()) {
                user.setPassword(profile.passwordInfo().get().password());
            }

            if (!profile.authMethod().is(AuthenticationMethod.UserPassword())) {
                user.setVerified(new Date());
            }

            appUserDao.saveUser(user);

            //TODO REMOVE ME I"M ADDING LOTS OF MONEY FOR NEW USERS SO THEY DON"T HAVE TO DEPOSIT
            //TODO REMOVE ME I"M ADDING LOTS OF MONEY FOR NEW USERS SO THEY DON"T HAVE TO DEPOSIT
            //TODO REMOVE ME I"M ADDING LOTS OF MONEY FOR NEW USERS SO THEY DON"T HAVE TO DEPOSIT
            //TODO REMOVE ME I"M ADDING LOTS OF MONEY FOR NEW USERS SO THEY DON"T HAVE TO DEPOSIT
            //TODO REMOVE ME I"M ADDING LOTS OF MONEY FOR NEW USERS SO THEY DON"T HAVE TO DEPOSIT
            UserWallet userWallet = new UserWallet(user);
            userWallet.updateUsd(1000000);
            appUserDao.updateWallet(userWallet);
            //TODO REMOVE ME I"M ADDING LOTS OF MONEY FOR NEW USERS SO THEY DON"T HAVE TO DEPOSIT
            //TODO REMOVE ME I"M ADDING LOTS OF MONEY FOR NEW USERS SO THEY DON"T HAVE TO DEPOSIT
            //TODO REMOVE ME I"M ADDING LOTS OF MONEY FOR NEW USERS SO THEY DON"T HAVE TO DEPOSIT
            //TODO REMOVE ME I"M ADDING LOTS OF MONEY FOR NEW USERS SO THEY DON"T HAVE TO DEPOSIT
            //TODO REMOVE ME I"M ADDING LOTS OF MONEY FOR NEW USERS SO THEY DON"T HAVE TO DEPOSIT

        } else {
            if (!profile.passwordInfo().isEmpty()) {
                user.setPassword(profile.passwordInfo().get().password());
                appUserDao.saveUser(user);
            }
        }

        return F.Promise.pure(user);
    }

    @Override
    public F.Promise<Token> doSaveToken(Token token) {
        Logger.info("doSave(Token token)");
        SecureSocialToken newtoken = new SecureSocialToken(token.getUuid());
        newtoken.setCreationTime(token.getCreationTime());
        newtoken.setEmail(token.getEmail());
        newtoken.setExpirationTime(token.getExpirationTime());
        newtoken.setSignUp(token.getIsSignUp());
        appUserDao.saveToken(newtoken);
        return F.Promise.pure(token);
    }

    @Override
    public F.Promise<User> doLink(User current, BasicProfile to) {
        User user = Ebean.find(User.class).where(Expr.eq(User.EMAIL, to.email())).findUnique();
        return F.Promise.pure(user);
    }

    @Override
    public F.Promise<BasicProfile> doFind(String providerId, String userId) {
        Logger.info("doFind:" + userId + "," + providerId);

        User localUser = appUserDao.findUserByUsername(userId);
        if(localUser == null){
            localUser = appUserDao.findUserByEmail(userId);
        }

        if (localUser != null && localUser.getVerified() != null && localUser.getPassword() != null) {
            Logger.info("Found! " + localUser.getEmail());

            BasicProfile socialUser =
                    new BasicProfile(providerId, userId, Option.apply(localUser.getFirstName()), Option.apply(localUser.getLastName()), Option.apply(String.format(
                            "%s %s", localUser.getFirstName(), localUser.getLastName())), Option.apply(localUser
                            .getEmail()), null, new AuthenticationMethod("userPassword"), null, null,
                            Some.apply(new PasswordInfo("bcrypt", localUser.getPassword(), null)));
            return F.Promise.pure(socialUser);
        } else {
            return F.Promise.pure(null);
        }
    }

    @Override
    public F.Promise<PasswordInfo> doPasswordInfoFor(User user) {
        return null;
    }

    @Override
    public F.Promise<BasicProfile> doUpdatePasswordInfo(User user, PasswordInfo info) {
        return null;
    }

    @Override
    public F.Promise<Token> doFindToken(String tokenId) {
        Logger.info("doFindToken");
        SecureSocialToken sst = appUserDao.findToken(tokenId);

        if (sst == null) {
            return F.Promise.pure(null);
        }

        Token token = new Token();
        token.setCreationTime(sst.getCreationTime());
        token.setEmail(sst.getEmail());
        token.setExpirationTime(sst.getExpirationTime());
        token.setIsSignUp(sst.isSignUp());
        token.setUuid(sst.getUuid());
        return F.Promise.pure(token);
    }

    @Override
    public F.Promise<BasicProfile> doFindByEmailAndProvider(String email, String providerId) {
        Logger.debug("findByEmailAndProvider...");
        Logger.debug(String.format("email = %s", email));
        Logger.debug(String.format("providerId = %s", providerId));

        List<User> list = appUserDao.findUsers(email, providerId);

        if (list.size() != 1) {
            return F.Promise.pure(null);
        } else if(list.get(0).getVerified() == null){
            return F.Promise.pure(null);
        }

        User localUser = list.get(0);
        BasicProfile socialUser =
                new BasicProfile(providerId, localUser.getId().toString(),
                        Option.apply(localUser.getFirstName()), Option.apply(localUser.getLastName()),
                        Option.apply(String.format("%s %s", localUser.getFirstName(), localUser.getLastName())),
                        Option.apply(localUser.getEmail()), null, new AuthenticationMethod("userPassword"), null,
                        null, Some.apply(new PasswordInfo("bcrypt", localUser.getPassword(), null)));
        if (Logger.isDebugEnabled()) {
            Logger.debug(String.format("socialUser = %s", socialUser));
        }
        return F.Promise.pure(socialUser);
    }

    @Override
    public F.Promise<Token> doDeleteToken(String uuid) {
        Ebean.delete(SecureSocialToken.class, uuid);
        return F.Promise.pure(null);
    }

    @Override
    public void doDeleteExpiredTokens() {
        Logger.info("doDeleteExpiredTokens()");
    }
}
