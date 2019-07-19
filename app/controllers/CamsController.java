package controllers;

import dao.DaoFactory;
import dao.IUserDao;
import distributed.DistributedServices;
import models.user.StateProvince;
import models.user.User;
import models.user.UserProfile;
import models.wallet.UserWallet;
import models.wallet.UserWalletTxn;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.json.JSONException;
import org.json.JSONObject;
import play.mvc.Http;
import play.mvc.Result;
import securesocial.core.java.SecuredAction;
import utils.ITimeService;
import wallet.CamsFormUrlResponse;
import wallet.CamsOrderResponse;
import wallet.CamsRetriever;
import wallet.CreditCardDetector;

import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by mwalsh on 8/27/14.
 */
public class CamsController extends AbstractController {

    private static final IUserDao userDao = DistributedServices.getContext().getBean("userDao", IUserDao.class);

    @SecuredAction
    public static Result getCamsForm() throws UnknownHostException {
        Date userDob = getCurrentUser().getDateOfBirth();

        if(userDob == null){
            return ok(views.html.camsForm.render(false));
        }

        Instant instant = Instant.ofEpochMilli(userDob.getTime());
        LocalDate dob = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
        Period period = Period.between(dob, LocalDate.now());

        return ok(views.html.camsForm.render(period.getYears() >= 21));
    }

    private static boolean verifyUser(LocalDate dob, StateProvince sp){
        Period period = Period.between(dob, LocalDate.now());

        int minimumAge = sp.equals(StateProvince.US_AL) || sp.equals(StateProvince.US_NE) ? 19 : 18;

        boolean stateLegal = !sp.equals(StateProvince.US_AZ) &&
                !sp.equals(StateProvince.US_LA) &&
                !sp.equals(StateProvince.US_MO) &&
                !sp.equals(StateProvince.US_WA);

        return (period.getYears() >= minimumAge && stateLegal);
    }

