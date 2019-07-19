package models.stats.mlb;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by mwalsh on 7/5/14.
 */
@Entity
public class StatsMlbFielding {
    @Id
    private int id;
    private int statProviderId;
    private String position;
    private Integer assists;
    private Integer ballsHitInZone;
    private Integer doublePlays;
    private Integer errors;
    private Integer fieldingOuts;
    private Integer hitsAllowed;
    private Float innings;
    private Integer opportunities;
    private Integer putOuts;
    private Integer triplePlays;

    public Integer getTriplePlays() {
        return triplePlays;
    }

    public void setTriplePlays(Integer triplePlays) {
        this.triplePlays = triplePlays;
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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Integer getAssists() {
        return assists;
    }

    public void setAssists(Integer assists) {
        this.assists = assists;
    }

    public Integer getBallsHitInZone() {
        return ballsHitInZone;
    }

    public void setBallsHitInZone(Integer ballsHitInZone) {
        this.ballsHitInZone = ballsHitInZone;
    }

    public Integer getDoublePlays() {
        return doublePlays;
    }

    public void setDoublePlays(Integer doublePlays) {
        this.doublePlays = doublePlays;
    }

    public Integer getErrors() {
        return errors;
    }

    public void setErrors(Integer errors) {
        this.errors = errors;
    }

    public Integer getFieldingOuts() {
        return fieldingOuts;
    }

    public void setFieldingOuts(Integer fieldingOuts) {
        this.fieldingOuts = fieldingOuts;
    }

    public Integer getHitsAllowed() {
        return hitsAllowed;
    }

    public void setHitsAllowed(Integer hitsAllowed) {
        this.hitsAllowed = hitsAllowed;
    }

    public Float getInnings() {
        return innings;
    }

    public void setInnings(Float innings) {
        this.innings = innings;
    }

    public Integer getOpportunities() {
        return opportunities;
    }

    public void setOpportunities(Integer opportunities) {
        this.opportunities = opportunities;
    }

    public Integer getPutOuts() {
        return putOuts;
    }

    public void setPutOuts(Integer putOuts) {
        this.putOuts = putOuts;
    }
}
