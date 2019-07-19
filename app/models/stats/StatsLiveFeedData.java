package models.stats;

import models.sports.SportEvent;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Model for a single message coming from the Stats feed.
 */
@Entity
public class StatsLiveFeedData {
    @Id
    private int id;

    @ManyToOne
    private SportEvent sportEvent;

    @Column(columnDefinition = "longtext not null")
    private String data;

    @Column(columnDefinition = "varchar(32) not null")
    private String dataHash;

    public StatsLiveFeedData(SportEvent sportEvent, String data, String dataHash) {
        this.sportEvent = sportEvent;
        this.data = data;
        this.dataHash = dataHash;
    }

    public String getDataHash() {
        return dataHash;
    }

    public void setDataHash(String dataHash) {
        this.dataHash = dataHash;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SportEvent getSportEvent() {
        return sportEvent;
    }

    public void setSportEvent(SportEvent sportEvent) {
        this.sportEvent = sportEvent;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
