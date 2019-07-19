package models.wallet;

import models.user.User;
import models.user.UserBonus;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mwalsh on 7/18/14.
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "user_id"))
public class UserWallet {

    public static final String USER_ID = "user_id";
    public static final String usd_currency = "usd_currency";
    public static final String loyalty_points = "loyalty_points";

    @Id
    private int id;

    @OneToOne
    @Column(name = USER_ID, unique = true, nullable = false)
    private User user;

    @Column(name = usd_currency)
    private long usd;

    @Column(name = loyalty_points)
    private long loyaltyPoints;

    @OneToMany(cascade = CascadeType.PERSIST)
    private List<UserBonus> userBonuses;

    public UserWallet(User user) {
        this.user = user;
        this.usd = 0;
        this.loyaltyPoints = 0;
        this.userBonuses = new ArrayList<>();
    }

    public void updateUsd(long amount){
        if(amount < 0){
            throw new IllegalArgumentException("USD amount CANNOT be less than zero. Ever.");
        }
        usd = amount;
    }

    public void addUserBonus(UserBonus userBonus) {
        if(userBonuses == null){
            userBonuses = new ArrayList<>();
        }
        userBonuses.add(userBonus);
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public long getUsd() {
        return usd;
    }

    public long getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public List<UserBonus> getUserBonuses() {
        return userBonuses;
    }
}
