package models.stats.predictive;

import models.sports.AthleteSportEventInfo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * Created by dmaclean on 7/11/14.
 */
@Entity
public class StatsProjection {
    @Id
    private int id;
    @OneToOne
    private AthleteSportEventInfo athleteSportEventInfo;
    private double projection;
    private double projectionMod;

    public StatsProjection() {
    }

    public StatsProjection(AthleteSportEventInfo athleteSportEventInfo, double projection, double projectionMod) {
        this.athleteSportEventInfo = athleteSportEventInfo;
        this.projection = projection;
        this.projectionMod = projectionMod;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AthleteSportEventInfo getAthleteSportEventInfo() {
        return athleteSportEventInfo;
    }

    public void setAthleteSportEventInfo(AthleteSportEventInfo athleteSportEventInfo) {
        this.athleteSportEventInfo = athleteSportEventInfo;
    }

    public double getProjection() {
        return projection;
    }

    public void setProjection(double projection) {
        this.projection = projection;
    }

    public double getProjectionMod() {
        return projectionMod;
    }

    public void setProjectionMod(double projectionMod) {
        this.projectionMod = projectionMod;
    }
}
