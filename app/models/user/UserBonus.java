package models.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

/**
 * Created by mwalsh on 7/18/14.
 */
@Entity
public class UserBonus {

    private static final String BONUS_TYPE = "bonus_type_id";
    private static final String AMOUNT = "amount";

    @Id
    private int id;

    @Column(name = "created_date")
    private Date createdDate;

    @ManyToOne
    @Column(name = BONUS_TYPE)
    private UserBonusType userBonusType;
    
    @Column(name = AMOUNT)
    private long amount;

    public UserBonus(UserBonusType userBonusType, long amount) {
        this.userBonusType = userBonusType;
        this.amount = amount;
        this.createdDate = new Date();
    }

    public int getId() {
        return id;
    }

    public long getAmount() {
        return amount;
    }

    public UserBonusType getUserBonusType() {
        return userBonusType;
    }

}
