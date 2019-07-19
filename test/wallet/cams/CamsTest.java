package wallet.cams;

import org.apache.http.message.BasicNameValuePair;
import wallet.CamsRetriever;
import wallet.WalletException;

import java.net.InetAddress;
import java.util.UUID;

/**
 * Created by mwalsh on 7/13/14.
 */
public class CamsTest {

    public final static String CAMS_URL = "https://secure.verifi.com/gw/api/transact.php";

    public static void main(String[] args) throws Exception {
        new CamsTest().run();
    }

    private void beginThreeStepProcess(
            String apiKey,
            String redirectUrl,
            InetAddress ipAddress) throws WalletException {
        String xml = String.format("<validate>\n" +
                        "   <api-key>%s</api-key>\n" +
                        "   <redirect-url>%s</redirect-url>\n" +
                        "   <ip-address>%s</ip-address>\n" +
                        "   <amount>%s</amount>\n" +
                        "   <currency>%s</currency>\n" +
                        "   <order-id>%s</order-id>\n" +
                        "   <merchant-defined-field-12>VAULT</merchant-defined-field-12>\n" +
                        "   <billing>\n" +
                        "       <first-name>%s</first-name>\n" +
                        "       <last-name>%s</last-name>\n" +
                        "       <address1>%s</address1>\n" +
                        "       <city>%s</city>\n" +
                        "       <state>%s</state>\n" +
                        "       <postal>%s</postal>\n" +
                        "       <country>%s</country>\n" +
                        "   </billing>\n" +
                        "</validate>",
                apiKey,
                redirectUrl,
                ipAddress.getHostAddress(),
                "0.00",
                "USD",
                UUID.randomUUID().toString(),
                "Matt",
                "Walsh",
                "123 Main",
                "Austin",
                "TX",
                "78751",
                "US");

        System.out.println(xml);

        CamsRetriever retriever = new CamsRetriever();
//        String result = retriever.postXML(THREE_STEP, xml);
//
//        CamsResponse response = retriever.getResponse(result);

    }

    private void run() throws Exception {
//
//        try {
//            beginThreeStepProcess(
//                    API_KEY,
//                    "https://www.ruckusgaming.com/CAMS.php?depositId=XXXXX",
//                    InetAddress.getLocalHost());
//        } catch (WalletException e) {
//            e.printStackTrace();
//        }

//        CloseableHttpClient httpClient = HttpClients.createDefault();
//        HttpPost postXML = new HttpPost(CAMS_URL);
//
//        List<NameValuePair> formparams = Arrays.asList(
//                e("type", "verify"),
//                e("username", "apiruckusdev"),
//                e("password", "ruckus235"),
//                e("ccnumber", "4111111111111111"),
//                e("ccexp", "0711"),
//                e("amount", "0.00"),
//                e("firstname", "John"),
//                e("lastname", "Doe"),
//                e("address1", "1234 Main St."),
//                e("address2", "Apt #1"),
//                e("city", "Los Angeles"),
//                e("state", "CA"),
//                e("zip", "90210"),
//                e("merchant_defined_field_1: \"Customer ID\"", "jdoe"),
//                e("merchant_defined_field_12: \"Verify Indicator\"", "VAULT")
//        );
//        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
//        postXML.setEntity(entity);
//        CloseableHttpResponse response = httpClient.execute(postXML);
//        System.out.println(IOUtils.toString(response.getEntity().getContent()));

    }

    private static BasicNameValuePair e(String name, String value) {
        return new BasicNameValuePair(name, value);
    }

}
