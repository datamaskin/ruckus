package models.user;

import models.business.AffiliateCode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;


/** Encapsulates a single user in the system. */
@Entity
@SuppressWarnings("serial")
public class User implements Serializable {

    public static final String ID = "id";
    public static final String EMAIL = "email";
    private static final long serialVersionUID = 5639886803571424134L;

    @Version
    private Date version;

    @Id
    private Long id;

    @Column(unique = true, name = EMAIL)
    private String email;

    @Column(unique = true)
    private String userName;

    private String providerId;
    private String firstName;
    private String lastName;
    private String password;
    private Date verified;

    @Column(name="date_of_birth", nullable = true)
    private Date dateOfBirth;

    /** Holds the affiliate code that a user used to sign up. */
    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "affiliate_code_id")
    private AffiliateCode affiliateCode;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "user_x_role",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = User.ID),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = UserRole.ID))
    private List<UserRole> userRoles;

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getVersion() {
        return version;
    }

    public void setVersion(Date version) {
        this.version = version;
    }

    public List<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    public Date getVerified() {
        return verified;
    }

    public void setVerified(Date verified) {
        this.verified = verified;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /** The affiliate code that a user used to sign up. */
    public AffiliateCode getAffiliateCode() {
        return affiliateCode;
    }

    /** @see #getAffiliateCode() */
    public void setAffiliateCode(AffiliateCode affiliateCode) {
        this.affiliateCode = affiliateCode;
    }
}
