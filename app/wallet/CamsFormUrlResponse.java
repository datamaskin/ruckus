package wallet;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by mwalsh on 7/29/14.
 */
@XmlRootElement(name = "response")
public class CamsFormUrlResponse {

    @XmlElement(name = "result")
    private Integer result;
    @XmlElement(name = "result-text")
    private String resultText;
    @XmlElement(name = "transaction-id")
    private String transactionId;
    @XmlElement(name = "result-code")
    private Integer resultCode;
    @XmlElement(name = "form-url")
    private String formUrl;

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

    public String getFormUrl() {
        return formUrl;
    }
}
