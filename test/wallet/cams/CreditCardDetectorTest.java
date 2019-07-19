package wallet.cams;

import org.junit.Test;
import wallet.CreditCardDetector;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by mwalsh on 8/1/14.
 */
public class CreditCardDetectorTest {

    @Test
    public void test(){
        Map<CreditCardDetector.CreditCardType, String> cards = new HashMap<>();
        cards.put(CreditCardDetector.CreditCardType.VISA, "4111111111111111");
        cards.put(CreditCardDetector.CreditCardType.MASTERCARD, "5424000000000015");
        cards.put(CreditCardDetector.CreditCardType.AMEX, "370000000000002");
        cards.put(CreditCardDetector.CreditCardType.DISCOVER, "6011000000000012");
        cards.put(CreditCardDetector.CreditCardType.JCB, "3528288605211810");
        cards.put(CreditCardDetector.CreditCardType.DINERS, "38520000023237");

        CreditCardDetector detector = new CreditCardDetector();

        for(Map.Entry<CreditCardDetector.CreditCardType, String> entry: cards.entrySet()){
            assertEquals(entry.getKey(), detector.getCreditCardType(entry.getValue()));
            System.out.println(entry.getValue() + " ~~~> " + entry.getKey());
        }
        DecimalFormat fmt = new DecimalFormat("#.00");
        System.out.println(fmt.format(21));
        System.out.println(fmt.format(21.00));
        System.out.println(fmt.format(21.40));
        System.out.println(fmt.format(21.12));

    }

}
