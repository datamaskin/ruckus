package wallet;

import java.util.regex.Pattern;

/**
 * Created by mwalsh on 8/1/14.
 */
public class CreditCardDetector {

    public enum CreditCardType {
        VISA("^4[0-9]{6,}$"),
        MASTERCARD("^5[1-5][0-9]{5,}$"),
        AMEX("^3[47][0-9]{5,}$"),
        DISCOVER("^6(?:011|5[0-9]{2})[0-9]{3,}$"),
        DINERS("^3(?:0[0-5]|[68][0-9])[0-9]{4,}$"),
        JCB("^(?:2131|1800|35[0-9]{3})[0-9]{3,}$");

        private Pattern pattern;

        CreditCardType(String pattern) {
            this.pattern = Pattern.compile(pattern);
        }

        public Pattern getPattern() {
            return pattern;
        }
    }

    public CreditCardType getCreditCardType(String number){
        if (null == number || number.length() < 12)
            return null;

        for(CreditCardType card: CreditCardType.values()){
            if(card.getPattern().matcher(number).matches()){
                return card;
            }
        }

        return null;
    }

}
