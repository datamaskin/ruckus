package models.sports;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@SuppressWarnings("serial")
public class League {

    public static final League MLB = new League(1, Sport.BASEBALL, "Major League Baseball", "MLB", "Major League Baseball", false);
    public static final League NFL = new League(2, Sport.FOOTBALL, "National Football League", "NFL", "National Football League", true);
    public static final League[] ALL_LEAGUES = new League[]{MLB, NFL};
    public static final String ABBREVIATION = "abbreviation";
    public static final String IS_ACTIVE = "is_active";
    private static final String SPORT_ID = "sport_id";
    private static final String NAME = "name";
    private static final String DISPLAY_NAME = "display_name";

    @Id
    private int id;

    @ManyToOne
    @Column(nullable = false, name = SPORT_ID)
    private Sport sport;

    @Column(nullable = false, name = NAME)
    private String name;

    @Column(nullable = false, name = ABBREVIATION)
    @JsonProperty("abbreviation")
    private String abbreviation;

    @Column(nullable = false, name = DISPLAY_NAME)
    private String displayName;

    @Column(nullable = false, name = IS_ACTIVE)
    @JsonProperty("active")
    private boolean isActive;

    public League(Sport sport, String name, String abbreviation, String displayName, boolean isActive) {
        this.sport = sport;
        this.name = name;
        this.abbreviation = abbreviation;
        this.displayName = displayName;
        this.isActive = isActive;
    }

    public League(int id, Sport sport, String name, String abbreviation, String displayName, boolean isActive) {
        this.id = id;
        this.sport = sport;
        this.name = name;
        this.abbreviation = abbreviation;
        this.displayName = displayName;
        this.isActive = isActive;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Sport getSport() {
        return sport;
    }

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        League league = (League) o;

        if (id != league.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }
}