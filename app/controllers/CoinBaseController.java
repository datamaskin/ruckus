package controllers;

import dao.DaoFactory;
import dao.IUserDao;
import dao.WalletDao;
import distributed.DistributedServices;
import models.user.User;
import models.wallet.UserWallet;
import models.wallet.UserWalletTxn;
import models.wallet.VictivTxnType;
import org.json.JSONObject;
import play.mvc.Http;
import play.mvc.Result;
import securesocial.core.java.SecuredAction;
import wallet.CoinbaseRetriever;
import wallet.WalletException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mwalsh on 8/26/14.
 */
public class CoinBaseController extends AbstractController {

    private final static IUserDao userDao = DistributedServices.getContext().getBean("userDao", IUserDao.class);

    @SecuredAction
    public static Result getBitcoinButton() {
        try {
            Map<String, Object> attrs = new HashMap<>();
            attrs.put("name", "test");
            attrs.put("price_string", "0.01");
            attrs.put("price_currency_iso", "USD");
            attrs.put("choose_price", true);
            attrs.put("variable_price", true);
            attrs.put("custom", getCurrentUser().getEmail());

            JSONObject json = new JSONObject();
            json.put("button", attrs);

            String request = json.toString();
            String response = CoinbaseRetriever.post("buttons", request);
            if(response == null){
                return jerr("bitcoin depositing is not enabled");
            } else {
                return jok(new JSONObject(response).getJSONObject("button").getString("code"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return jerr(e.getMessage());
        }
    }

    public static Result bitcoinReceive() {
        try {
            JSONObject obj = new JSONObject(request().body().asJson().toString());
            int money = obj.getJSONObject("order").getJSONObject("total_native").getInt("cents");
            User user = userDao.findUserByEmail(obj.getJSONObject("order").getString("custom"));
            final UserWallet wallet = DaoFactory.getUserDao().getUserWallet(user);
            final UserWalletTxn txn = UserWalletTxn.bitcoinTxn(wallet, money, (wallet.getUsd() + money),
                    "Money deposited from bitcoin");
            userDao.plusUsd(user, money);
            DaoFactory.getWalletDao().save(txn);
            return jok("payment received");
        } catch (Exception e) {
            e.printStackTrace();
            return jerr(e.getMessage());
        }
    }

    @SecuredAction
    public static Result bitcoinWithdrawal(){
        try {
            User user = getCurrentUser();
            UserWallet wallet = userDao.getUserWallet(user);

            if(wallet == null){
                return jerr("Insufficient funds.");
            }

            Http.RequestBody requestBody = request().body();
            JSONObject obj = new JSONObject(requestBody.asJson().toString());

            String withdrawalBitcoinAddress = obj.getString("withdrawalBitcoinAddress");
            int withdrawalAmount = (int) (obj.getDouble("withdrawalAmount") * 100);

            if(wallet.getUsd() >= withdrawalAmount){
                JSONObject attrs = new JSONObject()
                        .put("to", withdrawalBitcoinAddress)
                        .put("amount_currency_iso", "USD")
                        .put("notes", "bitcoin withdraw from Victovo")
                        .put("amount", withdrawalAmount);

                JSONObject transaction = new JSONObject().put("transaction", attrs);
                String result = CoinbaseRetriever.post("transactions/send_money", transaction.toString());
                try{
                    final UserWalletTxn txn = UserWalletTxn.bitcoinTxn(wallet, withdrawalAmount,
                            (wallet.getUsd() - withdrawalAmount), "Money withdrawn to bitcoin");
                    userDao.minusUsd(user, withdrawalAmount);
                    DaoFactory.getWalletDao().save(txn);
                } catch (WalletException e){
                    return jerr("Not enough funds.");
                }

                return jok("payment received");
            } else {
                return jerr("Not enough funds.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return jerr(e.getMessage());
        }
    }
}
