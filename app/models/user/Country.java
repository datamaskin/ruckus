package models.user;

import com.avaje.ebean.annotation.EnumValue;

/**
 * Created by mwalsh on 7/29/14.
 */
public enum Country {

    @EnumValue("US") US("US", "United States of America"),
    @EnumValue("CA") CA("CA", "Canada");

    private final String abbreviation;
    private final String name;

    private Country(String abbreviation, String name) {
        this.abbreviation = abbreviation;
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + "-" + abbreviation;
    }
}
