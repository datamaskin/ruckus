package models.user;

import org.joda.time.DateTime;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@SuppressWarnings("serial")
public class SecureSocialToken {

    @Id
    private String uuid;
    private DateTime creationTime;
    private String email;
    private DateTime expirationTime;
    private boolean isSignUp;

    public SecureSocialToken(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public DateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(DateTime creationTime) {
        this.creationTime = creationTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public DateTime getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(DateTime expirationTime) {
        this.expirationTime = expirationTime;
    }

    public boolean isSignUp() {
        return isSignUp;
    }

    public void setSignUp(boolean isSignUp) {
        this.isSignUp = isSignUp;
    }

}
