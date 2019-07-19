package wallet;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import play.Play;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by mwalsh on 7/23/14.
 */
public class CoinbaseRetriever {

    private static String API_KEY = Play.application().configuration().getString("coinbase.api.key");
    private static String API_SECRET = Play.application().configuration().getString("coinbase.api.secret");
    private static String BASE_URL = "https://coinbase.com/api/v1/";

    public static String get(String action) throws Exception {
        String url = BASE_URL + action;
        String nonce = String.valueOf(System.currentTimeMillis());
        String message = nonce + url;

        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(API_SECRET.getBytes(), "HmacSHA256"));
        String signature = new String(Hex.encodeHex(mac.doFinal(message.getBytes())));

        HttpRequestBase request = new HttpGet(url);

        request.setHeader("ACCESS_KEY", API_KEY);
        request.setHeader("ACCESS_SIGNATURE", signature);
        request.setHeader("ACCESS_NONCE", nonce);

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse response = httpClient.execute(request);

        HttpEntity entity = response.getEntity();
        if (entity != null)
            return EntityUtils.toString(entity);
        return null;
    }

    public static String post(String action, String body) throws Exception {
        if(API_SECRET.isEmpty()){
            return null;
        }
        String url = BASE_URL + action;
        String nonce = String.valueOf(System.currentTimeMillis());
        String message = nonce + url + (body != null ? body : "");

        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(API_SECRET.getBytes(), "HmacSHA256"));
        String signature = new String(Hex.encodeHex(mac.doFinal(message.getBytes())));

        HttpRequestBase request;
        if (body == null || body.length() == 0)
            request = new HttpGet(url);
        else {
            HttpPost post = new HttpPost(url);
            post.setEntity(new StringEntity(body));
            request = post;
        }
        request.setHeader("Content-Type", "application/json");
        request.setHeader("ACCESS_KEY", API_KEY);
        request.setHeader("ACCESS_SIGNATURE", signature);
        request.setHeader("ACCESS_NONCE", nonce);

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse response = httpClient.execute(request);

        HttpEntity entity = response.getEntity();
        if (entity != null)
            return EntityUtils.toString(entity);
        return null;
    }
}
