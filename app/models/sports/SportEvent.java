package models.sports;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@SuppressWarnings("serial")
public class SportEvent {

    public static final String ID = "id";
    public static final String STAT_PROVIDER_ID = "stat_provider_id";
    public static final String LEAGUE_ID = "league_id";
    public static final String START_TIME = "start_time";
    public static final String DESCRIPTION = "description";
    public static final String SHORT_DESCRIPTION = "short_description";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = ID)
    private int id;

    @Column(nullable = false, unique = true, name = STAT_PROVIDER_ID)
    private int statProviderId;

    @ManyToOne(cascade = CascadeType.DETACH)
    @Column(nullable = false, name = LEAGUE_ID)
    private League league;

    @Column(nullable = false, name = START_TIME)
    private Date startTime;

    @Column(nullable = false, name = SHORT_DESCRIPTION)
    private String shortDescription;

    @Column(nullable = false, name = DESCRIPTION)
    private String description;

    @Column(nullable = false)
    private int unitsRemaining;

    private boolean complete;
    private int week = -1;
    private int season = -1;
    private int eventTypeId = -1;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "sport_event_x_team",
            joinColumns = @JoinColumn(name = "sport_event_id", referencedColumnName = SportEvent.ID),
            inverseJoinColumns = @JoinColumn(name = "team_id", referencedColumnName = Team.TEAM_ID))
    private List<Team> teams;

    public SportEvent(int statProviderId, League league, Date startTime,
                      String description, String shortDescription,
                      int unitsRemaining, boolean complete, int season, int week, int eventTypeId) {
        this.shortDescription = shortDescription;
        this.statProviderId = statProviderId;
        this.league = league;
        this.startTime = startTime;
        this.description = description;
        this.unitsRemaining = unitsRemaining;
        this.complete = complete;
        this.week = week;
        this.season = season;
        this.eventTypeId = eventTypeId;
    }

    public SportEvent(SportEvent update) {
        this.shortDescription = update.shortDescription;
        this.statProviderId = update.statProviderId;
        this.league = update.league;
        this.startTime = update.startTime;
        this.description = update.description;
        this.unitsRemaining = update.unitsRemaining;
        this.complete = update.complete;
        this.week = update.week;
        this.season = update.season;
        this.eventTypeId = update.eventTypeId;
        this.teams = update.getTeams();
    }

    public void update(SportEvent update) {
        this.shortDescription = update.shortDescription;
        this.statProviderId = update.statProviderId;
        this.league = update.league;
        this.startTime = update.startTime;
        this.description = update.description;
        this.unitsRemaining = update.unitsRemaining;
        this.complete = update.complete;
        this.week = update.week;
        this.season = update.season;
        this.eventTypeId = update.eventTypeId;
        this.teams = update.getTeams();
    }

    public int getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(int eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getStatProviderId() {
        return statProviderId;
    }

    public void setStatProviderId(int statProviderId) {
        this.statProviderId = statProviderId;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public League getLeague() {
        return league;
    }

    public void setLeague(League league) {
        this.league = league;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUnitsRemaining() {
        return unitsRemaining;
    }

    public void setUnitsRemaining(int unitsRemaining) {
        this.unitsRemaining = unitsRemaining;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public Team getOpponent(Team team) {
        Team opponent = team;
        for (Team t : this.getTeams()) {
            if (t.getStatProviderId() != team.getStatProviderId()) {
                opponent = t;
            }
        }
        return opponent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SportEvent that = (SportEvent) o;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + id;
        return result;
    }
}
