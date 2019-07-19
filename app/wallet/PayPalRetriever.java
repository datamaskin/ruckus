package wallet;

import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import play.Logger;
import play.Play;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by mwalsh on 8/25/14.
 */
public class PayPalRetriever {
    private static final String external_url = Play.application().configuration().getString("external.url");
    private static final String NVP_ENDPOINT = "https://api-3t.sandbox.paypal.com/nvp";
    public static final String FIRST_REDIRECT = "https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token=";
    private static final String TOKEN = "TOKEN";
    private static final String USER = "info-facilitator_api1.victiv.com";
    private static final String PWD = "5EWBQ5DBT7XDJGG2";
    private static final String SIGNATURE = "AbDd9Ivd0.Hed5JrMexoOUbdCbZpAj4vaS0-yeWWhP9tVjbtgk0kOIF1";

    private String post(UrlEncodedFormEntity entity) throws IOException {
        HttpPost post = new HttpPost(NVP_ENDPOINT);
        post.setEntity(entity);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse httpResponse = httpClient.execute(post);
        String response = IOUtils.toString(httpResponse.getEntity().getContent());
        return response;
    }

    public String getNvpToken(Double amount) throws IOException {
        String response = post(new UrlEncodedFormEntity(Arrays.asList(
                new BasicNameValuePair("USER", USER),
                new BasicNameValuePair("PWD", PWD),
                new BasicNameValuePair("SIGNATURE", SIGNATURE),
                new BasicNameValuePair("METHOD", "SetExpressCheckout"),
                new BasicNameValuePair("VERSION", "109.0"),
                new BasicNameValuePair("PAYMENTREQUEST_0_PAYMENTACTION", "SALE"),
                new BasicNameValuePair("PAYMENTREQUEST_0_AMT", amount.toString()),
                new BasicNameValuePair("PAYMENTREQUEST_0_ITEMAMT", amount.toString()),
                new BasicNameValuePair("PAYMENTREQUEST_0_CURRENCYCODE", "USD"),
                new BasicNameValuePair("RETURNURL", external_url + "/paypal/confirm"),
                new BasicNameValuePair("CANCELURL", external_url + "/#lobby"),
                new BasicNameValuePair("REQCONFIRMSHIPPING", "0"),
                new BasicNameValuePair("NOSHIPPING", "1"),
                new BasicNameValuePair("EMAIL", "walshms@gmail.com"),
                new BasicNameValuePair("BRANDNAME", "Victiv.com"))));

        List<NameValuePair> values = URLEncodedUtils.parse(response, StandardCharsets.UTF_8);
        for( NameValuePair nvp: values){
            if(nvp.getName().equalsIgnoreCase(TOKEN)){
                return nvp.getValue();
            }
        }
        String msg = "Received unrecognized response from PayPal.";
        Logger.error(msg);
        throw new IllegalArgumentException(msg);
    }

    public String confirmSale(String token) throws IOException {
        String checkOutDetails = post(new UrlEncodedFormEntity(Arrays.asList(
                new BasicNameValuePair("USER", USER),
                new BasicNameValuePair("PWD", PWD),
                new BasicNameValuePair("SIGNATURE", SIGNATURE),
                new BasicNameValuePair("METHOD", "GetExpressCheckoutDetails"),
                new BasicNameValuePair("VERSION", "109.0"),
                new BasicNameValuePair("TOKEN", token))));

        List<NameValuePair> values = URLEncodedUtils.parse(checkOutDetails, StandardCharsets.UTF_8);
        Map<String, String> map = values.stream().collect(Collectors.toMap(NameValuePair::getName, NameValuePair::getValue));

        String response = post(new UrlEncodedFormEntity(Arrays.asList(
                new BasicNameValuePair("USER", USER),
                new BasicNameValuePair("PWD", PWD),
                new BasicNameValuePair("SIGNATURE", SIGNATURE),
                new BasicNameValuePair("METHOD", "DoExpressCheckoutPayment"),
                new BasicNameValuePair("VERSION", "109.0"),
                new BasicNameValuePair("TOKEN", token),
                new BasicNameValuePair("PAYERID", map.get("PAYERID")),
                new BasicNameValuePair("PAYMENTREQUEST_0_PAYMENTACTION", "SALE"),
                new BasicNameValuePair("PAYMENTREQUEST_0_AMT", "100.00"),
                new BasicNameValuePair("PAYMENTREQUEST_0_ITEMAMT", "100.00"),
                new BasicNameValuePair("PAYMENTREQUEST_0_CURRENCYCODE", "USD"))));
        System.out.println("\n\n\n"+URLEncodedUtils.parse(response, StandardCharsets.UTF_8)+"\n\n\n");
        return "ok";
    }
}
