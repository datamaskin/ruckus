package controllers;

import dao.DaoFactory;
import models.user.User;
import models.wallet.UserWallet;
import play.mvc.Result;
import securesocial.core.java.UserAwareAction;
import views.html.pub.*;

/**
 * Created by mgiles on 8/15/14.
 */
public class PublicController extends AbstractController {

    ////////////////// landing page ////////////////
    @UserAwareAction
    public static Result landing() {
        User user = getCurrentUser();
        if(user == null){
            return ok(landing.render(null, null));
        } else {
            UserWallet wallet = DaoFactory.getUserDao().getUserWallet(user);
            return ok(landing.render(user, wallet));
        }
    }
    ////////////////// landing page ////////////////

    ////////////////// howitworks page ////////////////
    @UserAwareAction
    public static Result howitworks() {
        User user = getCurrentUser();

        if(user == null){
            return ok(how.render(null, null));
        } else {
            UserWallet wallet = DaoFactory.getUserDao().getUserWallet(user);
            return ok(how.render(user, wallet));
        }

    }
    ////////////////// howitworks page ////////////////

    ////////////////// about page ////////////////
    @UserAwareAction
    public static Result about() {
        User user = getCurrentUser();

        if(user == null){
            return ok(about.render(null, null));
        } else {
            UserWallet wallet = DaoFactory.getUserDao().getUserWallet(user);
            return ok(about.render(user, wallet));
        }

    }
    ////////////////// about page ////////////////

    ////////////////// affiliates page ////////////////
    @UserAwareAction
    public static Result affiliates() {
        User user = getCurrentUser();

        if(user == null){
            return ok(affiliates.render(null, null));
        } else {
            UserWallet wallet = DaoFactory.getUserDao().getUserWallet(user);
            return ok(affiliates.render(user, wallet));
        }

    }
    ////////////////// affiliates page ////////////////

    ////////////////// legal page ////////////////
    @UserAwareAction
    public static Result legal() {
        User user = getCurrentUser();

        if(user == null){
            return ok(legal.render(null, null));
        } else {
            UserWallet wallet = DaoFactory.getUserDao().getUserWallet(user);
            return ok(legal.render(user, wallet));
        }

    }
    ////////////////// legal page ////////////////

    ////////////////// privacy page ////////////////
    @UserAwareAction
    public static Result privacy() {
        User user = getCurrentUser();

        if(user == null){
            return ok(privacypolicy.render(null, null));
        } else {
            UserWallet wallet = DaoFactory.getUserDao().getUserWallet(user);
            return ok(privacypolicy.render(user, wallet));
        }

    }
    ////////////////// privacy page ////////////////

    ////////////////// promos page ////////////////
    @UserAwareAction
    public static Result promos() {
        User user = getCurrentUser();

        if(user == null){
            return ok(promos.render(null, null));
        } else {
            UserWallet wallet = DaoFactory.getUserDao().getUserWallet(user);
            return ok(promos.render(user, wallet));
        }

    }
    ////////////////// promos page ////////////////

    ////////////////// support page ////////////////
    @UserAwareAction
    public static Result support() {
        User user = getCurrentUser();

        if(user == null){
            return ok(support.render(null, null));
        } else {
            UserWallet wallet = DaoFactory.getUserDao().getUserWallet(user);
            return ok(support.render(user, wallet));
        }

    }
    ////////////////// landing page ////////////////

    ////////////////// termsofuse page ////////////////
    @UserAwareAction
    public static Result termsofuse() {
        User user = getCurrentUser();

        if(user == null){
            return ok(termsofuse.render(null, null));
        } else {
            UserWallet wallet = DaoFactory.getUserDao().getUserWallet(user);
            return ok(termsofuse.render(user, wallet));
        }

    }
    ////////////////// termsofuse page ////////////////

}
