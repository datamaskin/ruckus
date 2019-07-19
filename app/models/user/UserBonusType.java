package models.user;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mwalsh on 7/19/14.
 */
@Entity
public class UserBonusType {

    public final static String FIRST_DEPOSIT_DATA_CLEAR_RAKE_PERCENTAGE_KEY = "clearRakePercentage";
    public final static String FIRST_DEPOSIT_DATA_MAX_AMOUNT_CENTS = "maxAmountCents";
    private final static Map<String, String> firstDepositData = new HashMap<>();
    private final static Map<String, String> reloadData = new HashMap<>();
    static {
        firstDepositData.put(FIRST_DEPOSIT_DATA_CLEAR_RAKE_PERCENTAGE_KEY, "40");
        firstDepositData.put(FIRST_DEPOSIT_DATA_MAX_AMOUNT_CENTS, "100000");
    }

    public static final UserBonusType FIRST_DEPOSIT = new UserBonusType(
            1, "First deposit", getValueAsString(firstDepositData));
    public static final UserBonusType RELOAD = new UserBonusType(
            2, "Reload", getValueAsString(reloadData));

    public static final String NAME = "name";
    public static final UserBonusType[] ALL = new UserBonusType[]{
            FIRST_DEPOSIT, RELOAD
    };

    private static String getValueAsString(Object object){
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch(Exception e){
            e.printStackTrace();
            throw new IllegalArgumentException("Could not create a UserBonusType from Object", e);
        }
    }

    @Id
    private int id;

    @Column(name = NAME)
    private String name;

    @Column(name = "parameters")
    private String parameters;

    private UserBonusType(int id, String name, String parameters) {
        this.id = id;
        this.name = name;
        this.parameters = parameters;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getParameters() {
        return parameters;
    }
}
