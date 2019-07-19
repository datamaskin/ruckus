package models.sports;

import org.json.JSONObject;

import javax.persistence.*;
import java.util.List;

@Entity
public class Athlete {

    public static final String ATHLETE_ID = "id";
    public static final String TEAM_ID = "team_id";
    public static final String STAT_PROVIDER_ID = "stat_provider_id";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ATHLETE_ID)
    private int id;

    @Column(nullable = false, name = STAT_PROVIDER_ID, unique = true)
    private int statProviderId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @ManyToOne
    @Column(name = TEAM_ID)
    private Team team;

    @Column(nullable = false)
    private String uniform;

    @Column(nullable = false)
    private String injuryStatus;

    @Column(nullable = false)
    private boolean active = true;

    @ManyToMany
    @JoinTable(name = "athlete_x_position",
            joinColumns = @JoinColumn(name = "athlete_id", referencedColumnName = Athlete.ATHLETE_ID),
            inverseJoinColumns = @JoinColumn(name = "position_id", referencedColumnName = Position.POSITION_ID))
    private List<Position> positions;

    private JSONObject statsSeason;
    private JSONObject statsCareer;
    private JSONObject statsCurrentGame;
    private float fppSeason;
    private float fppCareer;
    private float fppCurrentGame;
    private int depth = 0;

    public Athlete(int statProviderId, String firstName, String lastName, Team team, String uniform) {
        this.statProviderId = statProviderId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.team = team;
        this.uniform = uniform;
        this.injuryStatus = "";
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getStatProviderId() {
        return statProviderId;
    }

    public void setStatProviderId(int statProviderId) {
        this.statProviderId = statProviderId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public String getUniform() {
        return uniform;
    }

    public void setUniform(String uniform) {
        this.uniform = uniform;
    }

    public String getInjuryStatus() {
        return injuryStatus;
    }

    public void setInjuryStatus(String injuryStatus) {
        this.injuryStatus = injuryStatus == null ? "" : injuryStatus;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public void setPositions(List<Position> positions) {
        this.positions = positions;
    }

    public JSONObject getStatsSeason() {
        return statsSeason;
    }

    public void setStatsSeason(JSONObject statsSeason) {
        this.statsSeason = statsSeason;
    }

    public JSONObject getStatsCareer() {
        return statsCareer;
    }

    public void setStatsCareer(JSONObject statsCareer) {
        this.statsCareer = statsCareer;
    }

    public JSONObject getStatsCurrentGame() {
        return statsCurrentGame;
    }

    public void setStatsCurrentGame(JSONObject statsCurrentGame) {
        this.statsCurrentGame = statsCurrentGame;
    }

    public float getFppSeason() {
        return fppSeason;
    }

    public void setFppSeason(float fppSeason) {
        this.fppSeason = fppSeason;
    }

    public float getFppCareer() {
        return fppCareer;
    }

    public void setFppCareer(float fppCareer) {
        this.fppCareer = fppCareer;
    }

    public float getFppCurrentGame() {
        return fppCurrentGame;
    }

    public void setFppCurrentGame(float fppCurrentGame) {
        this.fppCurrentGame = fppCurrentGame;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        Athlete other = (Athlete) obj;
        if (id != other.id)
            return false;
        return true;
    }

}
