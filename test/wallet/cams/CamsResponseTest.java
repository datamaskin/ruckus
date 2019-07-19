package wallet.cams;

import org.junit.Test;

/**
 * Created by mwalsh on 8/1/14.
 */
public class CamsResponseTest {

    @Test
    public void test(){
        String response = "response=2&responsetext=Configuration Error - No Merchant Account&authcode=&transactionid=2354135527&avsresponse=&cvvresponse=&orderid=2a857477-b939-406a-bdbf-59393ccf0a54&type=sale&response_code=823&merchant_defined_field_14=&merchant_defined_field_15=&merchant_defined_field_16=&merchant_defined_field_17=&merchant_defined_field_19=";

        for(String var: response.split("&")){
            String[] kv = var.split("=");
            System.out.println(kv[0]+" >>> "+(kv.length < 2 ? "" : kv[1]));
        }

    }

}