    @SecuredAction
    public static Result postFirstVerify() {
        User user = getCurrentUser();
        String host = getIpAddress();
        Http.RequestBody body = request().body();
        Map<String, String[]> form = body.asFormUrlEncoded();

        Integer day = Integer.parseInt(form.get("dobDay")[0]);
        Integer month = Integer.parseInt(form.get("dobMonth")[0]);
        Integer year = Integer.parseInt(form.get("dobYear")[0]);
        LocalDate dob = LocalDate.of(year, month, day);
        user.setDateOfBirth(Date.from(dob.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        userDao.saveUser(user);

        StateProvince sp = StateProvince.getStateProvince(form.get("state")[0]);

        if(verifyUser(dob, sp)){
            CamsRetriever camsRetriever = new CamsRetriever();
            CamsFormUrlResponse response = camsRetriever.stepOne(
                    host, "0.00", "USD",
                    UUID.randomUUID().toString(),
                    user.getFirstName(),
                    user.getLastName(),
                    form.get("address1")[0],
                    form.get("address2")[0],
                    form.get("city")[0],
                    sp.getAbbreviation(),
                    form.get("postal")[0],
                    form.get("country")[0]);

            return ok(response.getFormUrl());
        } else {
            return ok("invalid");
        }
    }

    @SecuredAction
    @Deprecated
    public static Result getUserProfiles() throws UnknownHostException {
        String host = getIpAddress();
        User user = getCurrentUser();
        List<UserProfile> userProfiles = userDao.getUserProfiles(user);
        List<Map<String, String>> result = new ArrayList<>();
        for (UserProfile userProfile : userProfiles) {

            Map<String, String> row = new HashMap<>();
            row.put("id", String.valueOf(userProfile.getId()));
            row.put("address1", userProfile.getAddress1());
            row.put("address2", userProfile.getAddress2());
            row.put("city", userProfile.getCity());
            row.put("stateProvince", userProfile.getStateProvince().getAbbreviation());
            row.put("postalCode", userProfile.getPostalCode());
            row.put("country", userProfile.getCountry().getAbbreviation());

            if (userProfile.getCamsTokenId() != null) {
                row.put("tokenId", "true");
                String cc = userProfile.getCcNumber();
                row.put("ccNumber", cc.substring(cc.length() - 4, cc.length()));
                row.put("ccExpMonth", String.valueOf(userProfile.getCcExpMonth()));
                row.put("ccExpYear", String.valueOf(userProfile.getCcExpYear()));
                row.put("ccType", userProfile.getCcType().name());
            } else {
                CamsRetriever camsRetriever = new CamsRetriever();
                CamsFormUrlResponse response = camsRetriever.stepOne(
                        host, "0.00", "USD",
                        UUID.randomUUID().toString(),
                        user.getFirstName(),
                        user.getLastName(),
                        userProfile.getAddress1(),
                        userProfile.getAddress2(),
                        userProfile.getCity(),
                        userProfile.getStateProvince().getAbbreviation(),
                        userProfile.getPostalCode(),
                        userProfile.getCountry().name());
                row.put("formUrl", response.getFormUrl());
            }

            result.add(row);
        }

        return jok(result);
    }

    @SecuredAction
    public static Result deleteCreditCard() {
        try {
            JSONObject o = new JSONObject(request().body().asJson().toString());
            UserProfile profile = userDao.getUserProfile(getCurrentUser(), Integer.parseInt(o.getString("profile")));
            userDao.deleteCreditCard(profile);
            return ok("success");
        } catch (JSONException e) {
            e.printStackTrace();
            return jerr("Could not delete credit card.");
        }
    }

    @SecuredAction
    public static Result authorizeDeposit() {
        try {
            final JSONObject o = new JSONObject(request().body().asJson().toString());
            final CamsRetriever retriever = new CamsRetriever();

            final String amountStr = o.getString("authorizeDepositAmount");
            final Double amount = Double.parseDouble(amountStr);
            // TODO MORE validation of amounts
            if (amount > 3000) {
                return jerr("No, seriously.");
            }

            if (!new DecimalFormat("#.00").format(amount).equals(amountStr)) {
                return jerr("Please enter amounts with dollars and cents (e.g. 20.50)");
            }

            final String orderId = UUID.randomUUID().toString();
            final User user = getCurrentUser();
            final UserProfile userProfile = userDao.getUserProfile(user, Integer.parseInt(o.getString("profile")));

            //Convert to cents
            final Integer intAmount = (int) (amount * 100);

            // Get response from CAMS
            final String responseText = retriever.postSalesTransaction(userProfile, amountStr, orderId, getIpAddress());

            // Record transaction
            final ITimeService timeService = DistributedServices.getContext().getBean("timeService", ITimeService.class);
            final Date timestamp = Date.from(timeService.getNowAsZonedDateTime().toInstant());
            final UserWallet wallet = userDao.getUserWallet(user);
            final Map<String, String> responseItems = URLEncodedUtils.parse(responseText, Charset.forName("UTF-8"))
                    .stream().collect(Collectors.toMap(NameValuePair::getName, NameValuePair::getValue));
            final String ccLastFour = userProfile.getCcNumber().substring(userProfile.getCcNumber().length() - 4);
            // Will throw a NPE if the response_code isn't there.
            final UserWalletTxn txn = UserWalletTxn.camsTxn(wallet, 0, 0, null, Integer.valueOf(responseItems.get("response_code")),
                    responseItems.get("authcode"));

            if (txn.camsDetail().camsResultCode() == 100) {
                txn.camsDetail().setDescription("Deposit from credit card ending in " + ccLastFour);
                txn.setAmount(intAmount);
                txn.setBalanceAfter(wallet.getUsd() + intAmount);
                userDao.plusUsd(user, intAmount);
                DaoFactory.getWalletDao().save(txn);
                return ok("success");
            } else {
                txn.camsDetail().setDescription("Rejected deposit attempt from credit card ending in " + ccLastFour);
                txn.setAmount(0);
                txn.setBalanceAfter(wallet.getUsd());
                DaoFactory.getWalletDao().save(txn);
                return jerr("Error depositing money. Try again.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return jerr("Could not make sale.");
        }
    }

    @SecuredAction
    public static Result removeBillingAddress() {
        try {
            JSONObject o = new JSONObject(request().body().asJson().toString());
            UserProfile profile = userDao.getUserProfile(getCurrentUser(), Integer.parseInt(o.getString("profile")));
            profile.setActive(false);
            userDao.saveUserProfile(profile);
            return ok("success");
        } catch (JSONException e) {
            e.printStackTrace();
            return jerr(e.getMessage());
        }
    }

    @SecuredAction
    public static Result camsRedirect() {
        Map<String, String[]> query = request().queryString();
        String tokenId = query.get("token-id")[0];

        CamsRetriever camsRetriever = new CamsRetriever();
        CamsOrderResponse camsOrderResponse = camsRetriever.stepThree(tokenId);

        if (camsOrderResponse.getResultCode() == 100) {
            CamsOrderResponse.CamsOrderResponseBilling billing = camsOrderResponse.getCamsOrderResponseBilling();
            UserProfile userProfile = new UserProfile(getCurrentUser(),
                    billing.getAddress1(), billing.getAddress2(),
                    billing.getCity(), StateProvince.getStateProvince(billing.getState()), billing.getPostal());

            String e = camsOrderResponse.getCamsOrderResponseBilling().getCcExp();
            String cc = camsOrderResponse.getCamsOrderResponseBilling().getCcNumber();

            userProfile.setCamsTokenId(camsOrderResponse.getTransactionId());
            userProfile.setCcNumber(cc);
            userProfile.setCcExpMonth(Integer.parseInt(e.substring(0, 2)));
            userProfile.setCcExpYear(Integer.parseInt(e.substring(2, 4)));
            userProfile.setCcType(new CreditCardDetector().getCreditCardType(cc.replaceAll("\\*", "0")));
            userDao.saveUserProfile(userProfile);

        }

        return ok("success! please refresh the page.");
    }

//    @SecuredAction
//    public static Result ageVerification(){
//        Map<String, String[]> form = request().body().asFormUrlEncoded();
//
//        String yearStr = form.get("year")[0];
//        String monthStr = form.get("month")[0];
//        String dayStr = form.get("day")[0];
//
//        if(StringUtils.isEmpty(yearStr) || StringUtils.isEmpty(monthStr) || StringUtils.isEmpty(dayStr)){
//            flash().put("error", "Day, month, and year are all required");
//            return ok(views.html.camsForm.render(false));
//        }
//
//        try {
//            int year = Integer.parseInt(yearStr);
//            int month = Integer.parseInt(monthStr);
//            int day = Integer.parseInt(dayStr);
//            LocalDate dob = LocalDate.of(year, month, day);
//            Period period = Period.between(dob, LocalDate.now());
//
//            User user = getCurrentUser();
//            user.setDateOfBirth(Date.from(dob.atStartOfDay(ZoneId.systemDefault()).toInstant()));
//            userDao.saveUser(user);
//
//            if(period.getYears() >= 21){
//                return ok(views.html.camsForm.render(true));
//            } else {
//                return ok(views.html.camsForm.render(false));
//            }
//        } catch(Exception e) {
//            flash().put("error", "There was an error with input.");
//            return ok(views.html.camsForm.render(false));
//        }
//
//
//    }

}
