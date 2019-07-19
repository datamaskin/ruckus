package wallet;

import models.user.UserProfile;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import play.Play;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.util.Arrays;

/**
 * Created by mwalsh on 7/30/14.
 */
public class CamsRetriever {

    public static final String THREE_STEP = "https://secure.centralams.com/api/v2/three-step";
    public static final String TRANSACTIONS = "https://secure.centralams.com/gw/api/transact.php";

    private static final String API_KEY = "xVTawYMe2jTTazGV4eWr83xc86635jRx";

    private static final String validate = "<validate>\n" +
            "   <api-key>%s</api-key>\n" +
            "   <redirect-url>%s</redirect-url>\n" +
            "   <ip-address>%s</ip-address>\n" +
            "   <amount>%s</amount>\n" +
            "   <currency>%s</currency>\n" +
            "   <order-id>%s</order-id>\n" +
            "   <merchant-defined-field-3>ruckus</merchant-defined-field-3>\n" +
            "   <merchant-defined-field-12>VAULT</merchant-defined-field-12>\n" +
            "   <billing>\n" +
            "       <first-name>%s</first-name>\n" +
            "       <last-name>%s</last-name>\n" +
            "       <address1>%s</address1>\n" +
            "       <address2>%s</address2>\n" +
            "       <city>%s</city>\n" +
            "       <state>%s</state>\n" +
            "       <postal>%s</postal>\n" +
            "       <country>%s</country>\n" +
            "   </billing>\n" +
            "</validate>";

    private static final String completeAction = "<complete-action>" +
            "<api-key>%s</api-key><token-id>%s</token-id>\n" +
            "</complete-action>";

    private JAXBContext jaxbContext;

    public CamsRetriever() {
        try {
            jaxbContext = JAXBContext.newInstance(
                    CamsFormUrlResponse.class,
                    CamsOrderResponse.class);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    private final static String url = Play.application().configuration().getString("external.url") + "/wallet/camsRedirect";

    public CamsFormUrlResponse stepOne(String hostAddress, String amount, String currency,
            String orderId,
            String firstName, String lastName, String address1, String address2,
            String city, String state, String postalCode, String country) {

        String data = String.format(validate, API_KEY, url,
                hostAddress, amount, currency, orderId, firstName, lastName,
                address1, address2, city, state, postalCode, country);

        String response = postXML(CamsRetriever.THREE_STEP, data);

        try {
            CamsFormUrlResponse blah = unmarshal(jaxbContext.createUnmarshaller(), CamsFormUrlResponse.class, response);
            return blah;
        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static <T> T unmarshal(Unmarshaller unmarshaller, Class<T> clazz, String xmlLocation)
            throws JAXBException {
        StreamSource xml = new StreamSource(new StringReader(xmlLocation));
        T object = unmarshaller.unmarshal(xml, clazz).getValue();
        return object;
    }

    public CamsOrderResponse stepThree(String tokenId) {
        String data = String.format(completeAction, API_KEY, tokenId);

        String response = postXML(CamsRetriever.THREE_STEP, data);

        try {
            CamsOrderResponse blah = unmarshal(jaxbContext.createUnmarshaller(), CamsOrderResponse.class, response);
            return blah;
        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String postXML(String url, String data) {
        try {
            HttpPost post = new HttpPost(url);
            post.setHeader("Content-type", "text/xml");
            post.setEntity(new StringEntity(data));
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(post);
            String result = IOUtils.toString(response.getEntity().getContent());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String postSalesTransaction(
            UserProfile userProfile, String amount, String orderId, String ipAddress) {
        try {
            HttpPost post = new HttpPost(TRANSACTIONS);
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(Arrays.asList(
                    new BasicNameValuePair("type", "sale"),
                    new BasicNameValuePair("username", "adminruckusdev"),
                    new BasicNameValuePair("password", "ruckus235"),
                    new BasicNameValuePair("transactionid", userProfile.getCamsTokenId()),
                    new BasicNameValuePair("amount", amount.toString()),
                    new BasicNameValuePair("currency", "USD"),
                    new BasicNameValuePair("orderid", orderId),
                    new BasicNameValuePair("orderdescription", "User initiated deposit."),
                    new BasicNameValuePair("ipaddress", ipAddress),
                    new BasicNameValuePair("firstname", userProfile.getUser().getFirstName()),
                    new BasicNameValuePair("lastname", userProfile.getUser().getLastName()),
                    new BasicNameValuePair("address1", userProfile.getAddress1()),
                    new BasicNameValuePair("address2", userProfile.getAddress2()),
                    new BasicNameValuePair("city", userProfile.getCity()),
                    new BasicNameValuePair("state", userProfile.getStateProvince().getAbbreviation()),
                    new BasicNameValuePair("zip", userProfile.getPostalCode()),
                    new BasicNameValuePair("country", userProfile.getCountry().getAbbreviation()),
                    new BasicNameValuePair("email", userProfile.getUser().getEmail()),
                    new BasicNameValuePair("merchant_defined_field_3", "ruckus")
            ));
            post.setEntity(entity);
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(post);
            String result = IOUtils.toString(response.getEntity().getContent());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



}
