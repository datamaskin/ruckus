package wallet.paypal;

import wallet.PayPalRetriever;

import java.io.IOException;

/**
 * Created by mwalsh on 8/25/14.
 */
public class PayPalTest {


    public static void main(String[] args) throws Exception {
        new PayPalTest().run();
    }

    private void run() throws IOException {
        //client makes request to our server
        PayPalRetriever retriever = new PayPalRetriever();
        String token = retriever.getNvpToken(amount);

        if(token == null){
            throw new IllegalArgumentException("Could not start transaction with PayPal.");
        }

        //Step 3. https://developer.paypal.com/docs/classic/express-checkout/ht_ec-singleItemPayment-curl-etc/
        //build the url to paypal and redirect the user there.

        System.out.println("https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token="+token);



    }

}
