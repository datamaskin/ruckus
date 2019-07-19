package controllers;

import play.mvc.Result;
import securesocial.core.java.SecuredAction;
import wallet.PayPalRetriever;

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created by mwalsh on 8/26/14.
 */
public class PayPalController extends AbstractController {

    @SecuredAction
    public static Result start(){
        String amountStr = request().body().asFormUrlEncoded().get("amount")[0];
        Double amount = Double.parseDouble(amountStr);
        // TODO MORE validation of amounts
        if(amount > 3000){
            return jerr("Cannot process amounts over $3000.");
        }

        if(!new DecimalFormat("#.00").format(amount).equals(amountStr)){
            flash("error", "Please enter amounts with dollars and cents (e.g. 20.50)");
            return redirect("/app#deposit");
        }

        PayPalRetriever retriever = new PayPalRetriever();
        String token = null;
        try {
            token = retriever.getNvpToken(amount);
        } catch (IOException e) {
            // TODO handle this gracefully
            e.printStackTrace();
        }
        return redirect(PayPalRetriever.FIRST_REDIRECT + token);
    }

    @SecuredAction
    public static Result confirm(){
        String token = request().getQueryString("token");

        PayPalRetriever retriever = new PayPalRetriever();
        try {
            retriever.confirmSale(token);
        } catch (IOException e) {
            // TODO handle this gracefully
            e.printStackTrace();
        }
        return redirect("/app#deposit");
    }

}
