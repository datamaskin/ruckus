package models.stats;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.S3Link;

/**
 * Created by mgiles on 8/12/14.
 */
@DynamoDBTable(tableName = "StatsAthleteBySeasonRaw")
public class DynamoStatsAthleteBySeasonRaw {
    private String id;
    private S3Link s3Link;
    private String lastUpdate;
    private String lastFetched;
    private int statsAthleteId;
    private boolean previouslyFailed = false;
    private int season;
    private int eventTypeId;

    @DynamoDBHashKey(attributeName = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public S3Link getS3Link() {
        return s3Link;
    }

    public void setS3Link(S3Link s3Link) {
        this.s3Link = s3Link;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getLastFetched() {
        return lastFetched;
    }

    public void setLastFetched(String lastFetched) {
        this.lastFetched = lastFetched;
    }

    public int getStatsAthleteId() {
        return statsAthleteId;
    }

    public void setStatsAthleteId(int statsAthleteId) {
        this.statsAthleteId = statsAthleteId;
    }

    public boolean isPreviouslyFailed() {
        return previouslyFailed;
    }

    public void setPreviouslyFailed(boolean previouslyFailed) {
        this.previouslyFailed = previouslyFailed;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public int getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(int eventTypeId) {
        this.eventTypeId = eventTypeId;
    }
}
