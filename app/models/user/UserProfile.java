package models.user;

import wallet.CreditCardDetector;

import javax.persistence.*;
import java.util.Arrays;

/**
 * Created by mwalsh on 7/29/14.
 */
@Entity
public class UserProfile {

    @Id
    private int id;
    @ManyToOne
    @Column(name = "user_id")
    private User user;

    @Column(name = "address1")
    private String address1;

    @Column(name = "address2")
    private String address2;

    @Column(name = "city")
    private String city;

    @Column(name = "state_province")
    private StateProvince stateProvince;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "country")
    private Country country;

    @Column(name = "active")
    private boolean active;

    @Column(name = "cams_token_id")
    private String camsTokenId;

    @Column(name = "cc_type")
    private CreditCardDetector.CreditCardType ccType;

    @Column(name = "cc_number")
    private String ccNumber;

    @Column(name = "cc_exp_month")
    private Integer ccExpMonth;

    @Column(name = "cc_exp_year")
    private Integer ccExpYear;

    public UserProfile(User user, String address1, String address2,
                       String city, StateProvince stateProvince, String postalCode) {

        if(user == null || user.getId() == null){
            throw new IllegalArgumentException("User cannot be null.");
        }

        this.user = user;
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.stateProvince = stateProvince;
        this.postalCode = postalCode;

        if(Arrays.asList(StateProvince.ALL_US).contains(stateProvince)){
            this.country = Country.US;
        } else if (Arrays.asList(StateProvince.ALL_CANADA).contains(stateProvince)){
            this.country = Country.CA;
        } else {
            throw new IllegalArgumentException("Now aware of stateProvince " + stateProvince);
        }

        active = true;
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
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

    public StateProvince getStateProvince() {
        return stateProvince;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public Country getCountry() {
        return country;
    }

    public String getCamsTokenId() {
        return camsTokenId;
    }

    public void setCamsTokenId(String camsTokenId) {
        this.camsTokenId = camsTokenId;
    }

    public String getCcNumber() {
        return ccNumber;
    }

    public void setCcNumber(String ccNumber) {
        this.ccNumber = ccNumber;
    }

    public Integer getCcExpMonth() {
        return ccExpMonth;
    }

    public void setCcExpMonth(Integer ccExpMonth) {
        this.ccExpMonth = ccExpMonth;
    }

    public Integer getCcExpYear() {
        return ccExpYear;
    }

    public void setCcExpYear(Integer ccExpYear) {
        this.ccExpYear = ccExpYear;
    }

    public CreditCardDetector.CreditCardType getCcType() {
        return ccType;
    }

    public void setCcType(CreditCardDetector.CreditCardType ccType) {
        this.ccType = ccType;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
