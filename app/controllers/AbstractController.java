package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.ClientMessage;
import common.GlobalConstants;
import common.SecureSocialCookie;
import dao.DaoFactory;
import distributed.DistributedServices;
import models.user.User;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Http;
import securesocial.core.java.SecureSocial;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by mgiles on 5/15/14.
 */
public abstract class AbstractController extends Controller {

    private static Status jok(Object content, boolean error) {
        try {
            session().put("SecureSocial.OriginalUrlKey", "/#lobby");
        } catch (Exception e) {
            Logger.debug(e.getMessage());
        }
        try {
            ClientMessage message = new ClientMessage();
            String type = error ? "ERROR" : "OK";
            message.setType(type);
            message.setPayload(content);
            return ok(new ObjectMapper().writeValueAsString(message));
        } catch (JsonProcessingException e) {
            return ok(content.toString());
        }
    }

    public static Status jok(Object content) {
        return jok(content, false);
    }

    public static Status jerr(Object content) {
        return jok(content, true);
    }

    protected static User getCurrentUser() {
        User user = (User) ctx().args.get(SecureSocial.USER_KEY);
        if (user != null && user.getEmail().equals(user.getUserName())) {
            user = DaoFactory.getUserDao().findUser(user.getId());
        }
        Http.Cookie cookie = request().cookie(SecureSocialCookie.getName());
        if (cookie != null) {
            Long userId = (Long) DistributedServices.getInstance().getMap(GlobalConstants.DISTRIBUTED_SESSION_MAP)
                    .get(cookie.value());
            //we know the user but there is no distributed version, so store it
            if (userId == null && user != null) {
                DistributedServices.getInstance()
                        .getMap(GlobalConstants.DISTRIBUTED_SESSION_MAP).put(cookie.value(), user.getId());
            } else if (userId != null && user == null) {
                user = DaoFactory.getUserDao().findUser(userId);
            }
        }
        return user;
    }

    //TODO - change this to get IP from FORWARDED-FOR header
    public static String getIpAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return null;
        }
    }

}
