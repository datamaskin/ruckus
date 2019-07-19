package models.user;

import audit.IAuditable;
import com.avaje.ebean.annotation.EnumValue;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

/**
 * Created by mwalsh on 8/1/14.
 */
@Entity
public class UserAction implements IAuditable {

    public enum Type {
        @EnumValue("LOGIN") LOGIN,
        @EnumValue("LOGOUT") LOGOUT,
        @EnumValue("PW_CHANGE") PASSWORD_CHANGE,
        @EnumValue("PW_RESET") PASSWORD_RESET,
        @EnumValue("SIGN_UP") SIGN_UP
    }

    @Id
    private Long id;

    @Column(name = "user_id")
    @ManyToOne
    private User user;

    @Column(name = "start_timestamp")
    private Date startTimestamp;

    @Column(name = "type")
    private Type type;

    @Column(name = "data")
    private String data;

    public UserAction(User user, Type type, String data) {
        this.user = user;
        this.startTimestamp = new Date();
        this.type = type;
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Date getStartTimestamp() {
        return startTimestamp;
    }

    public Type getType() {
        return type;
    }

    public String getData() {
        return data;
    }
}
