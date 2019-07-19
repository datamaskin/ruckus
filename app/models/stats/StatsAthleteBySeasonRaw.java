package models.stats;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;
import java.sql.Timestamp;

/**
 * Created by mgiles on 8/2/14.
 */
@Entity
public class StatsAthleteBySeasonRaw {
    public static final String UNIQUE_KEY = "unique_key";

    @Id
    private int id;

    @Version
    private Timestamp lastUpdate;

    private Timestamp lastFetched;

    @Column(columnDefinition = "LONGTEXT")
    private String rawData;

    private int statsAthleteId;

    @Column(unique = true)
    private String uniqueKey;

    private boolean previouslyFailed = false;
    private int season;
    private int eventTypeId;

    public String getUniqueKey() {
        uniqueKey = statsAthleteId + "_" + season + "_" + eventTypeId;
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public Timestamp getLastFetched() {
        return lastFetched;
    }

    public void setLastFetched(Timestamp lastFetched) {
        this.lastFetched = lastFetched;
    }

    public int getStatsAthleteId() {
        return statsAthleteId;
    }

    public void setStatsAthleteId(int statsAthleteId) {
        this.statsAthleteId = statsAthleteId;
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

    public String getRawData() {
        return rawData;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }

    public boolean isPreviouslyFailed() {
        return previouslyFailed;
    }

    public void setPreviouslyFailed(boolean previouslyFailed) {
        this.previouslyFailed = previouslyFailed;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
