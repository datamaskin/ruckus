package models.sports;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@SuppressWarnings("serial")
public class Team {

    public static final String TEAM_ID = "id";
    public static final String LEAGUE_ID = "league_id";
    public static final String STAT_PROVIDER_ID = "stat_provider_id";
    public static final String LOCATION = "location";
    private static final String NAME = "name";
    private static final String ABBREVIATION = "abbreviation";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = TEAM_ID)
    private int id;

    @Column(nullable = false, name = STAT_PROVIDER_ID, unique = true)
    private int statProviderId;

    @ManyToOne
    @Column(name = LEAGUE_ID)
    @JsonIgnore
    private League league;

    @Column(nullable = false, name = LOCATION)
    private String location;

    @Column(nullable = false, name = NAME)
    private String name;

    @Column(nullable = false, name = ABBREVIATION)
    private String abbreviation;

    public Team(League league, String location, String name, String abbreviation, int statProviderId) {
        this.league = league;
        this.location = location;
        this.name = name;
        this.abbreviation = abbreviation;
        this.statProviderId = statProviderId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatProviderId() {
        return statProviderId;
    }

    public void setStatProviderId(int statProviderId) {
        this.statProviderId = statProviderId;
    }

    public League getLeague() {
        return league;
    }

    public void setLeague(League league) {
        this.league = league;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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
}
