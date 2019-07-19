package controllers;

import dao.IUserDao;
import distributed.DistributedServices;
import models.user.Country;
import models.user.StateProvince;
import models.user.User;
import models.user.UserBonus;
import models.wallet.UserWallet;
import play.mvc.Result;
import securesocial.core.java.SecuredAction;
import securesocial.core.java.UserAwareAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mwalsh on 7/22/14.
 */
public class WalletController extends AbstractController {

    private final static IUserDao userDao = DistributedServices.getContext().getBean("userDao", IUserDao.class);

    @UserAwareAction
    public static Result getWallet() {
        Map<String, Object> retValue = new HashMap<>();
        User user = getCurrentUser();
        if (user == null) {
            return jok(retValue);
        }

        UserWallet wallet = userDao.getUserWallet(getCurrentUser());

        if (wallet == null) {
            wallet = new UserWallet(user);
        }

        retValue.put("username", wallet.getUser().getUserName());
        retValue.put("usd", wallet.getUsd());
        retValue.put("loyalty", wallet.getLoyaltyPoints());

        List<Map<String, Object>> bonuses = new ArrayList<>();
        for (UserBonus userBonus : wallet.getUserBonuses()) {
            Map<String, Object> map = new HashMap<>();
            map.put("amount", userBonus.getAmount());
            map.put("type", userBonus.getUserBonusType().getName());
            bonuses.add(map);
        }
        retValue.put("bonuses", bonuses);

        return jok(retValue);
    }

    @SecuredAction
    public static Result staticData() {
        Map<String, List<Map<String, String>>> retValue = new HashMap<>();

        List<Map<String, String>> us = new ArrayList<>();
        for (StateProvince c : StateProvince.ALL_US) {
            Map<String, String> cVal = new HashMap<>();
            cVal.put("name", c.getName());
            cVal.put("abbr", c.getAbbreviation());
            us.add(cVal);
        }

        List<Map<String, String>> canadians = new ArrayList<>();
        for (StateProvince c : StateProvince.ALL_CANADA) {
            Map<String, String> cVal = new HashMap<>();
            cVal.put("name", c.getName());
            cVal.put("abbr", c.getAbbreviation());
            canadians.add(cVal);
        }

        List<Map<String, String>> countries = new ArrayList<>();
        for (Country c : Country.values()) {
            Map<String, String> cVal = new HashMap<>();
            cVal.put("name", c.getName());
            cVal.put("abbr", c.getAbbreviation());
            countries.add(cVal);
        }

        retValue.put("US", us);
        retValue.put("CA", canadians);
        retValue.put("countries", countries);

        return jok(retValue);
    }
}