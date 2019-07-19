package wallet.bitcoin;

import com.amazonaws.util.json.JSONObject;
import wallet.CoinbaseRetriever;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mwalsh on 7/22/14.
 */
public class CoinbaseTest {

    public static void main(String[] args) throws Exception {

        String something = CoinbaseRetriever.get("accounts");
        System.out.println(something);

        JSONObject json = new JSONObject();

        Map<String, Object> attrs = new HashMap<>();
        attrs.put("name", "test");
        attrs.put("price_string", "0.01");
        attrs.put("price_currency_iso", "USD");
        attrs.put("choose_price", true);
        attrs.put("variable_price", true);

        json.put("account_id", "53cf31f9cb16b5a38d000006");
        json.put("button", attrs);

        String request = json.toString();

        String response = CoinbaseRetriever.post("buttons", request);
        System.out.println(">>" + request);
        System.out.println(">>" + response);
        System.out.println(
                new JSONObject(response).getJSONObject("button")
        .getString("code"));

    }

}
