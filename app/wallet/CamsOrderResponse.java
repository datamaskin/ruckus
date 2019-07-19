package wallet;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by mwalsh on 7/29/14.
 */
@XmlRootElement(name = "response")
public class CamsOrderResponse {

    @XmlRootElement(name = "billing")
    public static class CamsOrderResponseBilling {
        @XmlElement(name = "first-name")
        private String firstName;
        @XmlElement(name = "last-name")
        private String lastName;
        @XmlElement(name = "address1")
        private String address1;
        @XmlElement(name = "address2")
        private String address2;
        @XmlElement(name = "city")
        private String city;
        @XmlElement(name = "state")
        private String state;
        @XmlElement(name = "postal")
        private String postal;
        @XmlElement(name = "country")
        private String country;
        @XmlElement(name = "cc-number")
        private String ccNumber;
        @XmlElement(name = "cc-exp")
        private String ccExp;

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getAddress1() {
            return address1;
        }

        public String getAddress2() {
            return address2;
        }

        public String getCity() {
            return city;
        }

        public String getState() {
            return state;
        }

        public String getPostal() {
            return postal;
        }

        public String getCountry() {
            return country;
        }

        public String getCcNumber() {
            return ccNumber;
        }

        public String getCcExp() {
            return ccExp;
        }
    }

    @XmlElement(name = "result")
    private Integer result;
    @XmlElement(name = "result-text")
    private String resultText;
    @XmlElement(name = "transaction-id")
    private String transactionId;
    @XmlElement(name = "result-code")
    private Integer resultCode;
    @XmlElement(name = "action-type")
    private String actionType;
    @XmlElement(name = "amount")
    private Float amount;
    @XmlElement(name = "tip-amount")
    private Float tipAmount;
    @XmlElement(name = "surcharge-amount")
    private Float surchargeAmount;
    @XmlElement(name = "ip-address")
    private String ipAddress;
    @XmlElement(name = "industry")
    private String industry;
    @XmlElement(name = "processor-id")
    private String processorId;
    @XmlElement(name = "currency")
    private String currency;
    @XmlElement(name = "merchant-defined-field-12")
    private String merchantDefinedField12;
    @XmlElement(name = "order-id")
    private String orderId;
    @XmlElement(name = "tax-amount")
    private Float taxAmount;
    @XmlElement(name = "shipping-amount")
    private Float shippingAmount;
    @XmlElement(name = "billing")
    private CamsOrderResponseBilling camsOrderResponseBilling;

    public Integer getResult() {
        return result;
    }

    public String getResultText() {
        return resultText;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public String getActionType() {
        return actionType;
    }

    public Float getAmount() {
        return amount;
    }

    public Float getTipAmount() {
        return tipAmount;
    }

    public Float getSurchargeAmount() {
        return surchargeAmount;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getIndustry() {
        return industry;
    }

    public String getProcessorId() {
        return processorId;
    }

    public String getCurrency() {
        return currency;
    }

    public String getMerchantDefinedField12() {
        return merchantDefinedField12;
    }

    public String getOrderId() {
        return orderId;
    }

    public Float getTaxAmount() {
        return taxAmount;
    }

    public Float getShippingAmount() {
        return shippingAmount;
    }

    public CamsOrderResponseBilling getCamsOrderResponseBilling() {
        return camsOrderResponseBilling;
    }

}
